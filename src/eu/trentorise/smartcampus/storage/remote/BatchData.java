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
package eu.trentorise.smartcampus.storage.remote;

import java.util.List;
import java.util.Map;

import eu.trentorise.smartcampus.storage.BasicObject;

/**
 * A container of the batch storage update to be sent over the network to the remote service. 
 * Contains references to deleted objects, objects created and updated.
 * Should not be instantiated directly.
 * @author raman
 *
 */
public class BatchData {

	private Map<String,List<BasicObject>> created;
	private Map<String,List<BasicObject>> updated;
	private Map<String,List<String>> deleted;
	
	public BatchData() {
		super();
	}

	public Map<String, List<BasicObject>> getCreated() {
		return created;
	}

	public void setCreated(Map<String, List<BasicObject>> created) {
		this.created = created;
	}

	public Map<String, List<BasicObject>> getUpdated() {
		return updated;
	}

	public void setUpdated(Map<String, List<BasicObject>> updated) {
		this.updated = updated;
	}

	public Map<String, List<String>> getDeleted() {
		return deleted;
	}

	public void setDeleted(Map<String, List<String>> deleted) {
		this.deleted = deleted;
	}
}
