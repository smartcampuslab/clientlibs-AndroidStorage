package eu.trentorise.smartcampus.storage.remote;

import java.util.List;
import java.util.Map;

import eu.trentorise.smartcampus.storage.BasicObject;

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
