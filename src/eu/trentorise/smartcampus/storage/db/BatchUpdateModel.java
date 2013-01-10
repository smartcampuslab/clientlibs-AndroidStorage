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
package eu.trentorise.smartcampus.storage.db;

import eu.trentorise.smartcampus.storage.BasicObject;
import android.content.ContentValues;

/**
 * Prototype for the batch modification element descriptors adopted to android storage APIs. 
 * Should not be instantiated directly.
 * @author raman
 *
 */
public class BatchUpdateModel {

	/**
	 * Prototype for the batch 'delete' element descriptor adopted to android storage APIs. 
	 * Should not be instantiated directly.
	 * @author raman
	 *
	 */
	public static class DeleteModel extends BatchUpdateModel {
		public String id;
		public Class<? extends BasicObject> cls;
		public DeleteModel(String id, Class<? extends BasicObject> cls) {
			super();
			this.id = id;
			this.cls = cls;
		}
	}
	/**
	 * Prototype for the batch 'create' element descriptor adopted to android storage APIs. 
	 * Should not be instantiated directly.
	 * @author raman
	 *
	 */
	public static class CreateModel extends BatchUpdateModel {
		public  BasicObject object;
		public ContentValues extensions;
		public CreateModel(BasicObject object, ContentValues extensions) {
			super();
			this.object = object;
			this.extensions = extensions;
		}
	}
	/**
	 * Prototype for the batch 'update' element descriptor adopted to android storage APIs. 
	 * Should not be instantiated directly.
	 * @author raman
	 *
	 */
	public static class UpdateModel extends BatchUpdateModel {
		public  BasicObject object;
		public ContentValues extensions;
		public boolean upsert;
		public UpdateModel(BasicObject object, ContentValues extensions,
				boolean upsert) {
			super();
			this.object = object;
			this.extensions = extensions;
			this.upsert = upsert;
		}
	}
}
