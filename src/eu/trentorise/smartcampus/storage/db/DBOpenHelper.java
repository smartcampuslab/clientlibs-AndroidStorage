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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Extension of the {@link SQLiteOpenHelper} to perform DB operations. 
 * @author raman
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	protected StorageConfiguration storageConfig = null;
	private DBCreator creator;
	
	private static Map<String, DBOpenHelper> map = new HashMap<String, DBOpenHelper>();
	
	public interface DBCreator {
		public void onCreate(SQLiteDatabase db);
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
	
	public static DBOpenHelper getHelperInstance(Context context, String dbName, int version, StorageConfiguration config, DBCreator creator) {
		if (!map.containsKey(dbName)) {
			map.put(dbName, new DBOpenHelper(context, dbName, version,config, creator));
		} else {
			map.get(dbName).storageConfig = config;
		}
		return map.get(dbName);
	}

	public DBOpenHelper(Context context, String dbName, int version, StorageConfiguration config, DBCreator creator) {
		super(context, dbName, null, version);
		this.storageConfig = config;
		this.creator = creator;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		creator.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		creator.onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	protected void finalize() throws Throwable {
		if (map!=null && !map.isEmpty()) {
			for (DBOpenHelper helper : map.values()) {
				helper.close();
			}
		}
		super.finalize();
	}
	
	

}
