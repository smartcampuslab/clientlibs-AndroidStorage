package eu.trentorise.smartcampus.storage.sync;

import android.os.RemoteException;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;


public interface ISyncManager {

	void start(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException;
	void stop(String appToken) throws DataException, StorageConfigurationException, RemoteException;
	void disconnect();

	void synchronize(String authToken, String appToken) throws DataException, StorageConfigurationException, RemoteException;
	void synchronize(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException;

}