package eu.trentorise.smartcampus.storage.db;

import java.io.Serializable;

import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;

public interface StorageConfiguration extends Serializable {

	Class<? extends BasicObject>[] getClasses();
	String getTableName(Class<? extends BasicObject> cls) throws StorageConfigurationException;
	<T extends BasicObject> BeanStorageHelper<T> getStorageHelper(Class<T> cls) throws StorageConfigurationException;
}
