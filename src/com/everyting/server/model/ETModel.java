package com.everyting.server.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ETModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, Object> model = new HashMap<>();
	public Object get(String key){
		return model.get(key);
	}
	public Object set(String key, Object value){
		return model.put(key, value);
	}
	
	public Set<String> getKeySet(){
		return model.keySet();
	}
	
	public Iterator<String> getKeyIterator(){
		Set<String> modelKeySet =  model.keySet();
		return modelKeySet.iterator();
	}
	
	@Deprecated
	public Map<String, Object> getRawMap(){
		return model;
	}
	
	public int getSize() {
		return model.size();
	}
}
