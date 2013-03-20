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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;
import eu.trentorise.smartcampus.storage.BatchModel.CreateModel;
import eu.trentorise.smartcampus.storage.BatchModel.DeleteModel;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.ILocalStorage;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.BatchUpdateModel;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
import eu.trentorise.smartcampus.storage.db.StorageHelper;
import eu.trentorise.smartcampus.storage.sync.SyncUpdateModel.UpdateModel;

/**
 * Extends SQLite-based {@link StorageHelper} with the methods for data synchronization.
 * The synchronization is based on the object version. Each update/create/delete increments the
 * global counter associated with the storage. The version is then used to identify the
 * changes since last sync session. The updates received from the remote storage, as well as the 
 * new remote global version, are then stored locally.
 * 
 * @author raman
 *
 */
public class SyncStorageHelper extends StorageHelper {

	private static final String TABLE_SYNC = "_sync_objects";
	private static final String TABLE_SYNC_FIELD_ID = "id";
	private static final String TABLE_SYNC_FIELD_TYPE = "type";
	private static final String TABLE_SYNC_FIELD_ACTION = "action";
	private static final String TABLE_SYNC_FIELD_DATA = "data";

	private static final int ACTION_DELETE = 1;
	private static final int ACTION_UPDATE = 2;
	private static final int ACTION_CREATE = 3;

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (getStorageConfig() != null) {
			Class<? extends BasicObject>[] classes = getStorageConfig().getClasses();
			for (Class<? extends BasicObject> cls : classes) {
				try {
					if (getStorageConfig().getStorageHelper(cls) == null) {
						Log.e(getClass().getCanonicalName(),
								"No configuration for class "
										+ cls.getCanonicalName());
						continue;
					}
					String upgradeQuery = "DROP TABLE IF EXISTS "
							+ getStorageConfig().getTableName(cls);
					db.execSQL(upgradeQuery);

					String createStmt = generateCreateStatement(getStorageConfig().getTableName(cls), getStorageConfig().getStorageHelper(cls).getColumnDefinitions(), getStorageConfig().getStorageHelper(cls).isSearchable());
					db.execSQL(createStmt);
				} catch (StorageConfigurationException e) {
					Log.e(getClass().getCanonicalName(), e.getMessage(), e);
				}
			}
		}

		String upgradeQuery = "DROP TABLE IF EXISTS " + TABLE_SYNC;
		db.execSQL(upgradeQuery);
		upgradeQuery = "CREATE TABLE " + TABLE_SYNC + "("
				+ TABLE_SYNC_FIELD_TYPE + " TEXT, " + TABLE_SYNC_FIELD_ID
				+ " TEXT, " + ILocalStorage.FIELD_UPDATE_TIME + " INTEGER,  "
				+ TABLE_SYNC_FIELD_ACTION + " INTEGER, "
				+ TABLE_SYNC_FIELD_DATA + " TEXT)";
		db.execSQL(upgradeQuery);
	}
	@Override
	protected String generateCreateStatement(String table, Map<String, String> inColumnDefinitions, boolean searchable) {
		Map<String,String> extended = new HashMap<String, String>();
		if (inColumnDefinitions != null) extended.putAll(inColumnDefinitions);
		extended.put(ISyncStorage.FIELD_SYNC_FLAG, "INTEGER");
		return super.generateCreateStatement(table, extended, searchable);
	}


	public SyncStorageHelper(Context context, String dbName, int version,
			StorageConfiguration config) {
		super(context, dbName, version, config);
	}

	public void batchUpdate(List<BatchModel> mdls, long version,
			long updateTime) throws StorageConfigurationException,
			DataException {
		if (mdls != null) {
			List<BatchUpdateModel> uMdls = new ArrayList<BatchUpdateModel>();
			for (BatchModel mdl : mdls) {
				if (mdl instanceof CreateModel) {
					uMdls.add(new eu.trentorise.smartcampus.storage.db.BatchUpdateModel.CreateModel(
							((CreateModel) mdl).object, getCreateExt(version, updateTime)));
				}
				if (mdl instanceof UpdateModel) {
					uMdls.add(new eu.trentorise.smartcampus.storage.db.BatchUpdateModel.UpdateModel(
							((UpdateModel) mdl).object, getUpdateExt(
									((UpdateModel) mdl).sync, version,
									updateTime), ((UpdateModel) mdl).upsert));
				}
				if (mdl instanceof DeleteModel) {
					uMdls.add(new eu.trentorise.smartcampus.storage.db.BatchUpdateModel.DeleteModel(
							((DeleteModel) mdl).id, ((DeleteModel) mdl).cls));
				}
			}
			batchUpdate(uMdls);
		}
	}

	public <T extends BasicObject> T create(T input, long version, long updateTime)
			throws StorageConfigurationException, DataException {
		ContentValues values = getCreateExt(version, updateTime);
		return create(input, values);
	}

	private ContentValues getCreateExt(long version, long updateTime) {
		ContentValues values = new ContentValues();
		values.put(ILocalStorage.FIELD_VERSION, version + 1);
		values.put(ILocalStorage.FIELD_UPDATE_TIME, updateTime);
		values.put(ISyncStorage.FIELD_SYNC_FLAG, true);
		return values;
	}

	private ContentValues getUpdateExt(boolean sync, long version, long updateTime) {
		ContentValues values = new ContentValues();
		values.put(ILocalStorage.FIELD_VERSION, version + (sync ? 1 : 0));
		values.put(ILocalStorage.FIELD_UPDATE_TIME, updateTime);
		values.put(ISyncStorage.FIELD_SYNC_FLAG, sync);
		return values;
	}

	public <T extends BasicObject> void update(T input, boolean upsert, boolean sync, long version,
			long updateTime) throws StorageConfigurationException,
			DataException {
		update(input, upsert, getUpdateExt(sync, version, updateTime));
	}

	@Override
	protected <T extends BasicObject> T create(T input, ContentValues extensions,
			SQLiteDatabase db) throws StorageConfigurationException,
			DataException {
		db.beginTransaction();
		try {
			T dbObject = super.create(input, extensions, db);
			ContentValues values = new ContentValues();
			values.put(TABLE_SYNC_FIELD_ID, dbObject.getId());
			values.put(TABLE_SYNC_FIELD_TYPE, input.getClass().getCanonicalName());
			values.put(ILocalStorage.FIELD_UPDATE_TIME, System.currentTimeMillis());
			values.put(TABLE_SYNC_FIELD_ACTION, ACTION_CREATE);
			dbObject.setVersion(extensions.getAsLong(ILocalStorage.FIELD_VERSION));
			dbObject.setUpdateTime(extensions.getAsLong(ILocalStorage.FIELD_UPDATE_TIME));
			values.put(TABLE_SYNC_FIELD_DATA, Utils.convertDataToJSON(dbObject));
			if (db.insert(TABLE_SYNC, null, values) < 0) {
				return null;
			}
			db.setTransactionSuccessful();
			return dbObject;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	protected <T extends BasicObject> String update(T input, ContentValues extensions,
			boolean upsert, SQLiteDatabase db)
			throws StorageConfigurationException, DataException {
		db.beginTransaction();
		try {
			Boolean sync = extensions.getAsBoolean(ISyncStorage.FIELD_SYNC_FLAG);
			extensions.remove(ISyncStorage.FIELD_SYNC_FLAG);
			String id = super.update(input, extensions, upsert, db);
			if (Boolean.TRUE.equals(sync)) {
				ContentValues values = new ContentValues();
				values.put(TABLE_SYNC_FIELD_ID, id);
				values.put(TABLE_SYNC_FIELD_TYPE, input.getClass().getCanonicalName());
				values.put(ILocalStorage.FIELD_UPDATE_TIME, System.currentTimeMillis());
				values.put(TABLE_SYNC_FIELD_ACTION, ACTION_UPDATE);
				BasicObject dbObject = getObjectById(id, input.getClass(), db);
				dbObject.setVersion(extensions.getAsLong(ILocalStorage.FIELD_VERSION));
				dbObject.setUpdateTime(extensions.getAsLong(ILocalStorage.FIELD_UPDATE_TIME));
				values.put(TABLE_SYNC_FIELD_DATA, Utils.convertDataToJSON(dbObject));
				if (db.insert(TABLE_SYNC, null, values) < 0) {
					return null;
				}
			}
			db.setTransactionSuccessful();
			return id;
		} finally {
			db.endTransaction();
		}
	}

	protected <T extends BasicObject> void delete(String id, Class<T> cls, SQLiteDatabase db)
			throws StorageConfigurationException, DataException {
		db.beginTransaction();
		try {
			super.delete(id, cls, db);
			ContentValues values = new ContentValues();
			values.put(TABLE_SYNC_FIELD_ID, id);
			values.put(TABLE_SYNC_FIELD_TYPE, cls.getCanonicalName());
			values.put(ILocalStorage.FIELD_UPDATE_TIME, System.currentTimeMillis());
			values.put(TABLE_SYNC_FIELD_ACTION, ACTION_DELETE);
			db.insert(TABLE_SYNC, null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	public SyncData getDataToSync(long version)
			throws StorageConfigurationException {
		Map<String, List<Object>> updatedMap = new HashMap<String, List<Object>>();
		Map<String, List<String>> deletedMap = new HashMap<String, List<String>>();
		List<Pair<String, String>> syncElements = new ArrayList<Pair<String, String>>();
		Cursor cursor = null;
		try {
			String query = "SELECT * FROM " + TABLE_SYNC;
			cursor = rawQuery(query, null);
			cursor.moveToFirst();
			while (cursor.getPosition() < cursor.getCount()) {
				String type = cursor.getString(cursor
						.getColumnIndex(TABLE_SYNC_FIELD_TYPE));
				int action = cursor.getInt(cursor
						.getColumnIndex(TABLE_SYNC_FIELD_ACTION));
				String id = cursor.getString(cursor
						.getColumnIndex(TABLE_SYNC_FIELD_ID));
				switch (action) {
				case ACTION_CREATE:
				case ACTION_UPDATE: {
					List<Object> updated = updatedMap.get(type);
					if (updated == null) {
						updated = new ArrayList<Object>();
						updatedMap.put(type, updated);
					}
					updated.add(Utils.convertJSONToData(cursor.getString(cursor
							.getColumnIndex(TABLE_SYNC_FIELD_DATA))));
				}
					break;
				case ACTION_DELETE: {
					List<String> deleted = deletedMap.get(type);
					if (deleted == null) {
						deleted = new ArrayList<String>();
						deletedMap.put(type, deleted);
					}
					deleted.add(cursor.getString(cursor
							.getColumnIndex(TABLE_SYNC_FIELD_ID)));
				}
				}
				syncElements.add(new Pair<String, String>(id, type));
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}
		SyncData data = new SyncData();
		data.setVersion(version);
		data.setDeleted(deletedMap);
		data.setUpdated(updatedMap);
		data.setSyncElements(syncElements);
		return data;
	}

	@SuppressWarnings("unchecked")
	public void cleanSyncData(SyncData result, List<Pair<String, String>> oldElements) throws StorageConfigurationException, DataException {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (result.getUpdated() != null) {
				for (Map.Entry<String, List<Object>> entry : result.getUpdated().entrySet()) {
					Class<? extends BasicObject> cls = null;
					try {
						cls = (Class<? extends BasicObject>)Class.forName(entry.getKey());
					} catch (ClassNotFoundException e) {
						Log.e(getClass().getName(), "Unknown data type");
						continue;
					}
					for (Object o : entry.getValue()) {
						BasicObject bo = eu.trentorise.smartcampus.android.common.Utils.convertObjectToData(cls, o);
						if (bo == null) continue;
						super.update(bo,
								getUpdateExt(false, bo.getVersion(), bo.getUpdateTime()),
								true, db);
					}
				}
			}
			if (result.getDeleted() != null) {
				for (Map.Entry<String, List<String>> entry : result.getDeleted().entrySet()) {
					Class<? extends BasicObject> cls = null;
					try {
						cls = (Class<? extends BasicObject>)Class.forName(entry.getKey());
					} catch (ClassNotFoundException e) {
						Log.e(getClass().getName(), "Unknown data type");
						continue;
					}
					for (String o : entry.getValue()) {
						try {
							super.delete(o, cls, db);
						} catch (Exception e) {
							Log.w(getClass().getName(), e.getMessage());
						}
					}
				}
			}
			if (oldElements != null) {
				for (Pair<String, String> elem : oldElements) {
					db.execSQL("DELETE FROM " + TABLE_SYNC + " WHERE "
							+ TABLE_SYNC_FIELD_ID + " = '" + elem.first
							+ "' AND " + TABLE_SYNC_FIELD_TYPE + " = '"
							+ elem.second + "'");
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public SyncData synchronize(Context ctx, ProtocolCarrier mProtocolCarrier, String authToken, String appToken, String host, String service) throws SecurityException, ConnectionException,
			DataException, ProtocolException, StorageConfigurationException 
	{
		synchronized (helper) {
			SyncData data = getDataToSync(Utils.getObjectVersion(ctx, appToken));
			MessageRequest req = prepareSyncRequest(data, host, service);
			MessageResponse res = mProtocolCarrier.invokeSync(req, appToken, authToken);
			return processResponse(ctx, res, data, appToken);
		}
	}

	private SyncData processResponse(Context ctx, MessageResponse res, SyncData data, String appToken) throws StorageConfigurationException, DataException {
		SyncData resData = Utils.convertJSONToSyncData(res.getBody());
		cleanSyncData(resData, data.getSyncElements());
		Utils.writeObjectVersion(ctx, appToken, resData.getVersion());
		return resData;
	}

	private MessageRequest prepareSyncRequest(SyncData syncData, String host, String service) {
		MessageRequest req = new MessageRequest(host, service);
		req.setMethod(Method.POST);
		req.setBody(Utils.convertSyncDataToJSON(syncData));
		req.setQuery("since=" + syncData.getVersion());
		return req;
	}

}
