package com.everyting.server.test;

import java.util.ArrayList;
import java.util.List;

import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;

public class ETAppStructureSave {
	
	public static void main(String[] args) {
		
		ETModel requestData = new ETModel();
		List<ETModel> insertedETAppsList = new ArrayList<>();
		ETModel insertedETAppsData = insertedETAppsList.get(0);
		
		ETModel valueMapDetails = (ETModel) requestData.get("valueMap");
		int appId = (int) valueMapDetails.get("id");
		int templateId = (int) insertedETAppsData.get("templateId");
		List<ETModel> templateStructureList = DBExecutor.rawExecuteQuery("SELECT * FROM ET_TEMPLATE_STRUCTURE WHERE TEMPLATE_ID = ?", 
				new Object[]{templateId});
		List<ETModel> appStructureList = new ArrayList<>();
		for(int i=0; i < templateStructureList.size(); i++ ){
			ETModel templateStructure = templateStructureList.get(i);
			ETModel appStructure = new ETModel();
			appStructure.set("uid", templateStructure.get("uid"));
			appStructure.set("name", templateStructure.get("name"));
			appStructure.set("isFolder", templateStructure.get("isFolder"));
			appStructure.set("parentUid", templateStructure.get("parentUid"));
			appStructure.set("appId", appId);
			appStructure.set("fileContent", templateStructure.get("fileContent"));
			appStructureList.add(appStructure);
		}
		ETModel appStructureBatch = new ETModel();
		appStructureBatch.set("dataSource", "etAppStructure");
		appStructureBatch.set("valueMapList", appStructureList);
		int[] list = DBExecutor.batchExecuteUpdate("insert", appStructureBatch);
		for(int i : list){
			System.out.println(i);
		}
		
	}
}
