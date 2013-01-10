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
package eu.trentorise.smartcampus.storage.remote;

import java.util.Collection;
import java.util.List;

import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.IStorage;
/**
 * Remote service storage facade that extends CRUD operations of {@link IStorage}.
 * @author raman
 *
 */
public interface IRemoteStorage extends IStorage {

	/**
	 * Update the configuration parameters for the remote storage session with the service address and auth token.
	 * @param authToken
	 * @param host server address
	 * @param service service address relative to the server path
	 */
	public void setConfig(String authToken, String host, String service);
	
	public <T extends BasicObject> T create(T input) throws DataException, ConnectionException, ProtocolException, SecurityException;
	public <T extends BasicObject> void update(T input, boolean upsert) throws DataException, ConnectionException, ProtocolException, SecurityException;
	public void delete(String id, Class<? extends BasicObject> cls)  throws DataException, ConnectionException, ProtocolException, SecurityException;
	public void batch(List<BatchModel> mdls)  throws DataException, ConnectionException, ProtocolException, SecurityException;
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException;
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException;

	/**
	 * Search objects of the type defined by the specified class according to the query model. The query 
	 * should be an instance of the valid JavaBean class to be transformed to JSON format.
	 * @param query
	 * @param cls
	 * @return
	 * @throws DataException
	 * @throws ConnectionException
	 * @throws ProtocolException
	 * @throws SecurityException
	 */
	public  <T extends BasicObject> Collection<T> searchObjects(Object query, Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException;

}
