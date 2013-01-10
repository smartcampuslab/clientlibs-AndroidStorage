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

import java.io.Serializable;

import eu.trentorise.smartcampus.storage.db.StorageConfiguration;

/**
 * Synchronizable storage configuration descriptor. Defines the configuration of the
 * storage itself, as well as the synchronization interval (in milliseconds; if less then 0 
 * means no periodic synchronization), and the service address parameters.
 * @author raman
 *
 */
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
