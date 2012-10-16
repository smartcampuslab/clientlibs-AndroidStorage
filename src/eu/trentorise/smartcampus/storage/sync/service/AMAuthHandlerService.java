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
