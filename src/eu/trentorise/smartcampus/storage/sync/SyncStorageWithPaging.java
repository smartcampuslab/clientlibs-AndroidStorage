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
import eu.trentorise.smartcampus.storage.ILocalStorageWithPaging;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;

/**
 * Synchronizable storage supporting the paged queries ({@link ILocalStorageWithPaging} interface).
 * @author raman
 *
 */
public class SyncStorageWithPaging extends SyncStorage implements ILocalStorageWithPaging {
	
	/**
	 * Create a new version of the storage given the app, the DB name and version, and the {@link StorageConfiguration} 
	 * descriptor.
	 * @param context
	 * @param appToken
	 * @param dbName
	 * @param dbVersion
	 * @param config
	 */
	public SyncStorageWithPaging(Context context, String appToken, String dbName, int dbVersion, StorageConfiguration config) {
		super(context, appToken, dbName, dbVersion, config);
	}

	
	@Override
	protected SyncStorageHelper createHelper(Context context, String dbName, int dbVersion, StorageConfiguration config) {
		return new SyncStorageHelperWithPaging(context, dbName, dbVersion, config);
	}


	@Override
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls, int offset, int limit) throws DataException, StorageConfigurationException {
		return ((SyncStorageHelperWithPaging)helper).getObjects(cls, offset, limit);
	}

	@Override
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit) throws DataException, StorageConfigurationException {
		return ((SyncStorageHelperWithPaging)helper).query(cls, selection, args, offset, limit);
	}

	@Override
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit, String orderBy) throws DataException, StorageConfigurationException {
		return ((SyncStorageHelperWithPaging)helper).query(cls, selection, args, offset, limit, orderBy);
	}


	@Override
	public Cursor rawQuery(String query, String[] args, int offset, int limit) throws DataException, StorageConfigurationException {
		return ((SyncStorageHelperWithPaging)helper).rawQuery(query, args, offset, limit);
	}

}
