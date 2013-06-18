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

import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.NopAnnotationIntrospector;

import android.content.Context;
import eu.trentorise.smartcampus.storage.BasicObject;

/**
 * Utility methods used by storage implementations.
 * @author raman
 *
 */
public class Utils {
	private static final String PREF_SYNC_STORAGE_TIMES = "PrefSyncStorageSyncTimes";
	private static final String PREF_SYNC_STORAGE_VERSIONS = "PrefSyncStorageObjectVersions";
	private static final String PREF_SYNC_STORAGE_DB_VERSIONS = "PrefSyncStorageDBVersions";

	/**
	 * Read current DB version used by the specific app from app preferences
	 * @param mContext
	 * @param appToken
	 * @return
	 */
	public static int getDBVersion(Context mContext, String appToken, String dbName) {
		return mContext.getSharedPreferences(PREF_SYNC_STORAGE_DB_VERSIONS, Context.MODE_PRIVATE).getInt(key(appToken, dbName), 0);
	}
	/**
	 * Read current value of the global object version counter.
	 * @param mContext
	 * @param appToken
	 * @return
	 */
	public static long getObjectVersion(Context mContext, String appToken, String dbName) {
		return mContext.getSharedPreferences(PREF_SYNC_STORAGE_VERSIONS, Context.MODE_PRIVATE).getLong(key(appToken, dbName), -1);
	}
	
	/**
	 * 
	 * @param mContext
	 * @param appToken
	 * @return timestamp of the last synchronization for the app
	 */
	public static long getLastObjectSyncTime(Context mContext, String appToken, String dbName) {
		return mContext.getSharedPreferences(PREF_SYNC_STORAGE_TIMES, Context.MODE_PRIVATE).getLong(key(appToken, dbName), -1);
	}
	
	/**
	 * Write last used global object version to the app preferences
	 * @param mContext
	 * @param appToken
	 * @param version
	 */
	public static void writeObjectVersion(Context mContext, String appToken, String dbName, long version) {
		mContext.getSharedPreferences(PREF_SYNC_STORAGE_VERSIONS, Context.MODE_PRIVATE).edit().putLong(key(appToken, dbName), version).commit();
	}
	/**
	 * Write last synchronization timestamp to the app preferences
	 * @param mContext
	 * @param appToken
	 * @param version
	 */
	public static void writeLastObjectSyncTime(Context mContext, String appToken, String dbName, long time) {
		mContext.getSharedPreferences(PREF_SYNC_STORAGE_TIMES, Context.MODE_PRIVATE).edit().putLong(key(appToken, dbName), time).commit();
	}
	/**
	 * Write DB version used by the specific app to the app preferences
	 * @param mContext
	 * @param appToken
	 * @param version
	 */
	public static void writeDBVersion(Context mContext, String appToken, String dbName, int version) {
		mContext.getSharedPreferences(PREF_SYNC_STORAGE_DB_VERSIONS, Context.MODE_PRIVATE).edit().putInt(key(appToken, dbName), version).commit();
	}

    private static ObjectMapper fullMapper = new ObjectMapper();
    static {
        fullMapper.setAnnotationIntrospector(NopAnnotationIntrospector.nopInstance());
        fullMapper.configure(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING, true);
        fullMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fullMapper.configure(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING, true);

        fullMapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
        fullMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }
    /**
     * Convert arbitrary {@link BasicObject} instance to JSON string
     * @param o
     * @return
     */
	@SuppressWarnings({"unchecked" })
	public static String convertDataToJSON(BasicObject o)  {
		try {
			Map<String,Object> map = fullMapper.convertValue(o, Map.class);
//			map.put(IStorage.FIELD_UPDATE_TIME, updateTime);
//			map.put(IStorage.FIELD_VERSION, version);
			map.put(ISyncStorage.FIELD_TYPE, o.getClass().getCanonicalName());
			return fullMapper.writeValueAsString(map);
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * Convert JSON object to {@link Map} 
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> convertJSONToData(String o)  {
		try {
			return fullMapper.readValue(o, Map.class);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Convert {@link SyncData} instance to JSON string representation
	 * @param syncData
	 * @return
	 */
	public static String convertSyncDataToJSON(SyncData syncData) {
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> node = fullMapper.convertValue(syncData, Map.class);
			node.remove("syncElements");
			return fullMapper.writeValueAsString(node);
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * Convert JSON string representing synchronization data to {@link SyncData} instance
	 * @param body
	 * @return
	 */
	public static SyncData convertJSONToSyncData(String body) {
		try {
			return fullMapper.readValue(body, SyncData.class);
		} catch (Exception e) {
			return new SyncData();
		}
	}
	
	private static String key(String appToken, String dbName) {
		return appToken + ":"+dbName;
	}
}
