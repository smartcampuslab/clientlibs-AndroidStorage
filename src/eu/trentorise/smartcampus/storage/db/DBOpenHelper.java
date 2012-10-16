package eu.trentorise.smartcampus.storage.db;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
