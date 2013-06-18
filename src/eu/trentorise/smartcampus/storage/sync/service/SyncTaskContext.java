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

import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.SyncData;
import eu.trentorise.smartcampus.storage.sync.SyncStorageHelper;

/**
 * An interface to handle synchronization events callbacks.
 * @author raman
 *
 */
public interface SyncTaskContext {

	/**
	 * @param appToken
	 * @param config
	 * @param dbName
	 * @return {@link SyncStorageHelper} managed by the app.
	 */
	SyncStorageHelper getSyncStorageHelper(String appToken, String dbName, StorageConfiguration config);
	/**
	 * Triggered when new remote sync data is available.
	 * @param data
	 * @param dbName
	 * @param appToken
	 */
	void onDBUpdate(SyncData data, String appToken, String dbName);
	/**
	 * Triggered on authentication failure / token expiration.
	 * @param appToken
	 * @param dbName
	 */
	void handleSecurityProblem(String appToken, String dbName);
	/**
	 * Triggered upon an arbitrary exception during the synchronization procedure
	 * @param appToken
	 * @param dbName
	 */
	void handleSyncException(String appToken, String dbName); 
}
