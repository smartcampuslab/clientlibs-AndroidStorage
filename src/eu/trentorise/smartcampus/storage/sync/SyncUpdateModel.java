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

import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;

/**
 * Extends batch update descriptor for update operation for the storage that
 * is synchronized remotely. Specifically, allows for defining
 * that the update of the object should be synchronized to remote storage or not.
 * @author raman
 *
 */
public class SyncUpdateModel extends BatchModel {

	public static class UpdateModel extends BatchModel.UpdateModel {
		public boolean sync;
		public UpdateModel(BasicObject object, boolean upsert, boolean sync) {
			super(object, upsert);
			this.sync = sync;
		}
	}

}
