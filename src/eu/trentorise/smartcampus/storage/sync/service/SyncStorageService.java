/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.storage.sync.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import eu.trentorise.smartcampus.protocolcarrier.common.Status;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.SyncData;
import eu.trentorise.smartcampus.storage.sync.SyncStorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.SyncStorageHelper;
import eu.trentorise.smartcampus.storage.sync.Utils;

/**
 * Default implementation of the synchronization service.
 * May be used by different apps simultaneously. For each app, a corresponding 
 * {@link SyncStorageTaskManager} instance is associated that handle the lifecycle of siynchronization
 * for that specific app. 
 * <p/>
 * Implements the {@link SyncTaskContext} interface to handle the specific synchronization events, 
 * such as auth token expiration, availability of new updates, and other exceptions. The application
 * should expose this service in AndroidMainfest.xml.
 * @author raman
 *
 */
public class SyncStorageService extends Service implements SyncTaskContext {

//	public static final String ACTION_START_SYNC_STORAGE = "eu.trentorise.smartcampus.storage.sync.START";
//	public static final String ACTION_STOP_SYNC_STORAGE = "eu.trentorise.smartcampus.storage.sync.STOP";
//	public static final String ACTION_SYNC_SYNC_STORAGE = "eu.trentorise.smartcampus.storage.sync.SYNC";

	public static final int MSG_START_SYNC_STORAGE = 0;
	public static final int MSG_STOP_SYNC_STORAGE = 1;
	public static final int MSG_SYNC_SYNC_STORAGE = 2;
	
	
	public static final String KEY_APP_TOKEN = "eu.trentorise.smartcampus.storage.sync.APP_TOKEN";
	public static final String KEY_AUTH_TOKEN = "eu.trentorise.smartcampus.storage.sync.AUTH_TOKEN";

	public static final String ACTION_AUTHENTICATION_PROBLEM = "eu.trentorise.smartcampus.storage.sync.AUTHENTICATION_PROBLEM";
	public static final String ACTION_PROTOCOL_PROBLEM = "eu.trentorise.smartcampus.storage.sync.PROTOCOL_PROBLEM";

	
	public static final String KEY_STORAGE_CONFIG = "eu.trentorise.smartcampus.storage.sync.STORAGE_CONFIG";

	private Status status;
	private BroadcastReceiver mConnReceiver;
//	private BroadcastReceiver mActionReceiver;
	
	private Map<String, SyncStorageTaskManager> storageTaskManagerMap = new HashMap<String, SyncStorageTaskManager>();

    final Messenger mMessenger = new Messenger(new MessageHandler());


	@Override
	public IBinder onBind(Intent arg0) {
		return mMessenger.getBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		status = new Status(getApplicationContext());

		/*
		 * ConnectivityManager Broadcast Receiver
		 */
		mConnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				if (status.isOnline() && noConnectivity) {
					// online --> offline
					status.setOnline(false);
					for (Entry<String, SyncStorageTaskManager> entry : storageTaskManagerMap.entrySet()) {
						SyncStorageTaskManager stm = entry.getValue();
						stm.setOffline();
					}
					Log.d(this.getClass().getName(), "Going offline, sync tasks stopped");
				} else if (!status.isOnline() && !noConnectivity) {
					// offline --> online
					status.setOnline(true);
					for (Entry<String, SyncStorageTaskManager> entry : storageTaskManagerMap.entrySet()) {
						SyncStorageTaskManager stm = entry.getValue();
						stm.setOnline();
					}	
					Log.d(this.getClass().getName(), "Going online, sync tasks started");
				}
			}
		};

		registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		for (Entry<String, SyncStorageTaskManager> entry : storageTaskManagerMap.entrySet()) {
			SyncStorageTaskManager stm = entry.getValue();
			stm.stop();
		}
		storageTaskManagerMap.clear();
		
		unregisterReceiver(mConnReceiver);
//		unregisterReceiver(mActionReceiver);
	}

	
	
	@Override
	public SyncStorageHelper getSyncStorageHelper(String appToken, StorageConfiguration config) {
		return new SyncStorageHelper(this, Utils.getDBName(this, appToken), Utils.getDBVersion(this, appToken), config);
	}


	@Override
	public void onDBUpdate(SyncData data, String appToken) {
	}


	@Override
	public void handleSecurityProblem(String appToken) {
		Intent broadcast = new Intent(SyncStorageService.ACTION_AUTHENTICATION_PROBLEM);
		broadcast.putExtra(SyncStorageService.KEY_APP_TOKEN, appToken);
		sendBroadcast(broadcast);
	}


	@Override
	public void handleSyncException(String appToken) {
		sendBroadcast(new Intent(SyncStorageService.ACTION_PROTOCOL_PROBLEM));
		
	}



	class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			String appToken = msg.getData().getString(KEY_APP_TOKEN);
			String authToken = msg.getData().getString(KEY_AUTH_TOKEN);
			switch (msg.what) {
				case MSG_START_SYNC_STORAGE:
					if (appToken != null && authToken != null) {
						// start
						SyncStorageTaskManager stm = storageTaskManagerMap.get(appToken);
						SyncStorageConfiguration config = (SyncStorageConfiguration)msg.getData().getSerializable(KEY_STORAGE_CONFIG);
						if (stm == null) {
							stm = new SyncStorageTaskManager(SyncStorageService.this, config, appToken, authToken, SyncStorageService.this);
							storageTaskManagerMap.put(appToken, stm);
							stm.start();
							Log.d(this.getClass().getName(), "SyncStorageTaskManager for " + appToken + " started >");
						} else {
							stm.reset(config, authToken);
							Log.d(this.getClass().getName(), "SyncStorageTaskManager for " + appToken + " already started ~");
						}
					}
					break;
				case MSG_SYNC_SYNC_STORAGE:
					if (appToken != null && status.isOnline()) {
						// force synchronization	
						SyncStorageConfiguration config = (SyncStorageConfiguration)msg.getData().getSerializable(KEY_STORAGE_CONFIG);
						SyncStorageTaskManager stm = storageTaskManagerMap.get(appToken);
						if (stm == null && config != null && authToken != null) {
							stm = new SyncStorageTaskManager(SyncStorageService.this, config, appToken, authToken, SyncStorageService.this);
							storageTaskManagerMap.put(appToken, stm);
						}
						if (stm != null) {
							stm.reset(config, authToken);
							stm.forceSync();
						}
					}
					break;
				case MSG_STOP_SYNC_STORAGE:
					if (appToken != null) {
						SyncStorageTaskManager stm = storageTaskManagerMap.get(appToken);
						// stop
						if (stm != null) {
							stm.stop();
							storageTaskManagerMap.remove(appToken);
							Log.d(this.getClass().getName(), "SyncStorageTaskManager for " + appToken + " stopped []");
							if (storageTaskManagerMap.isEmpty()) {
								stopSelf();
							}
						}
					}
			}
		}
		
	}
	
}
