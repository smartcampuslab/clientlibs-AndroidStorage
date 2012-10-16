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
