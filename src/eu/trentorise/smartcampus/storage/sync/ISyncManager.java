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

import android.os.RemoteException;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;

/**
 * An interface to control the life-cycle of the synchronization for a storage.
 * @author raman
 *
 */
public interface ISyncManager {

	/**
	 * Start the synchronization cycle for the specified storage. The background service
	 * is activated upon this starting periodic synchronization (if specified)
	 * @param authToken
	 * @param appToken
	 * @param config
	 * @throws DataException
	 * @throws StorageConfigurationException
	 * @throws RemoteException
	 */
	void start(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException;
	/**
	 * Stop the synchronization cylce for the specified app storage
	 * @param appToken
	 * @throws DataException
	 * @throws StorageConfigurationException
	 * @throws RemoteException
	 */
	void stop(String appToken) throws DataException, StorageConfigurationException, RemoteException;
	/**
	 * Disconnect from service and release resources
	 */
	void disconnect();
	/**
	 * Force storage synchronization (asynchronously), given the currently used configuration.
	 * @param authToken
	 * @param appToken
	 * @throws DataException
	 * @throws StorageConfigurationException
	 * @throws RemoteException
	 */
	void synchronize(String authToken, String appToken) throws DataException, StorageConfigurationException, RemoteException;
	/**
	 * Force storage synchronization (asynchronously), given the specified used configuration.
	 * @param authToken
	 * @param appToken
	 * @param config
	 * @throws DataException
	 * @throws StorageConfigurationException
	 * @throws RemoteException
	 */
	void synchronize(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException;

}
