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
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;

/**
 * Implements synchronizable storage on top of SQLite DB. Implementation relies upon {@link SyncStorageHelper}.
 * @author raman
 *
 */
public class SyncStorage implements ISyncStorage {

	protected SyncStorageHelper helper = null;
	protected Context mContext = null;

	protected ProtocolCarrier mProtocolCarrier = null;
	
	protected String appToken = null;
	protected String dbName = null;
	
	/**
	 * Create a new version of the storage given the app, the DB name and version, and the {@link StorageConfiguration} 
	 * descriptor.
	 * @param context
	 * @param appToken
	 * @param dbName
	 * @param dbVersion
	 * @param config
	 */
	public SyncStorage(Context context, String appToken, String dbName, int dbVersion, StorageConfiguration config) {
		this.mContext = context;
		this.appToken = appToken;
		this.dbName = dbName;
		Utils.writeDBVersion(mContext, appToken, dbName, dbVersion);
		helper = createHelper(context, dbName, dbVersion, config);
		mProtocolCarrier = new ProtocolCarrier(context, appToken);
	}

	/**
	 * Creates an instance of the storage helper class used. Subclasses may ovveride this method to 
	 * use custom storage helper.
	 *  
	 * @param context
	 * @param dbName
	 * @param dbVersion
	 * @param config
	 * @return
	 */
	protected SyncStorageHelper createHelper(Context context, String dbName, int dbVersion, StorageConfiguration config) {
		return new SyncStorageHelper(context, dbName, dbVersion, config);
	}
	
	@Override
	public <T extends BasicObject> T create(T input) throws DataException, StorageConfigurationException {
//		try {
			return helper.create(input,
					Utils.getObjectVersion(mContext, appToken, dbName),
					System.currentTimeMillis());
//		} finally {
//			helper.close();
//		}	
	}
	@Override
	public <T extends BasicObject> void update(T input, boolean upsert, boolean sync) throws DataException, StorageConfigurationException {
//		try {
			helper.update(input, upsert, sync, Utils.getObjectVersion(mContext,appToken, dbName), System.currentTimeMillis());
//		} finally {
//			helper.close();
//		}	
	}
	
	@Override
	public <T extends BasicObject> void update(T input, boolean upsert) throws DataException, StorageConfigurationException {
		update(input, upsert, true);
	}

	@Override
	public void delete(String id, Class<? extends BasicObject> cls) throws DataException, StorageConfigurationException {
//		try {
			helper.delete(id, cls);
//		} finally {
//			helper.close();
//		}	
	}
	@Override
	public void batch(List<BatchModel> mdls) throws DataException, StorageConfigurationException {
//		try {
			helper.batchUpdate(mdls,Utils.getObjectVersion(mContext,appToken, dbName), System.currentTimeMillis());
//		} finally {
//			helper.close();
//		}	
	}
	@Override
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws DataException, StorageConfigurationException {
//		try {
			return helper.getObjectById(id, cls);
//		} finally {
//			helper.close();
//		}	
	}
	@Override
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws DataException, StorageConfigurationException {
//		try {
			return helper.getObjects(cls);
//		} finally {
//			helper.close();
//		}	
	}
	@Override
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args) throws DataException, StorageConfigurationException {
//		try {
			return helper.query(cls, selection, args);
//		} finally {
//			helper.close();
//		}	
	}
	@Override
	public Cursor rawQuery(String query, String[] args) throws DataException, StorageConfigurationException {
		return helper.rawQuery(query, args);
	}

	
	@Override
	public void cleanCursor(Cursor cursor) {
		cursor.close();
//		helper.close();
	}

	@Override
	public SyncData synchronize(String authToken, String host, String service) throws SecurityException,
			ConnectionException, DataException, ProtocolException, StorageConfigurationException {
//		try {
			return helper.synchronize(mContext, mProtocolCarrier, authToken, appToken, host, service);
//		} finally {
//			helper.close();
//		}	
	}

	@Override
	public SyncData synchronize(ISynchronizer synchronizer) throws StorageConfigurationException, DataException, SecurityException, ConnectionException, ProtocolException {
		// TODO Auto-generated method stub
		return helper.synchronize(mContext, appToken, synchronizer);
	}

	@Override
	public void setSyncVersion(long version) {
		Utils.writeObjectVersion(mContext, appToken, dbName, version);
	}

	@Override
	public long getSyncVersion() {
		return Utils.getObjectVersion(mContext, appToken, dbName);
	}

	
}
