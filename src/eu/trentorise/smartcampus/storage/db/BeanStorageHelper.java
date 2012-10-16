package eu.trentorise.smartcampus.storage.db;

import java.util.Map;

import eu.trentorise.smartcampus.storage.BasicObject;

import android.content.ContentValues;
import android.database.Cursor;

public interface BeanStorageHelper<T extends BasicObject> {

	T toBean(Cursor cursor);
	ContentValues toContent(T bean);
	Map<String,String> getColumnDefinitions();
	boolean isSearchable();

}
