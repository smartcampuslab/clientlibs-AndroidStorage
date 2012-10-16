package eu.trentorise.smartcampus.storage.db;

import eu.trentorise.smartcampus.storage.BasicObject;
import android.content.ContentValues;

public class BatchUpdateModel {

	public static class DeleteModel extends BatchUpdateModel {
		public String id;
		public Class<? extends BasicObject> cls;
		public DeleteModel(String id, Class<? extends BasicObject> cls) {
			super();
			this.id = id;
			this.cls = cls;
		}
	}
	public static class CreateModel extends BatchUpdateModel {
		public  BasicObject object;
		public ContentValues extensions;
		public CreateModel(BasicObject object, ContentValues extensions) {
			super();
			this.object = object;
			this.extensions = extensions;
		}
	}
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
