package eu.trentorise.smartcampus.storage.sync.service;

import android.content.Intent;
import eu.trentorise.smartcampus.ac.Constants;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;

public abstract class EmbeddedAuthHandlerService extends AuthHandlerService {

	private String authority = null;
	
	public EmbeddedAuthHandlerService(String appToken, String authority) {
		super(appToken, new EmbeddedSCAccessProvider(), Constants.ACCOUNT_AUTHTOKEN_CHANGED_ACTION);
		this.authority = authority == null ? Constants.AUTHORITY_DEFAULT : authority;
	}

	@Override
	protected void handleTokenUpdatedAction(Intent intent) {
		 String inAuthority = intent.getStringExtra(Constants.KEY_AUTHORITY);
		 if (authority.equals(inAuthority)) {
			 try {
				String token = this.mAccessProvider.getAuthToken(this, authority);
				if (token != null) onTokenAcquired(token);
			} catch (Exception e) {
				// should never get here: token is just acquired and should be not null
			}
		 }
	}

	protected abstract void onTokenAcquired(String token);
}
