package com.everyting.server;

import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;

public class InterpreterHandler {
	
	public static void handleBeforeAPICall(String beforeInterpreter, ETModel requestData, ETModel responseData){
		List<ETModel> queryResponse = DBExecutor.rawExecuteQuery("SELECT * FROM ET_INTERPRETERS WHERE NAME =  ?", new Object[]{beforeInterpreter});
		if(queryResponse != null && queryResponse.size() > 0){
			ETModel interpreterInfo = queryResponse.get(0);
			String isActive = (String) interpreterInfo.get("isActive");
			String content = (String) interpreterInfo.get("content");
			if(isActive == null || "N".equalsIgnoreCase(isActive)){
				throw new ETException("InActiveInterpreter", "InterpreterHandler throws InActive Interpreter exception while handleBeforeAPICall", 
						beforeInterpreter + " such interpreter is in activated");
			}
			Interpreter interpreter = new Interpreter();
			try {
				interpreter.set("requestData", requestData);
				interpreter.set("responseData", responseData);
				interpreter.eval(content);
			} catch (EvalError e) {
				throw new ETException("InterpreterException", "InterpreterHandler throws InterpreterException while handleBeforeAPICall",
						beforeInterpreter + " throws " + e.getMessage());
			}
		}else throw new ETException("InvalidInterpreter", "InterpreterHandler throws Invalid Interpreter exception while handleBeforeAPICall", 
									beforeInterpreter + " no such interpreter exists");
	}
	public static void handleAfterAPICall(ETModel requestData, ETModel responseData){
		String afterInterpreter = (String) requestData.get("afterAPICall");
		List<ETModel> queryResponse = DBExecutor.rawExecuteQuery("SELECT * FROM ET_INTERPRETERS WHERE NAME =  ?", new Object[]{afterInterpreter});
		if(queryResponse != null && queryResponse.size() > 0){
			ETModel interpreterInfo = queryResponse.get(0);
			String isActive = (String) interpreterInfo.get("isActive");
			String content = (String) interpreterInfo.get("content");
			if(isActive == null || "N".equalsIgnoreCase(isActive)){
				throw new ETException("InActiveInterpreter", "APIManager throws InActive Interpreter exception while manageQuery", 
						afterInterpreter + " such interpreter is in activated");
			}
			Interpreter interpreter = new Interpreter();
			try {
				interpreter.set("responseData", responseData);
				interpreter.eval(content);
			} catch (EvalError e) {
				throw new ETException("InterpreterException", "APIManager throws InterpreterException while manageQuery",
						afterInterpreter + " throws " + e.getMessage());
			}
		}else throw new ETException("InvalidInterpreter", "APIManager throws Invalid Interpreter exception while manageQuery", 
				afterInterpreter + " no such interpreter exists");
		
	}
	
}
