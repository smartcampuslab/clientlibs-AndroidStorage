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
/**
 * Interface for the storage to support the 'paged' access to the list of class instances
 * @author raman
 *
 */
public interface IStorageWithPaging {

	/**
	 * Return a subset of at most 'limit' of the object instances that starts at position 'offset' in 
	 * the whole list of instances naturally ordered.
	 * @param cls
	 * @param offset 
	 * @param limit if less then 0, all the instances from the specified position returned
	 * @return
	 * @throws Exception
	 */
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls, int offset, int limit) throws Exception;

}
