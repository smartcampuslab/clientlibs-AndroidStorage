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

public class Utils {
	private static final String PREF_SYNC_STORAGE_VERSIONS = "PrefSyncStorageObjectVersions";
	private static final String PREF_SYNC_STORAGE_DB_VERSIONS = "PrefSyncStorageDBVersions";
	private static final String PREF_SYNC_STORAGE_DB_NAMES = "PrefSyncStorageDBNames";

	public static int getDBVersion(Context mContext, String appToken) {
		return mContext.getSharedPreferences(PREF_SYNC_STORAGE_DB_VERSIONS, Context.MODE_PRIVATE).getInt(appToken, 0);
	}
	public static long getObjectVersion(Context mContext, String appToken) {
		return mContext.getSharedPreferences(PREF_SYNC_STORAGE_VERSIONS, Context.MODE_PRIVATE).getLong(appToken, -1);
	}
	public static String getDBName(Context mContext, String appToken) {
		return mContext.getSharedPreferences(PREF_SYNC_STORAGE_DB_NAMES, Context.MODE_PRIVATE).getString(appToken, "_DB");
	}
	public static void writeObjectVersion(Context mContext, String appToken, long version) {
		mContext.getSharedPreferences(PREF_SYNC_STORAGE_VERSIONS, Context.MODE_PRIVATE).edit().putLong(appToken, version).commit();
	}
	public static void writeDBVersion(Context mContext, String appToken, int version) {
		mContext.getSharedPreferences(PREF_SYNC_STORAGE_DB_VERSIONS, Context.MODE_PRIVATE).edit().putInt(appToken, version).commit();
	}
	public static void writeDBName(Context mContext, String appToken, String name) {
		mContext.getSharedPreferences(PREF_SYNC_STORAGE_DB_NAMES, Context.MODE_PRIVATE).edit().putString(appToken, name).commit();
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
	@SuppressWarnings("unchecked")
	public static Map<String,Object> convertJSONToData(String o)  {
		try {
			return fullMapper.readValue(o, Map.class);
		} catch (Exception e) {
			return null;
		}
	}

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
	public static SyncData convertJSONToSyncData(String body) {
		try {
			return fullMapper.readValue(body, SyncData.class);
		} catch (Exception e) {
			return new SyncData();
		}
	}
}
