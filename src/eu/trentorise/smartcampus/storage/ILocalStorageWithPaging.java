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

import android.database.Cursor;

public interface ILocalStorageWithPaging extends IStorageWithPaging {

	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls, int offset, int limit) throws DataException, StorageConfigurationException;
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit) throws DataException, StorageConfigurationException;
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit, String orderBy) throws DataException, StorageConfigurationException;

	public Cursor rawQuery(String query, String[] args, int offset, int limit)
			throws DataException, StorageConfigurationException;

}
