package com.everyting.server.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.everyting.server.DBExecutor;
import com.everyting.server.InterpreterHandler;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.FileIOManager;

public class APIManager {
	
	public static ETModel manageQuery(ETModel requestData){
		ETModel responseData = new ETModel();
		/*Check for before Interpreters*/
		String beforeAPICallInterpreter = (String) requestData.get("beforeAPICall");
		if(beforeAPICallInterpreter != null && beforeAPICallInterpreter.trim().length() > 0){
			InterpreterHandler.handleBeforeAPICall(beforeAPICallInterpreter, requestData, responseData);
		}
		if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			  List<ETModel> queryResult = DBExecutor.executeQuery(requestData);
			  responseData.set("data", queryResult);
		}
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			InterpreterHandler.handleAfterAPICall(requestData, responseData);
		}
		return responseData;
	}
	
	/*Insert/Update/Delete Call*/
	public static ETModel manageExecuteUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		/*Check for before Interpreters*/
		String beforeAPICallInterpreter = (String) requestData.get("beforeAPICall");
		if(beforeAPICallInterpreter != null && beforeAPICallInterpreter.trim().length() > 0){
			InterpreterHandler.handleBeforeAPICall(beforeAPICallInterpreter, requestData, responseData);
		}
		if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			  List<ETModel> updateResult = DBExecutor.executeUpdate(action, requestData);
			  responseData.set("data", updateResult);
		 }
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			
		}
		return responseData;
	}
	/*Batch Insert/Update/Delete Call*/
	public static ETModel manageBatchExecuteUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		/*Check for before Interpreters*/
		if(requestData.get("beforeAPICall") != null){
		}
		if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			  DBExecutor.batchExecuteUpdate(action, requestData);
			  responseData.set("data", new ETModel());
		 }
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			
		}
		return responseData;
	}
	public static ETModel manageFileFormDataUpload(HttpServletRequest request){
		ETModel responseData = new ETModel();
		ETModel extractedData = FileIOManager.extractFileFormData(request);
		ETModel requestData = (ETModel) extractedData.get("data");
		ETModel data = new ETModel();
		data.set("files", extractedData.get("files"));
		data.set("formData", requestData.get("data"));
		data.set("fileMapping", requestData.get("fileMapping"));
		/*Check for before Interpreters*/
		String beforeAPICallInterpreter = (String) requestData.get("beforeAPICall");
		if(beforeAPICallInterpreter != null && beforeAPICallInterpreter.trim().length() > 0){
			InterpreterHandler.handleBeforeAPICall(beforeAPICallInterpreter, data, responseData);
		}
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			InterpreterHandler.handleAfterAPICall(data, responseData);
		}
		return data; 
	}
}
