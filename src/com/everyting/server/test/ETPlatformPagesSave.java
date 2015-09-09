package com.everyting.server.test;

import java.util.ArrayList;
import java.util.List;

import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;

public class ETPlatformPagesSave {
	
	public static void main(String[] args) {
		
		ETModel requestData = getRequestData();
		/*For Insertion*/
		if(requestData.get("valueMap") != null){
			ETModel valueMapDetails = (ETModel) requestData.get("valueMap");
			String html = (String) valueMapDetails.get("html");
			String js = (String) valueMapDetails.get("js");
			String insertSQL = "INSERT INTO ET_BLOBS(FILE_NAME, CONTENT_TYPE, CONTENT) VALUES(?,?,?)";
			int htmlBlobId = DBExecutor.rawExecuteUpdate(insertSQL, new Object[]{(String)valueMapDetails.get("pageName") +"_index.html", "text/html", html});
			int jsBlobId =   DBExecutor.rawExecuteUpdate(insertSQL, new Object[]{(String)valueMapDetails.get("pageName") +"_js.js", "application/javascript", js});
				if(htmlBlobId > -1 && jsBlobId > -1){
					valueMapDetails.set("htmlBlobId", htmlBlobId);
					valueMapDetails.set("jsBlobId", jsBlobId);
					//List<ETModel> insertedInfo=  DBExecutor.executeUpdate("insert", requestData);
				}
		}
		/*For Update*/
		if(requestData.get("setMap") != null){
			ETModel setMapDetails = (ETModel) requestData.get("setMap");
			String html = (String) setMapDetails.get("html");
			String js = (String) setMapDetails.get("js");
			if(html != null){
				String updateSQL = "UPDATE ET_BLOBS SET CONTENT = ?  WHERE ID = ?";
				DBExecutor.rawExecuteUpdate(updateSQL, new Object[]{html, setMapDetails.get("htmlBlobId")});
			}
			if(js != null){
				String updateSQL = "UPDATE ET_BLOBS SET CONTENT = ? WHERE ID = ?";
				DBExecutor.rawExecuteUpdate(updateSQL, new Object[]{js, setMapDetails.get("jsBlobId")});
			}
			List<ETModel> updateList  = DBExecutor.executeUpdate("update", requestData);
			System.out.println("updateList:" + updateList);
		}

	
}
	
	public static ETModel getRequestData(){
		ETModel requestData = new ETModel();
		requestData.set("dataSource", "etPages");
		requestData.set("returnRows", true);
		requestData.set("beforeAPICall", "ETPlatformPagesSave");
		requestData.set("skipAPI", true);
		ETModel valueMap = new ETModel();
		valueMap.set("pageName", "My Page66");
		valueMap.set("showInMenu", "N");
		valueMap.set("state", "app.mytest");
		valueMap.set("url", "/test");
		valueMap.set("js", "MyTestJS");
		valueMap.set("html", "MyTestHTMl");
		requestData.set("where", " ID = ?");
		List<Object> whereParams = new ArrayList<Object>();
		whereParams.add(10);
		requestData.set("whereParams", whereParams);
		requestData.set("setMap",valueMap);
		return requestData;
	}

}
