package eu.trentorise.smartcampus.storage.sync;

import java.util.List;
import java.util.Map;

import android.util.Pair;

public class SyncData {

	private long version;
	private Map<String,List<Object>> updated;
	private Map<String,List<String>> deleted;
	private List<Pair<String, String>> syncElements;
	
	public SyncData() {
		super();
	}


	public long getVersion() {
		return version;
	}


	public void setVersion(long version) {
		this.version = version;
	}


	public Map<String, List<Object>> getUpdated() {
		return updated;
	}


	public void setUpdated(Map<String, List<Object>> updated) {
		this.updated = updated;
	}


	public Map<String, List<String>> getDeleted() {
		return deleted;
	}


	public void setDeleted(Map<String, List<String>> deleted) {
		this.deleted = deleted;
	}

	public List<Pair<String, String>> getSyncElements() {
		return syncElements;
	}

	public void setSyncElements(List<Pair<String, String>> syncElements) {
		this.syncElements = syncElements;
	}

}
