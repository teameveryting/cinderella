package com.everyting.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;

public class DataHandler {
	
	public static ETModel getRequestData(HttpServletRequest request){
		
		try {
			JSONObject jsonObject = new JSONObject(DataHandler.getPostedData(request));
			return DataHandler.toETModel(jsonObject);
		} catch (JSONException e) {
			throw new ETException("ET-JSON-000", "DataHandler throws JSONException while getRequestData" 
					, e.getMessage());
		}
	}
	public static JSONObject toJSONResponse(ETModel responseModel){
		JSONObject response = new JSONObject();
		try {
			response.put("status", "success");
			response.put("data", DataHandler.toJSONObject(responseModel));
		} catch (JSONException e) {
			throw new ETException("ET-JSON-001", "DataHandler throws JSONException while toJSONResponse" 
					, e.getMessage());
		}
		return response;
	}
	public static JSONObject toJSONResponse(List<ETModel> etModelList){
		JSONObject response = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			for(int i=0; i < etModelList.size(); i++){
				ETModel etModel = etModelList.get(i);
				jsonArray.put(DataHandler.toJSONObject(etModel));
			}
			response.put("status", "success");
			response.put("data", jsonArray);
		} catch (JSONException e) {
			throw new ETException("ET-JSON-002", "DataHandler throws JSONException while toJSONResponse" 
					, e.getMessage());
		}
		return response;
	}
	public static JSONObject toJSONResponse(String errorTitle, String errorMsg){
		JSONObject response = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			response.put("status", "error");
			data.put("errorTitle", errorTitle);
			data.put("details", errorMsg);
			response.put("data", data);
		} catch (JSONException e) {
			throw new ETException("ET-JSON-003", "DataHandler throws JSONException while getResponseException" 
					, e.getMessage());
		}
		return response;
	}	
	@SuppressWarnings("unchecked")
	public static ETModel toETModel(JSONObject jsonObject){
		ETModel etModel = new ETModel();
		try {
			Iterator<String> jsonIterator = (Iterator<String>) jsonObject.keys();
			while (jsonIterator.hasNext()) {
				String key = jsonIterator.next();
				Object value = jsonObject.get(key);
				if(value instanceof JSONObject){
					value = toETModel((JSONObject) value);
				}
				if(value instanceof JSONArray){
					value = toETModel((JSONArray) value);
				}
				etModel.set(key, value);
			}
		} catch (JSONException e) {
			throw new ETException("ET-JSON-004", "DataHandler throws JSONException while toETModel" 
					, e.getMessage());
		}
		return etModel;
	}
	public static Object[] toETModel(JSONArray jsonArray){
		Object[] objectArray = new Object[jsonArray.length()];
		Object arrayItem = null;
		try{
			for(int i=0; i <jsonArray.length(); i++){
				arrayItem = jsonArray.get(i);
				if(arrayItem instanceof JSONObject){
					arrayItem = toETModel((JSONObject)arrayItem);
				}else if(arrayItem instanceof JSONArray){
					arrayItem = toETModel((JSONArray)arrayItem);
				}
				objectArray[i] = arrayItem;
			}
		} catch (JSONException e) {
			throw new ETException("ET-JSON-005", "DataHandler throws JSONException while toETModel" 
					, e.getMessage());
		}
		return objectArray;
	}
	@SuppressWarnings("unchecked")
	public static JSONObject toJSONObject(ETModel etModel){
		JSONObject jsonObject = new JSONObject();
		try {
			Iterator<String> iterator = etModel.getKeyIterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				Object value = etModel.get(key);
				if(value instanceof ETModel){
					value = (JSONObject) toJSONObject((ETModel)value);
				}
				if(value instanceof List<?>){
					JSONArray jsonArray = new JSONArray();
					List<ETModel> listModel = (List<ETModel>) value;
					for(ETModel entry: listModel){
						jsonArray.put(toJSONObject(entry));
					}
					value = jsonArray;
				}
				if(value instanceof Object[]){
					 Object[] objectsArray = (Object[]) value;
					JSONArray jsonArray = new JSONArray();
					 if(objectsArray != null){
						for(int i=0; i< objectsArray.length; i++){
							jsonArray.put(objectsArray[i]);
						  }
					 }
					 value = jsonArray;
				}
				jsonObject.put(key, value);
			}
		} catch (JSONException e) {
			throw new ETException("ET-JSON-005", "DataHandler throws JSONException while toJSONObject" 
					, e.getMessage());
		}
		return jsonObject;
	}
	private static String getPostedData(HttpServletRequest request) {
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null){
				stringBuffer.append(line);
			}
			return stringBuffer.toString();
		} catch (IOException e) {
			throw new ETException("ET-IO-000", "DataHandler throws IOException while getPostedData:" 
										+ (e.getMessage() !=null? e.getMessage():""));
		}
	}
	/*Ex: cendereallaJsonData --> CENDERELLA_JSON_DATA*/
	public static String toUpperSplitCase(String raw){
		if(raw == null){return "";}
		if(raw.contains("_") || !raw.matches(".*[a-z].*")){return raw.toUpperCase();}
		/* RegExp with zero-width positive lookahead - 
		 it finds UpperCase letters but doesn't include them into delimiter*/	
		StringBuilder caseBuilder = new StringBuilder();
		String[] capitalSpliier = raw.split("(?=\\p{Upper})");
			for(int i=0; i < capitalSpliier.length; i++){
				String group = capitalSpliier[i];
				if(caseBuilder.length() > 0){
					caseBuilder.append("_");
					caseBuilder.append(group.toUpperCase());
				}else{
					caseBuilder.append(group.toUpperCase());
				}
			}
			return caseBuilder.toString();
	}
	
	/*Ex: CENDERELLA_JSON_DATA --> cendereallaJsonData*/
	public static String toCamelCase(String raw){
		StringBuilder caseBuilder = new StringBuilder();
		String[] splitter = raw.split("_");
		for(int i=0; i < splitter.length; i++){
			String group = splitter[i];
			if(caseBuilder.length() > 0){
				caseBuilder.append(group.substring(0,1).toUpperCase() + group.substring(1).toLowerCase());
			}else{
				caseBuilder.append(group.toLowerCase()); 
			}
		}
		return caseBuilder.toString();
	}	
	public  static String hashPassword(String password){
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
	public  static boolean isPasswordMatches(String dbPassword, String given){
		return BCrypt.checkpw(given, dbPassword);
	}
	public static void main(String[] args) throws JSONException {
		//System.out.println(toUpperSplitCase("CENDERELLAJSONDATA"));
		ETModel etModel1 = new ETModel();
		ETModel etModel2 = new ETModel();
		ETModel etModel3 = new ETModel();
		ETModel etModel4 = new ETModel();
		
		etModel4.set("model4-key1", "model4");
		etModel4.set("model4-key2", "for test");
		etModel3.set("model3-key1", "last");
		etModel3.set("model3-key2", "child");
		Object[] innnerArray = new Object[]{"innner","array"};
		etModel2.set("model2-key1", 5656);
		etModel2.set("model2-key2", innnerArray);
		etModel2.set("model2-key3", "test");
		List<ETModel> list = new ArrayList<>();
		list.add(etModel2);
		Object[] objectsArray = new Object[]{"test", "object", innnerArray};
		etModel1.set("model1-key1",objectsArray);
		etModel1.set("model1-key2", 12.056);
		etModel1.set("model1-key3", list);
		etModel1.set("model1-key4", 78878787);
		List<ETModel> toJSonArray = new ArrayList<>();
		toJSonArray.add(etModel1);
		toJSonArray.add(etModel4);
		
		JSONObject jsonObject = toJSONResponse(toJSonArray);
		System.out.println(jsonObject.toString());
		
		String rawInsertSQL = "{"
				+ " \"dataSource\": \"etTemplateStructure\", "
				+ "\"valueMap\":[{uid:\"7889-552898-8989\","
				+ 				"type:\"folder\","
				+				"title:\"Silde Menu with tabs\","
				+				"parentUid:\"8585-989-6363\""
				+				"},"
				+ 				"[{type:\"myNew Array1\"}, [{type2:\"myNew Array2\"}]]"
				+ 				"]"
				+ "}";
		JSONObject jsonObject2 = new JSONObject(rawInsertSQL);
		ETModel etModel = DataHandler.toETModel(jsonObject2);
		Object[] objectArray = (Object[]) etModel.get("valueMap");
		Object[] objectArray2 = (Object[])objectArray[1];
	    Object[] objectArray3 = (Object[])objectArray2[1];
		System.out.println(((ETModel)objectArray3[0]).get("type2"));
	}
}
