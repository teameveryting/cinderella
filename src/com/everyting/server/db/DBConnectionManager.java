package com.everyting.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;

public class DBConnectionManager {
	
	private static Object myOwnLockMonitor = new Object();
	private static DBConnectionManager openedInstance;
	private static boolean isDBPropsFileExists = false;
	private static Properties dbProperties;
	private static BasicDataSource connectionPool;
	private static ETModel dbInfo = new ETModel();
	
	private DBConnectionManager(){
		dbProperties = new Properties();
		try{
			dbProperties.load( Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
			connectionPool = getBasicDataSource(dbProperties);
			dbInfo.set("vendor", dbProperties.get("vendor").toString());
			dbInfo.set("driverClassName", dbProperties.get("driverClassName").toString());
			dbInfo.set("url", dbProperties.get("url").toString());
		} catch (Exception e) {
			throw new ETException("ET-DB-000","db.properties file is mising inside /WEB-INF/classes " + e.getMessage(),
								  "Server is down for this moment please contact System Administrator");
		}
	}
	
	public static DBConnectionManager getInstance(){
		if(openedInstance != null){
			return openedInstance;
		}
		synchronized (myOwnLockMonitor) {
			openedInstance = new DBConnectionManager();
			return openedInstance;
		}
	}
	
	public Connection getConnection(){
		Connection connection;
		if(dbProperties != null && connectionPool != null){
			try {
				connection = connectionPool.getConnection();
			} catch (SQLException e) {
				throw new ETException("Database is down at this moment..." +e.getMessage());
			}
		}else{
			throw new ETException("Unable to load the db properties");
		}
		return connection;
	}
	
	public void closeConnection(Connection connection){
		if(connection != null){
			try {
				connection.close();
			} catch (SQLException e) {
				throw new ETException("ET-DB-001","Error in closing connection",
						  "Error in closing connection please contact System Administrator");
			}
		}
	}
	
	public static boolean isDBProsFileExists(){
		if(isDBPropsFileExists) return true;
		InputStream inputStream = null;
		Properties properties;   
		try {
			    inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
	            properties = new Properties();
	        	properties.load(inputStream);
	        	isDBPropsFileExists = true;
	        } catch (Exception e) {
	        	isDBPropsFileExists =  false;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {

					}
				}
			}
	    	return isDBPropsFileExists;
	}
	
	public static void closeDbResouces(Connection connection, Statement statement, ResultSet resultSet){
		try {
		        if (resultSet != null) {
					resultSet.close();
		        }
		        if (statement != null) {
		        	statement.close();
		        }
		        if (connection != null) {
		        	connection.close();
		        }
		} catch (SQLException e) {
			throw new ETException("ET-DB-002","Error in closing resources",
					  "Error in closing connection please contact System Administrator");
		}
	}
	
	public ETModel getDBInfo(){
		return dbInfo;
	}
	
	private BasicDataSource getBasicDataSource(Properties properties){
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(properties.get("driverClassName").toString());
		basicDataSource.setUrl(properties.get("url").toString());
		basicDataSource.setUsername(properties.get("username").toString());
		basicDataSource.setPassword(properties.get("password").toString());
		return basicDataSource;
	}

}
