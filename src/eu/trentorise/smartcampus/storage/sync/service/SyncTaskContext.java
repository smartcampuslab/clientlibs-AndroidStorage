package eu.trentorise.smartcampus.storage.sync.service;

import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.SyncData;
import eu.trentorise.smartcampus.storage.sync.SyncStorageHelper;

public interface SyncTaskContext {

	SyncStorageHelper getSyncStorageHelper(String appToken, StorageConfiguration config);
	void onDBUpdate(SyncData data, String appToken);
	
	void handleSecurityProblem(String appToken);
	void handleSyncException(String appToken); 
}
