package eu.trentorise.smartcampus.storage.remote;

import java.util.Collection;
import java.util.List;

import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.IStorage;

public interface IRemoteStorage extends IStorage {

	public void setConfig(String authToken, String host, String service);
	
	public <T extends BasicObject> T create(T input) throws DataException, ConnectionException, ProtocolException, SecurityException;
	public <T extends BasicObject> void update(T input, boolean upsert) throws DataException, ConnectionException, ProtocolException, SecurityException;
	public void delete(String id, Class<? extends BasicObject> cls)  throws DataException, ConnectionException, ProtocolException, SecurityException;
	public void batch(List<BatchModel> mdls)  throws DataException, ConnectionException, ProtocolException, SecurityException;
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException;
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException;

	public  <T extends BasicObject> Collection<T> searchObjects(Object query, Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException;

}