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

import android.accounts.AccountManager;
import android.content.Intent;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.authenticator.AMSCAccessProvider;

public abstract class AMAuthHandlerService extends AuthHandlerService {

	private String authority = null;
	
	public AMAuthHandlerService(String appToken, String authority) {
		super(appToken, new AMSCAccessProvider(), AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
		this.authority = authority == null ? Constants.AUTHORITY_DEFAULT : authority;
	}

	@Override
	protected void handleTokenUpdatedAction(Intent intent) {
		 try {
			String token = this.mAccessProvider.getAuthToken(this, authority);
			if (token != null) onTokenAcquired(token);
		} catch (Exception e) {
			// token may be null, skip
		}
	}

	protected abstract void onTokenAcquired(String token);
}
