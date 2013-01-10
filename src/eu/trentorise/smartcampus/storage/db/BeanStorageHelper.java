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

import java.util.Map;

import eu.trentorise.smartcampus.storage.BasicObject;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Descriptor for performing object-relational mapping.  
 * @author raman
 *
 * @param <T> 
 */
public interface BeanStorageHelper<T extends BasicObject> {

	/**
	 * Convert database cursor current row to the corresponding JavaBean object
	 * @param cursor
	 * @return
	 */
	T toBean(Cursor cursor);
	/**
	 * Convert JavaBean object to the corresponding {@link ContentValues} instance that will be stored in DB
	 * @param bean
	 * @return
	 */
	ContentValues toContent(T bean);
	/**
	 * @return 'name'-'type' mappings for the DB table where the corresponding JavaBeans will be stored.
	 */
	Map<String,String> getColumnDefinitions();
	/**
	 * @return true if the corresponding DB table should support full-text search.
	 */
	boolean isSearchable();

}
