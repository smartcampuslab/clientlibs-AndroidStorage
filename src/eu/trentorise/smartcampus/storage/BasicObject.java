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
package eu.trentorise.smartcampus.storage;

import java.io.Serializable;

/**
 * The base storable JavaBean object. Defines the minimal set of fields necessary for the 
 * object-based storage infrastructure. Specifically, defines
 * <ul>
 * <li>id - unique object identificator</li>
 * <li>version - current object version</li>
 * <li>update time - timestamp of the last object modification</li>
 * </ul>
 * @author raman
 *
 */
@SuppressWarnings("serial")
public class BasicObject implements Serializable {

	private String id;
	private long version;
	private long updateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
