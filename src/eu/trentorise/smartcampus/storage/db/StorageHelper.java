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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.ILocalStorage;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.BatchUpdateModel.CreateModel;
import eu.trentorise.smartcampus.storage.db.BatchUpdateModel.DeleteModel;
import eu.trentorise.smartcampus.storage.db.BatchUpdateModel.UpdateModel;
import eu.trentorise.smartcampus.storage.db.DBOpenHelper.DBCreator;

/**
 * A helper class that is used to provide SQLite-based implementation of the {@link ILocalStorage} interace
 * @author raman
 *
 */
public class StorageHelper implements DBCreator {

	protected StorageConfiguration storageConfig = null;
	protected DBOpenHelper helper = null;

	private String name = null;

	public StorageHelper(Context context, String dbName, int version, StorageConfiguration config) {
		this.storageConfig = config;
		this.name =dbName;
		this.helper = DBOpenHelper.getHelperInstance(context, dbName, version, config, this);
	}
	
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws DataException, StorageConfigurationException {
		SQLiteDatabase db = helper.getReadableDatabase();
		return getObjectById(id, cls, db);
	}

	protected <T extends BasicObject> T getObjectById(String id, Class<T> cls, SQLiteDatabase db) throws DataException, StorageConfigurationException {
		Cursor cursor = null;
		try {
			String tableName = getStorageConfig().getTableName(cls);
			cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE "+ILocalStorage.FIELD_ID+" = ?", new String[]{id});
			if (cursor.getCount() > 1) throw new DataException("Object of type " + cls.getCanonicalName() + "and id " + id + " is not unique.");
			cursor.moveToFirst();
			return getStorageConfig().getStorageHelper(cls).toBean(cursor);
		} finally {
			cursor.close();
		}	
	}

	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws StorageConfigurationException {
		String tableName = getStorageConfig().getTableName(cls);
		String queryString = "SELECT * FROM " + tableName; 
		return performQuery(cls, queryString, null);	
	}

	protected <T extends BasicObject> Collection<T> performQuery(Class<T> cls, String queryString, String[] args) throws StorageConfigurationException {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(queryString, args);
			Collection<T> result = new ArrayList<T>();
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					result.add(getStorageConfig().getStorageHelper(cls).toBean(cursor));
					cursor.moveToNext();
				}
			}
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new StorageConfigurationException(e.getMessage());
		} finally {
			cursor.close();
		}
	}
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args) throws StorageConfigurationException {
		String tableName = getStorageConfig().getTableName(cls);
		String clause = selection == null || selection.trim().length() == 0 ? "" : (" WHERE " + selection);
		String queryString = "SELECT * FROM " + tableName+ clause;
		return performQuery(cls, queryString, args);
	}

	public Cursor rawQuery(String query, String[] args) throws StorageConfigurationException {
		SQLiteDatabase db = helper.getReadableDatabase();
		return db.rawQuery(query, args);
	}
	
	public void batchUpdate(List<BatchUpdateModel> mdls) throws StorageConfigurationException, DataException {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			if (mdls != null) {
				for (BatchUpdateModel mdl : mdls) {
					if (mdl instanceof CreateModel) create(((CreateModel) mdl).object, ((CreateModel) mdl).extensions, db); 
					if (mdl instanceof UpdateModel) update(((UpdateModel) mdl).object, ((UpdateModel) mdl).extensions, ((UpdateModel) mdl).upsert, db); 
					if (mdl instanceof DeleteModel) delete(((DeleteModel) mdl).id, ((DeleteModel) mdl).cls, db); 
				}
			}
			db.setTransactionSuccessful();
		} 
		finally {
			db.endTransaction();
		}
		
	}
	
	public <T extends BasicObject> T create(T input, ContentValues extensions) throws StorageConfigurationException, DataException {
		SQLiteDatabase db = helper.getWritableDatabase();
		return create(input, extensions, db);
	}

	@SuppressWarnings("unchecked")
	protected <T extends BasicObject> T create(T input, ContentValues extensions, SQLiteDatabase db)
			throws StorageConfigurationException, DataException {
		String tableName = getStorageConfig().getTableName(input.getClass());
		String _id = generateID();
		ContentValues target = getStorageConfig().getStorageHelper((Class<T>)input.getClass()).toContent(input);
		target.put(ILocalStorage.FIELD_ID, _id);
		if (extensions != null) {
			target.putAll(extensions);
		}
		if (db.insert(tableName, null, target) == -1) {
			throw new DataException("Failed to create an object of type "+input.getClass());
		}
		return (T)getObjectById(_id, input.getClass(), db);
	}
	
	public <T extends BasicObject> void update(T input, boolean upsert, ContentValues extensions) throws StorageConfigurationException, DataException {
		SQLiteDatabase db = helper.getWritableDatabase();
		update(input, extensions, upsert, db);
	}

	@SuppressWarnings("unchecked")
	protected <T extends BasicObject> String update(T input, ContentValues extensions, boolean upsert, SQLiteDatabase db)
			throws StorageConfigurationException, DataException {
		String tableName = getStorageConfig().getTableName(input.getClass());
		ContentValues target = getStorageConfig().getStorageHelper((Class<T>)input.getClass()).toContent(input);
		String _id = target.getAsString(ILocalStorage.FIELD_ID);
		if (_id==null || _id.length() == 0) {
			throw new DataException("ID field for update should not be empty.");
		}
		if (extensions != null) {
			target.putAll(extensions);
		}
		if (db.update(tableName, target, ILocalStorage.FIELD_ID + "= ? ", new String[]{_id}) == 0) {
			if (upsert) {
				if (db.insert(tableName, null, target) == -1) {
					throw new DataException("Failed to create an object of type "+input.getClass());
				}
			} else {
				throw new DataException("Failed to update an object of type "+input.getClass()+" and id = "+_id);
			}
		}
		return _id;
	}

	public <T extends BasicObject> void delete(String id, Class<T> cls) throws StorageConfigurationException, DataException {
		SQLiteDatabase db = helper.getWritableDatabase();
		delete(id, cls, db);
	}

	protected <T extends BasicObject> void delete(String id, Class<T> cls, SQLiteDatabase db)
			throws StorageConfigurationException, DataException {
		String tableName = getStorageConfig().getTableName(cls);
		if (id==null || id.length() == 0) {
			throw new DataException("ID field for delete should not be empty.");
		}
		if (db.delete(tableName, ILocalStorage.FIELD_ID + " = ?", new String[]{id}) == 0) {
			throw new DataException("Failed to delete an object of type "+cls+"and id = "+id);
		}
	}

	protected String generateID() {
		return UUID.randomUUID().toString();
	}

	protected StorageConfiguration getStorageConfig() {
		return storageConfig;
	}

	public String getDBName() {
		return this.name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (storageConfig != null) {
			Class<? extends BasicObject>[] classes = storageConfig.getClasses(); 
			for (Class<? extends BasicObject> cls : classes) {
				try {
					if (storageConfig.getStorageHelper(cls) == null) {
						Log.e(getClass().getCanonicalName(), "No configuration for class "+ cls.getCanonicalName());
						continue;
					}
					String upgradeQuery = "DROP TABLE IF EXISTS " + storageConfig.getTableName(cls);
					db.execSQL(upgradeQuery);

					String createStmt = generateCreateStatement(storageConfig.getTableName(cls), storageConfig.getStorageHelper(cls).getColumnDefinitions(), storageConfig.getStorageHelper(cls).isSearchable());
					db.execSQL(createStmt);
				} catch (StorageConfigurationException e) {
					Log.e(getClass().getCanonicalName(),e.getMessage(),e);
				}
			}
		}
		
	}
	protected String generateCreateStatement(String table, Map<String, String> inColumnDefinitions, boolean searchable) {
		Map<String,String> columnDefinitions = inColumnDefinitions == null ? new HashMap<String, String>() : new HashMap<String, String>(inColumnDefinitions);
		columnDefinitions.put(ILocalStorage.FIELD_ID, "TEXT");
		columnDefinitions.put(ILocalStorage.FIELD_UPDATE_TIME, "INTEGER");
		columnDefinitions.put(ILocalStorage.FIELD_VERSION, "INTEGER");
		String stmt = null;
		if (searchable) {
			stmt = "CREATE VIRTUAL TABLE "+table + " USING FTS3(";
		}  else {
			stmt = "CREATE TABLE "+table + "(";
		}
		int i = 0;
		for (Entry<String,String> entry : columnDefinitions.entrySet()) {
			stmt += entry.getKey() +" "+entry.getValue();
			if (i < columnDefinitions.size()-1) stmt +=",";
			i++;
		}
		stmt += ")";
		return stmt;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

	public void close() {
		helper.close();
	}
}
