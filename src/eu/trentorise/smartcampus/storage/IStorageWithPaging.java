package eu.trentorise.smartcampus.storage;

import java.util.Collection;

public interface IStorageWithPaging {

	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls, int offset, int limit) throws Exception;

}