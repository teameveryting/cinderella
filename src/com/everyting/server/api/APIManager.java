package com.everyting.server.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.everyting.server.DBExecutor;
import com.everyting.server.InterpreterHandler;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.FileIOManager;

public class APIManager {
	
	public static Object manageQuery(ETModel requestData){
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
		String afterCallInterpreter = (String) requestData.get("afterAPICall");
		if(afterCallInterpreter != null && afterCallInterpreter.trim().length() > 0){
			InterpreterHandler.handleAfterAPICall(requestData, responseData);
		}
		return responseData.get("data");
	}
	
	/*Insert/Update/Delete Call*/
	public static Object manageExecuteUpdate(String action, ETModel requestData){
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
		String afterCallInterpreter = (String) requestData.get("afterAPICall");
		if(afterCallInterpreter != null && afterCallInterpreter.trim().length() > 0){
			InterpreterHandler.handleAfterAPICall(requestData, responseData);
		}
		return responseData.get("data");
	}
	/*Batch Insert/Update/Delete Call*/
	public static Object manageBatchExecuteUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		/*Check for before Interpreters*/
		if(requestData.get("beforeAPICall") != null){
		}
		if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			responseData.set("data",  DBExecutor.batchExecuteUpdate(action, requestData));
		 }
		/*Check for after InterPreters*/
		String afterCallInterpreter = (String) requestData.get("afterAPICall");
		if(afterCallInterpreter != null && afterCallInterpreter.trim().length() > 0){
			InterpreterHandler.handleAfterAPICall(requestData, responseData);
		}
		return responseData.get("data");
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
