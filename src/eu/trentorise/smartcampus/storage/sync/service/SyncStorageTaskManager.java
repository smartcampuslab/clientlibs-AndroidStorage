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

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.sync.SyncData;
import eu.trentorise.smartcampus.storage.sync.SyncStorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.SyncStorageHelper;

/**
 * Manages the synchronization activity for the corresponding sync storage.
 * @author raman
 *
 */
public class SyncStorageTaskManager {

	private static final Long SYNC_DELAY = 0L;
	private SyncStorageConfiguration mConfig = null;
	private SyncStorageHelper helper = null;
	private Context mContext = null;
	private ProtocolCarrier mProtocolCarrier = null;
	private String appToken;
	private String authToken;
	private String dbName;
	private SyncTaskContext ctx;
	
	TimerTask task = null;
	Timer timer = null;

	public SyncStorageTaskManager(Context context, SyncStorageConfiguration mConfig, String appToken, String dbName, String authToken, SyncTaskContext ctx) {
		super();
		this.mConfig = mConfig;
		this.mContext  = context;
		this.appToken = appToken;
		this.authToken = authToken;
		this.dbName = dbName;
		mProtocolCarrier = new ProtocolCarrier(context, appToken);
		this.ctx = ctx;
		helper = ctx.getSyncStorageHelper(appToken, dbName, mConfig.getStorageConfiguration());//new SyncStorageHelper(mContext, Utils.getDBName(mContext, appToken), Utils.getDBVersion(mContext, appToken), mConfig.getStorageConfiguration());
	}

	public void start() {
		activate();
		Log.d(getClass().getCanonicalName(), "Started sync storage "+appToken);
	}

	private void activate() {
		task = new SyncStorageUpdateTask();
		timer = new Timer(appToken+"_SYNC_STORAGE", true);
		if (mConfig.getInterval() > 0) {
			timer.schedule(task, SYNC_DELAY, mConfig.getInterval());
		}
	}
	
	
	private class SyncStorageUpdateTask extends TimerTask {

		@Override
		public void run() {
			Log.d(getClass().getCanonicalName(),"Synchronizing storage "+appToken);
			execute();
		}
	}

	public void setOffline() {
		Log.d(getClass().getCanonicalName(), "Offline sync storage "+appToken);
		terminate();
	}

	public void setOnline() {
		Log.d(getClass().getCanonicalName(), "Online sync storage "+appToken);
		activate();
	}

	private void terminate() {
		if (task != null) task.cancel();
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}
	
	public void stop() {
		Log.d(getClass().getCanonicalName(), "Stopped sync storage "+appToken);
		terminate();
	}

	public void forceSync() {
		timer.schedule(new SyncStorageUpdateTask(), 0);
		Log.d(getClass().getCanonicalName(), "Force sync for sync storage "+appToken);
	}

	public void reset(SyncStorageConfiguration config, String authToken) {
		if (config != null) mConfig = config;
		terminate();
		if (authToken != null) this.authToken = authToken; 
		activate();
		Log.d(getClass().getCanonicalName(), "Reset sync storage "+appToken);
	}

	private void execute() {
		try {
			SyncData data = helper.synchronize(mContext, mProtocolCarrier, authToken, appToken, mConfig.getHost(), mConfig.getService());
			if (data.getUpdated() != null && !data.getUpdated().isEmpty() ||
				data.getDeleted() != null && !data.getDeleted().isEmpty())
				ctx.onDBUpdate(data, appToken, dbName);
		} catch (ConnectionException e) {
			Log.e(this.getClass().getName(), ""+e.getMessage());
			setOffline();
		} catch (SecurityException e) {
			Log.e(this.getClass().getName(), ""+e.getMessage());
			ctx.handleSecurityProblem(appToken, dbName);
			stop();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), ""+e.getMessage());
			ctx.handleSyncException(appToken, dbName);
			stop();
		} finally {
			helper.close();
		}	
		
	}
	
}
