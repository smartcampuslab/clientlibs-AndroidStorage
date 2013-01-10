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
package eu.trentorise.smartcampus.storage.db;

import java.io.Serializable;

import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;

/**
 * The interface defines the storage configuration that will support the ORM operations 
 * between the object model used in the app and the SQL-basedd storage.
 * @author raman
 *
 */
public interface StorageConfiguration extends Serializable {

	/**
	 * @return list of JavaBean classes that the storage should manage.
	 */
	Class<? extends BasicObject>[] getClasses();
	/**
	 * @param cls
	 * @return the name of the DB table where the objects of the specified class are stored
	 * @throws StorageConfigurationException
	 */
	String getTableName(Class<? extends BasicObject> cls) throws StorageConfigurationException;
	/**
	 * return the ORM helper for the specified object class
	 * @param cls
	 * @return
	 * @throws StorageConfigurationException
	 */
	<T extends BasicObject> BeanStorageHelper<T> getStorageHelper(Class<T> cls) throws StorageConfigurationException;
}
