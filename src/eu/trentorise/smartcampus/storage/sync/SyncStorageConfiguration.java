package eu.trentorise.smartcampus.storage.sync;

import java.io.Serializable;

import eu.trentorise.smartcampus.storage.db.StorageConfiguration;

public class SyncStorageConfiguration implements Serializable {
	private static final long serialVersionUID = -8651964347061941712L;

	private String host;
	private String service;
	
	private StorageConfiguration storageConfiguration;

	private long interval;
	
	
	public SyncStorageConfiguration(
			StorageConfiguration storageConfiguration,
			String host, 
			String service,
			long interval) { 
		super();
		this.storageConfiguration = storageConfiguration;
		this.host = host;
		this.service = service;
		this.interval = interval;
	}

	public String getHost() {
		return host;
	}

	public String getService() {
		return service;
	}

	public StorageConfiguration getStorageConfiguration() {
		return storageConfiguration;
	}

	public long getInterval() {
		return interval;
	}
}
