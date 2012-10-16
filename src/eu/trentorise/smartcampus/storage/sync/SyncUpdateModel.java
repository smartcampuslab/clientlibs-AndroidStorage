package eu.trentorise.smartcampus.storage.sync;

import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;


public class SyncUpdateModel extends BatchModel {

	public static class UpdateModel extends BatchModel.UpdateModel {
		public boolean sync;
		public UpdateModel(BasicObject object, boolean upsert, boolean sync) {
			super(object, upsert);
			this.sync = sync;
		}
	}

}
