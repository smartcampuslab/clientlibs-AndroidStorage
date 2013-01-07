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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;

public abstract class AuthHandlerService extends Service {

	private BroadcastReceiver mAuthReceiver;


	private String tokenUpdatedAction;

	protected SCAccessProvider mAccessProvider;
	protected String appToken;
	
	public AuthHandlerService(String appToken, SCAccessProvider mAccessProvider, String tokenUpdatedAction) {
		super();
		this.mAccessProvider = mAccessProvider;
		this.tokenUpdatedAction = tokenUpdatedAction;
		this.appToken = appToken;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAccessProvider = new EmbeddedSCAccessProvider();
		mAuthReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(SyncStorageService.ACTION_AUTHENTICATION_PROBLEM)) {
					String appToken = intent.getStringExtra(SyncStorageService.KEY_APP_TOKEN);
					if (AuthHandlerService.this.appToken.equals(appToken)) {
						mAccessProvider.invalidateToken(AuthHandlerService.this, null);
						try {
							mAccessProvider.getAuthToken(AuthHandlerService.this, null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (intent.getAction().equals(tokenUpdatedAction)) {
					handleTokenUpdatedAction(intent);
				}
			}
			
		};

		registerReceiver(mAuthReceiver, new IntentFilter(SyncStorageService.ACTION_AUTHENTICATION_PROBLEM));
		registerReceiver(mAuthReceiver, new IntentFilter(tokenUpdatedAction));
	}

	protected abstract void handleTokenUpdatedAction(Intent intent);

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mAuthReceiver);
	}

	
}
