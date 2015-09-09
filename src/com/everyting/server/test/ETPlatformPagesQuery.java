package com.everyting.server.test;

import java.util.ArrayList;
import java.util.List;
import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;

public class ETPlatformPagesQuery {
	
	public static void main(String[] args) {
		
		ETModel requestData =  getRequestData();
		ETModel responseData = new ETModel();
		List<ETModel> queryResultList = DBExecutor.executeQuery(requestData);
		ETModel queryResult = queryResultList.get(0);
		
		int htmlBlobId = (int)queryResult.get("htmlBlobId");
		int jsBlobId = (int) queryResult.get("jsBlobId");
		if(htmlBlobId > -1){
			List<ETModel> htmlList = DBExecutor.rawExecuteQuery("SELECT CONTENT FROM ET_BLOBS WHERE ID = ?", new Object[]{htmlBlobId});
			String html = (String) htmlList.get(0).get("content");
			queryResult.set("html", html);
		}
		if(jsBlobId > -1){
			List<ETModel> jsList =  DBExecutor.rawExecuteQuery("SELECT CONTENT FROM ET_BLOBS WHERE ID = ?", new Object[]{jsBlobId});
			String js = (String) jsList.get(0).get("content");
			queryResult.set("js", js);
		}
		responseData.set("data", queryResult);
		System.out.println(responseData);
	}
	public static ETModel getRequestData(){
		ETModel requestData = new ETModel();
		requestData.set("dataSource", "etPages");
		requestData.set("where", "PAGE_NAME = ?");
		List<Object> whereParams = new ArrayList<>();
		whereParams.add("My Page66");
		requestData.set("whereParams", whereParams);
		return requestData;
	}
}
