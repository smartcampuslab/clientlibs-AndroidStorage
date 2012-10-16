package eu.trentorise.smartcampus.storage.sync;

import java.util.Collection;

import android.content.Context;
import android.database.Cursor;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.ILocalStorageWithPaging;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;

public class SyncStorageWithPaging extends SyncStorage implements ILocalStorageWithPaging {
	
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
