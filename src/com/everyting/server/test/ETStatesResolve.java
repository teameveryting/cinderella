package com.everyting.server.test;

import java.util.List;

import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;
import com.everyting.server.vendor.VendorManager;

public class ETStatesResolve {
	
	public static void main(String[] args) {
		String selectQuery = "";
		String dbVendor = VendorManager.getDBVendor();
		if("mysql".equalsIgnoreCase(dbVendor)){
			selectQuery = "SELECT * FROM ET_PAGES WHERE SHOW_IN_MENU = ?";
		}
		List<ETModel> queryResponse = DBExecutor.rawExecuteQuery(selectQuery,new Object[]{"Y"});
		if(queryResponse != null && queryResponse.size() > 0){
			 for(int i=0; i < queryResponse.size(); i++){
				 
			 }
		}
	}
}
