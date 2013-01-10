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

import java.util.Collection;

import android.content.Context;
import android.database.Cursor;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
/**
 * Synchronizable storage helper supporting the paged queries.
 * @author raman
 *
 */
public class SyncStorageHelperWithPaging extends SyncStorageHelper {

	public SyncStorageHelperWithPaging(Context context, String dbName, int version, StorageConfiguration config) {
		super(context, dbName, version, config);
	}

	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls, int offset, int limit) throws DataException, StorageConfigurationException {
		String tableName = getStorageConfig().getTableName(cls);
		if (limit <= 0) return getObjects(cls);
		String queryString = "SELECT * FROM " + tableName+" LIMIT "+limit+" OFFSET "+offset; 
		return performQuery(cls, queryString, null);	
	}

	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit)  throws DataException, StorageConfigurationException {
		String tableName = getStorageConfig().getTableName(cls);
		String clause = selection == null  || selection.trim().length() == 0 ? "" : (" WHERE " + selection);
		String queryString = "SELECT * FROM " + tableName+ clause;

		if (limit > 0) queryString += " LIMIT "+limit+" OFFSET "+offset; 
		return performQuery(cls, queryString, args);	
	}

	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit, String orderBy)  throws DataException, StorageConfigurationException {
		String tableName = getStorageConfig().getTableName(cls);
		String clause = selection == null || selection.trim().length() == 0 ? "" : (" WHERE " + selection);
		String queryString = "SELECT * FROM " + tableName+ clause;

		if (orderBy != null && !orderBy.isEmpty()) {
			queryString += " ORDER BY "+orderBy;
		}
		
		if (limit > 0) queryString += " LIMIT "+limit+" OFFSET "+offset; 
		return performQuery(cls, queryString, args);	
	}

	public Cursor rawQuery(String query, String[] args, int offset, int limit) throws DataException, StorageConfigurationException {
		String queryString = query;
		if (limit > 0) queryString += " LIMIT "+limit+" OFFSET "+offset; 
		return rawQuery(queryString, args);
	}

}
