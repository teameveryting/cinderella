package com.everyting.server.vendor;

import com.everyting.server.db.DBConnectionManager;
import com.everyting.server.model.ETModel;
import com.everyting.server.vendor.mysql.MySQLConstructor;

public  class VendorManager {
	
	public static SQLConstructor getSQLConstructor() {
		ETModel dbInfo = DBConnectionManager.getInstance().getDBInfo();
		String vendor = (String) dbInfo.get("vendor");
		if("mysql".equalsIgnoreCase(vendor)){
			return new MySQLConstructor();
		}
		return null;
	}
	
	public static String getDBVendor(){
		ETModel dbInfo = DBConnectionManager.getInstance().getDBInfo();
		return (String) dbInfo.get("vendor");
	}
}
