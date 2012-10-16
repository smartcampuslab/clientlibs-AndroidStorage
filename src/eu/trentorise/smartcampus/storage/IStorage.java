package eu.trentorise.smartcampus.storage;

import java.util.Collection;
import java.util.List;

public interface IStorage {

	public <T extends BasicObject> T create(T input) throws Exception;
	public <T extends BasicObject> void update(T input, boolean upsert) throws Exception;
	public void delete(String id, Class<? extends BasicObject> cls) throws Exception;
	public void batch(List<BatchModel> mdls) throws Exception;
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws Exception;
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws Exception;

}