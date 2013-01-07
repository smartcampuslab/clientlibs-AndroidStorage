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
package eu.trentorise.smartcampus.storage.sync;

import java.util.LinkedList;
import java.util.Queue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.sync.service.SyncStorageService;

public class SyncManager implements ISyncManager {

	private Context mContext = null;
	private Messenger mService = null;
	private boolean bound = false;
	
	private Queue<Message> messageQueue = new LinkedList<Message>();
	
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            if (!messageQueue.isEmpty()) {
            	for (Message m : messageQueue) {
            		try {
						mService.send(m);
					} catch (RemoteException e) {
						Log.e(this.getClass().getName(), "Failed to dispatch message: "+e.getMessage());
					}
            	}
            }
        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

	public SyncManager(Context mContext, Class<? extends SyncStorageService> cls) {
		super();
		this.mContext = mContext;
		mContext.bindService(new Intent(mContext,cls), mConnection, Context.BIND_AUTO_CREATE);
		bound = true;
	}
	
	public SyncManager(Context mContext) {
		super();
		this.mContext = mContext;
		mContext.bindService(new Intent(mContext,SyncStorageService.class), mConnection, Context.BIND_AUTO_CREATE);
		bound = true;
	}

	@Override
	public void synchronize(String authToken, String appToken) throws DataException, StorageConfigurationException, RemoteException {
		Message msg = Message.obtain(null, SyncStorageService.MSG_SYNC_SYNC_STORAGE, 0, 0);
		Bundle data = new Bundle();
		data.putString(SyncStorageService.KEY_APP_TOKEN, appToken);
		data.putString(SyncStorageService.KEY_AUTH_TOKEN, authToken);
		msg.setData(data);
		if (mService != null) {
			mService.send(msg);
		} else {
			messageQueue.add(msg);
		}
//		Intent broadcast = new Intent(SyncStorageService.ACTION_SYNC_SYNC_STORAGE);
//		broadcast.putExtra(SyncStorageService.KEY_APP_TOKEN, appToken);
//		broadcast.putExtra(SyncStorageService.KEY_AUTH_TOKEN, authToken);
//		mContext.sendBroadcast(broadcast);
	}

	@Override
	public void synchronize(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException {
		Message msg = Message.obtain(null, SyncStorageService.MSG_SYNC_SYNC_STORAGE, 0, 0);
		Bundle data = new Bundle();
		data.putString(SyncStorageService.KEY_APP_TOKEN, appToken);
		data.putString(SyncStorageService.KEY_AUTH_TOKEN, authToken);
		data.putSerializable(SyncStorageService.KEY_STORAGE_CONFIG, config);
		msg.setData(data);
		if (mService != null) {
			mService.send(msg);
		} else {
			messageQueue.add(msg);
		}
//		Intent broadcast = new Intent(SyncStorageService.ACTION_SYNC_SYNC_STORAGE);
//		broadcast.putExtra(SyncStorageService.KEY_APP_TOKEN, appToken);
//		broadcast.putExtra(SyncStorageService.KEY_AUTH_TOKEN, authToken);
//		broadcast.putExtra(SyncStorageService.KEY_STORAGE_CONFIG, config);
//		mContext.sendBroadcast(broadcast);
	}

	@Override
	public void start(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException {
		Message msg = Message.obtain(null, SyncStorageService.MSG_START_SYNC_STORAGE, 0, 0);
		Bundle data = new Bundle();
		data.putString(SyncStorageService.KEY_APP_TOKEN, appToken);
		data.putString(SyncStorageService.KEY_AUTH_TOKEN, authToken);
		data.putSerializable(SyncStorageService.KEY_STORAGE_CONFIG, config);
		msg.setData(data);
		if (mService != null) {
			mService.send(msg);
		} else {
			messageQueue.add(msg);
		}
//		Intent start = new Intent(mContext, SyncStorageService.class);
//		start.putExtra(SyncStorageService.KEY_APP_TOKEN, appToken);
//		start.putExtra(SyncStorageService.KEY_AUTH_TOKEN, authToken);
//		start.putExtra(SyncStorageService.KEY_STORAGE_CONFIG, config);
//		mContext.startService(start);
	}

	@Override
	public void stop(String appToken) throws DataException, StorageConfigurationException, RemoteException {
		Message msg = Message.obtain(null, SyncStorageService.MSG_STOP_SYNC_STORAGE, 0, 0);
		Bundle data = new Bundle();
		data.putString(SyncStorageService.KEY_APP_TOKEN, appToken);
		msg.setData(data);
		if (mService != null) {
			mService.send(msg);
		} else {
			messageQueue.add(msg);
		}
//		Intent broadcast = new Intent(SyncStorageService.ACTION_STOP_SYNC_STORAGE);
//		broadcast.putExtra(SyncStorageService.KEY_APP_TOKEN, appToken);
//		mContext.sendBroadcast(broadcast);
	}

	@Override
	public void disconnect() {
		if (bound) {
			mContext.unbindService(mConnection);
			bound = false;
		}
	} 
}
