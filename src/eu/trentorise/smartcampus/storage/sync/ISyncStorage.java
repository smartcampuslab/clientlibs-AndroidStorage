package eu.trentorise.smartcampus.storage.sync;

import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.ILocalStorage;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;

public interface ISyncStorage extends ILocalStorage {

	public static final String FIELD_TYPE = "_type";
	public static final String FIELD_SYNC_FLAG = "_sync";

	public void synchronize(String authToken, String host, String service) throws SecurityException, ConnectionException, DataException, ProtocolException, StorageConfigurationException; 

	public void setSyncVersion(long version); 
	
	public long getSyncVersion(); 

	public <T extends BasicObject> void update(T input, boolean upsert, boolean sync) throws DataException, StorageConfigurationException;

}
