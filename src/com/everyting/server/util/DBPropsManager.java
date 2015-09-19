package com.everyting.server.util;

import java.util.Properties;

import com.everyting.server.exception.ETException;

public class DBPropsManager {
	
	private  Properties dbProperties;
	private  String vendor = null , driverClassName = null, url = "", fileStoragePath = null;
	public DBPropsManager(){
		dbProperties = new Properties();
		try{
			dbProperties.load( Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
			vendor =  dbProperties.get("vendor").toString();
			driverClassName =  dbProperties.get("driverClassName").toString();
			url =  dbProperties.get("url").toString();
			fileStoragePath = dbProperties.get("fileStoragePath").toString();
		} catch (Exception e) {
			throw new ETException("Exception", "Server is down for this moment please contact System Administrator", e.getMessage());
		}
	}
	public  Properties getDbProperties() {
		return dbProperties;
	}
	public  String getVendor() {
		return vendor;
	}
	public  String getDriverClassName() {
		return driverClassName;
	}
	public  String getUrl() {
		return url;
	}
	public  String getFileStoragePath() {
		return fileStoragePath;
	}
	public  void setDbProperties(Properties dbProperties) {
		this.dbProperties = dbProperties;
	}
	public  void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public  void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public  void setUrl(String url) {
		this.url = url;
	}
	public  void setFileStoragePath(String fileStoragePath) {
		this.fileStoragePath = fileStoragePath;
	}
}
