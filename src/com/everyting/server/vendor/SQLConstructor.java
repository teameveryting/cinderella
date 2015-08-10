package com.everyting.server.vendor;

import com.everyting.server.model.ETModel;

public interface SQLConstructor {
	//For request, response data formats please refer EtingDataFormats.txt
	//Intended for Interpreter.java only, developer has to ensure, 
	//code(SQL syntax) will work for all types DBs
	
	/*Query Call*/
	public  ETModel getQuerySQL(ETModel requestData);

	/*Execute Update Call*/
	public ETModel getExecuteSQL(String action, ETModel requestData);
	
	/*Batch Execute Update Call*/
	public ETModel getBatchExecuteSQL(String action, ETModel requestData);
	
	public String getPKAIColumn(String table);
	public String constructTableDescSQL(String table);
	public String  getUserLoginSQL();
	
}
