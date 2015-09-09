package com.everyting.server.api;

import java.util.List;

import com.everyting.server.InterpreterHandler;
import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;

public class APIManager {
	
	public static ETModel manageQuery(ETModel requestData){
		ETModel responseData = new ETModel();
		/*Check for before Interpreters*/
		if(requestData.get("beforeAPICall") != null && ((String)requestData.get("beforeAPICall")).trim().length() > 0){
			InterpreterHandler.handleBeforeAPICall(requestData, responseData);
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
		if(requestData.get("beforeAPICall") != null){
			InterpreterHandler.handleBeforeAPICall(requestData, responseData);
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
			  List<ETModel> batchResults = DBExecutor.batchExecuteUpdate(action, requestData);
			  responseData.set("data", batchResults);
		 }
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			
		}
		return responseData;
	}
	
	
}
