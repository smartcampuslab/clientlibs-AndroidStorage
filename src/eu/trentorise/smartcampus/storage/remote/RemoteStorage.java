package eu.trentorise.smartcampus.storage.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import eu.trentorise.smartcampus.android.common.Utils;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
import eu.trentorise.smartcampus.storage.BatchModel;
import eu.trentorise.smartcampus.storage.BatchModel.CreateModel;
import eu.trentorise.smartcampus.storage.BatchModel.DeleteModel;
import eu.trentorise.smartcampus.storage.BatchModel.UpdateModel;
import eu.trentorise.smartcampus.storage.DataException;

public class RemoteStorage implements IRemoteStorage {

	private static final String OBJECTS_PATH = "objects";
	private static final String SEARCH_PATH = "objects";
	private String host;
	private String service;
	private String appToken;
	private String authToken;

	private ProtocolCarrier mProtocolCarrier;

	public RemoteStorage(Context ctx, String appToken, String authToken, String host, String service) {
		super();
		this.appToken = appToken;
		this.authToken = authToken;
		this.host = host;
		this.service = service;
		mProtocolCarrier = new ProtocolCarrier(ctx, appToken);
	}

	private static String ensureServiceString(String service) {
		if (!service.endsWith("/")) return service + "/";
		return service;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasicObject> T create(T input) throws DataException, ConnectionException, ProtocolException, SecurityException {
		String json = Utils.convertToJSON(input);
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+input.getClass().getCanonicalName());
		req.setMethod(Method.POST);
		req.setBody(json);
		MessageResponse res = mProtocolCarrier.invokeSync(req, appToken, authToken);
		T object = (T)Utils.convertJSONToObject(res.getBody(), input.getClass());
		if (object == null) {
			throw new DataException("Cannot parse remotely created object: "+res.getBody());
		}
		return object;
	}

	@Override
	public <T extends BasicObject> void update(T input, boolean upsert) throws DataException, ConnectionException, ProtocolException, SecurityException {
		if (input.getId() == null) {
			throw new DataException("object Id cannot be null");
		}
		String json = Utils.convertToJSON(input);
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+input.getClass().getCanonicalName()+"/"+input.getId());
		req.setMethod(Method.PUT);
		req.setBody(json);
		mProtocolCarrier.invokeSync(req, appToken, authToken);
	}

	@Override
	public void delete(String id, Class<? extends BasicObject> cls) throws DataException, ConnectionException, ProtocolException, SecurityException {
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+cls.getCanonicalName()+"/"+id);
		req.setMethod(Method.DELETE);
		mProtocolCarrier.invokeSync(req, appToken, authToken);
	}

	@Override
	public void batch(List<BatchModel> mdls) throws DataException, ConnectionException, ProtocolException, SecurityException {
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+OBJECTS_PATH);
		req.setMethod(Method.POST);
		req.setBody(Utils.convertToJSON(createBatchData(mdls)));
		mProtocolCarrier.invokeSync(req, appToken, authToken);
	}

	private BatchData createBatchData(List<BatchModel> mdls) {
		BatchData data =new BatchData();
		Map<String,List<BasicObject>> created = new HashMap<String, List<BasicObject>>();
		Map<String,List<BasicObject>> updated = new HashMap<String, List<BasicObject>>();
		Map<String,List<String>> deleted = new HashMap<String, List<String>>();

		if (mdls != null) {
			String key = null;
			for (BatchModel bm: mdls) {
				if (bm instanceof CreateModel) {
					CreateModel create = (CreateModel)bm;
					key = create.object.getClass().getCanonicalName();
					List<BasicObject> list = created.get(key);
					if (list == null) {
						list = new ArrayList<BasicObject>();
						created.put(key, list);
					}
					list.add(create.object);
				}
				if (bm instanceof UpdateModel) {
					UpdateModel update = (UpdateModel)bm;
					key = update.object.getClass().getCanonicalName();
					List<BasicObject> list = updated.get(key);
					if (list == null) {
						list = new ArrayList<BasicObject>();
						updated.put(key, list);
					}
					list.add(update.object);
				}
				if (bm instanceof DeleteModel) {
					DeleteModel delete = (DeleteModel)bm;
					key = delete.cls.getCanonicalName();
					List<String> list = deleted.get(key);
					if (list == null) {
						list = new ArrayList<String>();
						deleted.put(key, list);
					}
					list.add(delete.id);
				}
			}
		}
		
		data.setCreated(created);
		data.setUpdated(updated);
		data.setDeleted(deleted);
		return data;
	}
	
	@Override
	public <T extends BasicObject> T getObjectById(String id, Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException {
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+cls.getCanonicalName()+"/"+id);
		req.setMethod(Method.GET);
		MessageResponse res = mProtocolCarrier.invokeSync(req, appToken, authToken);
		T object = (T)Utils.convertJSONToObject(res.getBody(), cls);
		if (object == null) {
			throw new DataException("Cannot parse remotely created object: "+res.getBody());
		}
		return object;
	}

	@Override
	public <T extends BasicObject> Collection<T> getObjects(Class<T> cls) throws DataException, ConnectionException, ProtocolException, SecurityException {
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+cls.getCanonicalName());
		req.setMethod(Method.GET);
		MessageResponse res = mProtocolCarrier.invokeSync(req, appToken, authToken);
		List<T> objects = (List<T>)Utils.convertJSONToObjects(res.getBody(), cls);
		if (objects == null) {
			throw new DataException("Cannot parse remotely created object: "+res.getBody());
		}
		return objects;
	}

	@SuppressWarnings("unchecked")
	@Override
	public  <T extends BasicObject> Collection<T> searchObjects(Object query, Class<T> inCls) throws DataException, ConnectionException, ProtocolException, SecurityException {
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		MessageRequest req = new MessageRequest(host, ensureServiceString(service)+SEARCH_PATH);
		req.setMethod(Method.GET);
		if (query != null) {
			String queryStrObject = Utils.convertToJSON(query);
			String queryString = "filter="+queryStrObject;
			req.setQuery(queryString);
		}
		MessageResponse res = mProtocolCarrier.invokeSync(req, appToken, authToken);
		Map<String,List<Map<String,Object>>> map = Utils.convertJSON(res.getBody(), new TypeReference<Map<String,List<Map<String,Object>>>>() {});
		ArrayList<T> objects = new ArrayList<T>();
		if (map != null) {
			for (String key : map.keySet()) {
				List<Map<String,Object>> protos = map.get(key);
				if (protos != null && key.equals(inCls.getCanonicalName())) {
					try {
						Class<T> cls = (Class<T>)Thread.currentThread().getContextClassLoader().loadClass(key);
						for (Map<String,Object> proto : protos) {
							objects.add((T)Utils.convertObjectToData(cls, proto));
						}
					} catch (Exception e) {
						throw new DataException("Unknown data type: "+key, e);
					}
				}
			}
		}
		return objects;
	}

	
}