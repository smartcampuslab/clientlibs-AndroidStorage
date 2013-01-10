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
package eu.trentorise.smartcampus.storage;

import java.util.Collection;
import java.util.List;

/**
 * Basic storage CRUD operations interface.
 * @author raman
 *
 */
public interface IStorage {

	/**
	 * Insert the specified object into the storage. A new ID is generated for the object.
	 * @param input
	 * @return object created
	 * @throws Exception
	 */
	public <T extends BasicObject> T create(T input) throws Exception;
	/**
	 * Update the specified object in the storage. If 'upsert' is false and the object is not 
	 * present in the storage it will not be created.
	 * @param input
	 * @param upsert
	 * @throws Exception
	 */
	public <T extends BasicObject> void update(T input, boolean upsert) throws Exception;
	/**
	 * Delete an object of the specified type with the specified ID
	 * @param id
	 * @param cls
	 * @throws Exception
	 */
	public void delete(String id, Class<? extends BasicObject> cls) throws Exception;
	/**
	 * Perform batch storage update given the list of operations. Depending on the storage realization,
	 * the call may behave transactionally or implement only a subset of ACID properties. 
	 * @param mdls
	 * @throws Exception
	 */
	public void batch(List<BatchModel> mdls) throws Exception;
	/**
	 * Find an object of the specified class with the specified ID
	 * @param id
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws Exception;
	/**
	 * Get all the instances of the specified class.
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws Exception;

}
