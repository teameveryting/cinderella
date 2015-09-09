package com.everyting.server.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;
import com.everyting.server.vendor.VendorManager;

public class BeanShell2 {
	
	
	public static void main(String[] args) {
		
		//requestData.set("skipAPI", true);
		String dbVendor = VendorManager.getDBVendor();
		String rawQueryETDS = "";
		String rawQueryETDSAttrs = "";
		if("mysql".equalsIgnoreCase(dbVendor)){
		  rawQueryETDS = "SELECT * FROM ET_DS";
		  rawQueryETDSAttrs = "SELECT * FROM ET_DS_ATTRS";
		}
		if(!(rawQueryETDS.length() > 0 && rawQueryETDSAttrs.length() > 0)) return;
		List<ETModel> etDSResponseList = DBExecutor.rawExecuteQuery(rawQueryETDS, new Object[]{});
		List<ETModel> etDSAttrsResponseList = DBExecutor.rawExecuteQuery(rawQueryETDSAttrs, new Object[]{});

		/*Response Data--- [ TABLE_NAME:{dsInfo:{}, attrsInfo:[{},{},...]}, TABLE_NAME_2:{dsInfo:{}, attrsInfo:[{},{},...]}, ....  ]*/
		if(etDSResponseList != null && etDSResponseList.size() > 0){
		  List<ETModel> dsInfoList = new ArrayList();
		  for(int i=0; i < etDSResponseList.size(); i++){
				ETModel dsModel = new ETModel();
				List<ETModel> attrsInfoList = new ArrayList();
		  		ETModel etDSResponse = etDSResponseList.get(i);
				String tableName = (String) etDSResponse.get("tableName");
				 if(etDSAttrsResponseList != null && etDSAttrsResponseList.size() > 0){
					 for(int j=0; j < etDSAttrsResponseList.size(); j++){
					 	ETModel etDSAttrsResponse = etDSAttrsResponseList.get(j);
					    String dsName = (String) etDSAttrsResponse.get("tableName");
					   if(dsName.equals(tableName)){
					   			attrsInfoList.add(etDSAttrsResponse);
						 		etDSAttrsResponseList.remove(j);
					   		}
						}
					}
				dsModel.set("dsInfo",etDSResponse );
				dsModel.set("attrsInfo", attrsInfoList);
				dsInfoList.add(dsModel);
		  }
		  for(ETModel etModel: dsInfoList){
				Iterator<String> iterator = etModel.getKeyIterator();
				while(iterator.hasNext()){
					String key = iterator.next();
					System.out.println(key + " : " + etModel.get(key));
				}
				
			}

		}	
	
	
	}

}
