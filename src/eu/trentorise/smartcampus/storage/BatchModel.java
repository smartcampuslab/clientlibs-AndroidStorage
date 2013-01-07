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


public class BatchModel {

	public static class DeleteModel extends BatchModel {
		public String id;
		public Class<? extends BasicObject> cls;
		public DeleteModel(String id, Class<? extends BasicObject> cls) {
			super();
			this.id = id;
			this.cls = cls;
		}
	}
	public static class CreateModel extends BatchModel {
		public BasicObject object;
		public CreateModel(BasicObject object) {
			super();
			this.object = object;
		}
	}
	public static class UpdateModel extends BatchModel {
		public  BasicObject object;
		public boolean upsert;
		public UpdateModel(BasicObject object, boolean upsert) {
			super();
			this.object = object;
			this.upsert = upsert;
		}
	}
}
