package com.everyting.server.api;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.model.ETModel;
import com.everyting.server.util.FileIOManager;

public class APIRouter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public APIRouter() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index.html");
		requestDispatcher.forward(request, response);
	}

	private String getAPIOperation(String uri) {
		String subString = uri.substring(uri.indexOf("/api") + 5);
		return subString;
	}

	@SuppressWarnings("unused")
	private JSONArray getJSONData(String stringfied) {
		try {
			return new JSONArray(stringfied);
		} catch (JSONException e) {
			throw new RuntimeException("Error while parsing string to JSON Array:" + stringfied);
		}
	}

	private String getPostedData(HttpServletRequest request) {
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null){
				stringBuffer.append(line);
			}
			return stringBuffer.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error while reading posted data:" + e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter printWriter = response.getWriter();
		String requestURI = request.getRequestURI();
		JSONObject responseObject = new JSONObject();
		JSONObject responseDataObject = new JSONObject();
		try{	
			if (requestURI.contains("/api/")) {
				
				String opeartion = getAPIOperation(requestURI);
				
				//JSONArray postedData = getJSONData(getPostedData(request));
				String stringfiedJSONResponse = "[]";
	
				if (opeartion != null && opeartion.length() > 0) {
					/*Route query operation*/
					if(opeartion.equalsIgnoreCase("query")){
						String stringifiedRequest = getPostedData(request);
						JSONObject requestData = new JSONObject(stringifiedRequest);
						JSONObject apiManagerResponse = APIManager_Old.query(requestData);
						if(apiManagerResponse != null){
							stringfiedJSONResponse = apiManagerResponse.toString();
						}					
					}
					/*Route insert/save operation*/
					if(opeartion.equalsIgnoreCase("save")){
						String stringifiedRequest = getPostedData(request);
						JSONObject requestData = new JSONObject(stringifiedRequest);
						JSONObject apiManagerResponse = APIManager_Old.insert(requestData);
						if(apiManagerResponse != null){
							stringfiedJSONResponse = apiManagerResponse.toString();
						}
					}
					/*Route insert multiple/saveAll operation*/
					if(opeartion.equalsIgnoreCase("saveAll")){
						
					}
					
					/*Route update operation*/
					if(opeartion.equalsIgnoreCase("update")){
						
					}
					
					/*Route delete operation*/
					if(opeartion.equalsIgnoreCase("delete")){
						
					}
					
					/*Route login operation*/
					if(opeartion.equalsIgnoreCase("login")){
						String stringifiedRequest = getPostedData(request);
						JSONObject requestData = new JSONObject(stringifiedRequest);
						JSONObject loginResponse = APIManager_Old.login(request, requestData);
						if(loginResponse != null){
							stringfiedJSONResponse = loginResponse.toString();
						}
					}
					
					if(opeartion.equalsIgnoreCase("addTemplate")){
						 ETModel requestData = FileIOManager.getUploadedFileFormData(request);
						 JSONObject addTemplateResponse = APIManager_Old.addTemplate(requestData, request);
						 if(addTemplateResponse != null){
								stringfiedJSONResponse = addTemplateResponse.toString();
							}
					}
					
					/*Send the final repose*/
					printWriter.write(stringfiedJSONResponse);
					closeWriter(printWriter);
				}
			}
		}catch(RuntimeException runtimeException){
			
			try {
				responseObject.put("status", "error");
				responseDataObject.put("details", runtimeException.getMessage());
				responseObject.put("data", responseDataObject);
				printWriter.write(responseObject.toString());
				printWriter.flush();
				printWriter.close();
			} catch (JSONException e) {
				System.out.println("JSONException Error in APIRouter:" + e.getMessage());
			}
		} catch (JSONException e) {
			System.out.println("JSONException Error in APIRouter:" + e.getMessage());
		} 
	}
	
	private void closeWriter(Writer writer) throws IOException{
			writer.flush();
			writer.close();
	}
}
