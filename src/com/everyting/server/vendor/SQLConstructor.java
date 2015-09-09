package com.everyting.server.vendor;

import java.util.List;

import com.everyting.server.model.ETModel;

public interface SQLConstructor {
	//For request, response data formats please refer EtingDataFormats.txt
	//Intended for Interpreter.java only, developer has to ensure, 
	//code(SQL syntax) will work for all types DBs
    /*Query Call*/
	public  ETModel getQuerySQL(ETModel requestData);
	/*Execute Update Call for Insert, Update and Delete*/
	public ETModel getExecuteSQL(String action, ETModel requestData);
	/*Batch Execute Update Call for Insert, Update and Delete*/
	public ETModel getBatchExecuteSQL(String action, ETModel requestData);
	/*Verify DataSource Details*/
	public ETModel getDSDetails(String dataSource);
	
	public  ETModel getWhereClause(String whereClause, List<Object> whereClauseParams);
	
	/*Download ETFile*/
	public ETModel getETFileInfo(int fileId);
	
	public String getPKAIColumn(String table);
	
	public String constructTableDescSQL(String table);
	
	public String  getUserLoginSQL();
	
}
