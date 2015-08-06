package com.everyting.server.vendor;

import java.util.List;

import com.everyting.server.model.ETModel;

public interface SQLConstructor {

	/*Constructs typical/advanced SQL query syntax*/
	public ETModel constructQuerySQL(ETModel requestData);
	/*Construct Insert, Update, Delete SQL syntax*/
	public ETModel constructExecuteUpdateSQL(String action, ETModel requestData);
	/*Construct batch Insert, Update, Delete SQL syntax*/
	public List<ETModel> constructBatchExecuteUpdateSQL(String action, ETModel requestModel);
	public String getPKAIColumn(String table);
	public String constructTableDescSQL(String table);
	public String  getUserLoginSQL();
}
