package eu.trentorise.smartcampus.storage;

import java.util.Collection;

import android.database.Cursor;

public interface ILocalStorageWithPaging extends IStorageWithPaging {

	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls, int offset, int limit) throws DataException, StorageConfigurationException;
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit) throws DataException, StorageConfigurationException;
	public <T extends BasicObject> Collection<T> query(Class<T> cls, String selection, String[] args, int offset, int limit, String orderBy) throws DataException, StorageConfigurationException;

	public Cursor rawQuery(String query, String[] args, int offset, int limit)
			throws DataException, StorageConfigurationException;

}