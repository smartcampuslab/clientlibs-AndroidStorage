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


public interface ISyncManager {

	void start(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException;
	void stop(String appToken) throws DataException, StorageConfigurationException, RemoteException;
	void disconnect();

	void synchronize(String authToken, String appToken) throws DataException, StorageConfigurationException, RemoteException;
	void synchronize(String authToken, String appToken, SyncStorageConfiguration config) throws DataException, StorageConfigurationException, RemoteException;

}
