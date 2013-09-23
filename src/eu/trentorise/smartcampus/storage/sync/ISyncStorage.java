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

import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.ILocalStorage;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;

/**
 * Extension of the  local storage interface with the synchronization operations.
 * @author raman
 *
 */
public interface ISyncStorage extends ILocalStorage {

	public static final String FIELD_TYPE = "_type";
	public static final String FIELD_SYNC_FLAG = "_sync";

	/**
	 * Synchronize the local updates with the remote storage. Specifically, the data changes
	 * since last synchronization sessions are aggregated into {@link SyncData} data structure and send to the remote
	 * service. The remote updates (if any) are received back from the service and are written to the local storage. 
	 * @param authToken
	 * @param host
	 * @param service
	 * @return data changes from remote service
	 * @throws SecurityException
	 * @throws ConnectionException
	 * @throws DataException
	 * @throws ProtocolException
	 * @throws StorageConfigurationException
	 */
	public SyncData synchronize(String authToken, String host, String service) throws SecurityException, ConnectionException, DataException, ProtocolException, StorageConfigurationException; 

	/**
	 * Synchronize the local updates with the remote storage using custom implementation
	 * of the remote sync call. The remote updates (if any) are received back from the service and are written to the local storage. 
	 * @param synchronizer implementation of the {@link ISynchronizer} interface for remote synchronization
	 * @return
	 */
	public SyncData synchronize(ISynchronizer synchronizer) throws SecurityException, ConnectionException, DataException, ProtocolException, StorageConfigurationException;
	
	/**
	 * Set current global object version to be used to track object updates for synchronization
	 * @param version
	 */
	public void setSyncVersion(long version); 
	/**
	 * Return current global object version
	 * @return
	 */
	public long getSyncVersion(); 

	/**
	 * Allows to perform local update and control whether the changes should be syncronized to 
	 * the remote service or not.
	 * @param input
	 * @param upsert
	 * @param sync
	 * @throws DataException
	 * @throws StorageConfigurationException
	 */
	public <T extends BasicObject> void update(T input, boolean upsert, boolean sync) throws DataException, StorageConfigurationException;

}
