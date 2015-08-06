package com.everyting.server.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.db.ServiceExecutor;
import com.everyting.server.model.ETFolderModel;
import com.everyting.server.model.ETModel;
import com.everyting.server.session.SessionManager;
import com.everyting.server.util.FileIOManager;
import com.everyting.server.util.SQLConstructor;


public class APIManager_Old {

	private static final String ET_USERS_QUERY = "SELECT USER_ID,USER_NAME,DISPLY_NAME,EMAIL_ADDRESS,HASH_PASSWORD,"
			+ "AVATAR_URL,LAST_LOGIN_DATE,LAST_LOGIN_IP"
			+ " FROM ET_USERS WHERE EMAIL_ADDRESS= ?";
	//private static final String ET_USERS_LOGIN_UPDATE = "UPDATE ET_USERS SET LAST_LOGIN_DATE = ?,LAST_LOGIN_IP=?";
	 
	public static JSONObject query(JSONObject requestData){
		JSONObject queryResponse = new JSONObject();
		JSONObject dataObject = new JSONObject();
		try {
			ServiceExecutor serviceExecutor = new ServiceExecutor();
			StringBuilder queryBuilder = new StringBuilder();
				String tableName = requestData.getString("dataSource");
				//String whereCluase = requestInfo.getString("whereCluase");
				
				/*Construct the SQL query here!*/
				queryBuilder.append("SELECT * FROM ");
				queryBuilder.append(tableName);
			
			queryResponse.put("status", "success");
			dataObject.put("details", serviceExecutor.executeQuery(queryBuilder.toString()));
			queryResponse.put("data", dataObject);
			return queryResponse;
		} catch (JSONException e) {
			throw new RuntimeException("APIManager throws JSONException while quering:" + e.getMessage());
		}
	}
	
	public static JSONObject login(HttpServletRequest request, JSONObject userInfo){
		JSONObject loginResponse = new JSONObject();
		JSONObject dataObject = new JSONObject();
		
		try {
			ServiceExecutor serviceExecutor = new ServiceExecutor();
			List<Object> preparedPamas = new ArrayList<>();
				String emailAddress = (String) userInfo.get("emailAddress");
				String rawPassword = (String) userInfo.get("password");
				/*Server side user credentials validation here!*/
				if(emailAddress == null || rawPassword == null){
					throw new RuntimeException("This should not happen but fill all required fields");
				}
				if(!EmailValidator.getInstance().isValid(emailAddress)){
					throw new RuntimeException("Something bad happen, please provide a valid email not a spam");
				}
				preparedPamas.add(userInfo.get("emailAddress"));
				ETModel eTModel = new ETModel();
				eTModel.put("sql", ET_USERS_QUERY);
				eTModel.put("preparedParams", preparedPamas);
				JSONArray queryResults = serviceExecutor.executeQuery(eTModel);
				if(queryResults != null && queryResults.length() >0){
					JSONObject dbUserInfo = (JSONObject) queryResults.get(0);
					String dbHashedPassword = dbUserInfo.get("hashPassword").toString();
					if(ServiceExecutor.isPasswordMatches(dbHashedPassword, rawPassword)){
						/*Create user session here!*/
						SessionManager.createUserSession(request, dbUserInfo);
						/*Update user info in ET_USERS table*/
						/*Return a success status*/
						loginResponse.put("status", "success");
						dataObject.put("url", request.getServletContext().getContextPath() +"/index.html");
						dataObject.put("details", "Please wait while you redirect!");
						loginResponse.put("data", dataObject);
					}else{
						loginResponse.put("status", "error" );
						dataObject.put("details", "Please verify the email and password combination!");
						loginResponse.put("data", dataObject);
					}
				}else{
					loginResponse.put("status", "error" );
					dataObject.put("details", "Invalid email address!");
					loginResponse.put("data", dataObject);
				}
			return loginResponse;
		} catch (JSONException e) {
			throw new RuntimeException("APIManager throws JSONException:" + e.getMessage());
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static JSONObject insert(JSONObject requestData){
		JSONObject excuteResponse = new JSONObject();
		JSONObject dataObject = new JSONObject();
		try {
				StringBuilder insertSql = new StringBuilder("INSERT INTO " + requestData.getString("dataSource") +"(");
				StringBuilder insertBulider = new StringBuilder();
				StringBuilder paramBuilder = new StringBuilder();
				Iterator<String> requestDataKeys = ((JSONObject) requestData.get("data")).keys();
				List<Object> preparedPamas = new ArrayList<>();
				while(requestDataKeys.hasNext()){
					String key = requestDataKeys.next();
					preparedPamas.add(((JSONObject) requestData.get("data")).get(key));
					if(insertBulider.length() > 0){
						insertBulider.append("," + key);
					}else{
						insertBulider.append(key);
					}
					if(paramBuilder.length() > 0){
						paramBuilder.append(",?");
					}else{
						paramBuilder.append("?");
					}
				}
				insertSql.append(insertBulider.toString() + ") VALUES(");
				insertSql.append(paramBuilder.toString());
				insertSql.append(")");
				int affectedRows = ServiceExecutor.executeUpdate(insertSql.toString(), preparedPamas);
				excuteResponse.put("status", "success" );
				dataObject.put("details", affectedRows );
				excuteResponse.put("data", dataObject);
				return excuteResponse;
		} catch (JSONException e) {
			throw new RuntimeException("APIManager throws JSONException while insert:" + e.getMessage());
		}
	}
	
	public static JSONObject insert(JSONArray requestData){
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject addTemplate(ETModel eTModel, HttpServletRequest request ){
		JSONObject addTemplateResponse = new JSONObject();
		JSONObject dataObject = new JSONObject();
		JSONObject formData = (JSONObject) eTModel.get("data");
		JSONObject fileMapping = (JSONObject) eTModel.get("fileMapping");
		Map<String, InputStream>  uplodedFilesMap = (Map<String, InputStream> ) eTModel.get("files");
		if(formData != null && formData.length() > 0){
			try {
				String projectFileName = fileMapping.getString("project");
				InputStream fileStream = uplodedFilesMap.get(projectFileName);
				List<ETFolderModel>  projectStructure = FileIOManager.readUploadedZip(fileStream, request);
				ETModel temaplateResponseModel = SQLConstructor.templatesSQL(formData);
				int temaplteRowsAffted = ServiceExecutor.executeUpdate(temaplateResponseModel);
				if(temaplteRowsAffted > 0){
				}
				//List<ETModel> templateStructureSQL =   SQLConstructor.templateStructureSQL(projectStructure, );
				
				addTemplateResponse.put("status", "success" );
				dataObject.put("details", formData.toString() +"\n" );
				addTemplateResponse.put("data", dataObject);
			} catch (JSONException e) {
				throw new RuntimeException("APIManager throws JSONException while addTemplate:" + e.getMessage());
			}
		}
		return addTemplateResponse;
	}
	
}
