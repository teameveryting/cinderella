package com.everyting.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import com.everyting.server.db.DBConnectionManager;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;
import com.everyting.server.vendor.SQLConstructor;
import com.everyting.server.vendor.VendorManager;

public class ServiceExecutor{
	
	public  List<ETModel> rawExecuteQuery(String rawQuery, Object[] preparedParamsList){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(rawQuery);
			preparedStatement = statementHandler(preparedStatement,preparedParamsList);
			resultSet = preparedStatement.executeQuery();
			return queryResultHandler(resultSet);
		} catch (SQLException e) {
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while executeQuery" , e.getMessage());
		} catch (JSONException e) {
			throw new ETException("JSONException", "ServiceExecutor throws JSONException while executeQuery" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
	}	
	@SuppressWarnings("unchecked")
	public  List<ETModel> executeQuery(ETModel requestData){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		ETModel querySQLModel = sqlConstructor.getQuerySQL(requestData);
		try {
			preparedStatement = connection.prepareStatement((String)querySQLModel.get("sql"));
			preparedStatement = statementHandler(preparedStatement,(List<Object>) querySQLModel.get("preparedParams"));
			resultSet = preparedStatement.executeQuery();
				return queryResultHandler(resultSet);
		} catch (SQLException e) {
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while executeQuery", e.getMessage());
		} catch (JSONException e) {
			throw new ETException("JSONException", "ServiceExecutor throws JSONException while executeQuery" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
	}
	/*Returns ETModel of insert row for action Insert, no.of affected rows for action Update/Delete*/
	public  ETModel rawExecuteUpdate(String rawSQL,String dataSource, Object[] preparedParamsList){
		ETModel responseData = new ETModel();
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		dataSource = DataHandler.toUpperSplitCase(dataSource);
		String AIPKColumn = sqlConstructor.getPKAIColumn(dataSource);
		/*Check for proper where clause, where clause params*/
		if(rawSQL.trim().toUpperCase().startsWith("UPDATE") || rawSQL.trim().toUpperCase().startsWith("DELETE")){
			Pattern pattern = Pattern.compile("\\s+WHERE\\s+", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(rawSQL);
			if(!matcher.find()){
				throw new ETException("InvalidWhereClause", "ServiceExecutor throws InvalidWhereClause Exception while rawExecuteUpdate", 
						"Update/Delete operation should have valid 'where'");
			}
			if(preparedParamsList == null){
				throw new ETException("InvalidWhereClausePamars", "ServiceExecutor throws InvalidWhereClausePamars Exception while rawExecuteUpdate", 
					"Update/Delete operation should have valid 'where', 'wherePamars'");
			}
		}
		if(AIPKColumn == null || AIPKColumn.length()  < 1){
			throw new ETException("InvalidPKException", "ServiceExecutor throws InvalidPKException while rawExecuteUpdate" , 
					dataSource + " should have one primary key column with AUTO_INCREMENT/AUTO_SEQUENCE value");
		}
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(rawSQL, Statement.RETURN_GENERATED_KEYS);
			preparedStatement = statementHandler(preparedStatement, preparedParamsList);
			int affectedRows = preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys();
			int currentId = -1;
			 if (resultSet.next()){
			     currentId = resultSet.getInt(1);
			}
			 if(currentId > -1){
				 String querySQl = "SELECT * FROM " + dataSource + " WHERE " + AIPKColumn +"=?";
				List<ETModel> queryList =  rawExecuteQuery(querySQl, new Object[]{currentId});
				if(queryList != null && queryList.size() >0){
					responseData = queryList.get(0);
				}
			 }
			 responseData.set("affectedRows", affectedRows);
		} catch (SQLException e) {
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while rawExecuteUpdate" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return responseData;
	}
	/*Returns ETModel of insert row for action Insert, no.of affected rows for action Update/Delete*/
	@SuppressWarnings("unchecked")
	public  ETModel executeUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		String dataSource = DataHandler.toUpperSplitCase((String)requestData.get("dataSource"));
		String aIPKColumn = sqlConstructor.getPKAIColumn(dataSource);
		if(aIPKColumn == null || aIPKColumn.length()  < 1){
			throw new ETException("InvalidPKException", "ServiceExecutor throws InvalidPKException while executeUpdate" , 
					dataSource + " should have one primary key column with AUTO_INCREMENT/AUTO_SEQUENCE value");
		}
		/*Check for where clause, if not there construct by PK column*/
		ETModel updateSQLModel = sqlConstructor.getExecuteSQL(action, requestData);
		try {
			preparedStatement = connection.prepareStatement((String) updateSQLModel.get("sql"), Statement.RETURN_GENERATED_KEYS);
			preparedStatement = statementHandler(preparedStatement,((List<Object>) updateSQLModel.get("preparedParams")));
			int affectedRows = preparedStatement.executeUpdate();
			 resultSet = preparedStatement.getGeneratedKeys();
			 int currentId = -1;
			 if (resultSet.next()){
			     currentId = resultSet.getInt(1);
			}
			 if(currentId > -1){
				 String querySQl = "SELECT * FROM " + dataSource + " WHERE " + aIPKColumn +"=?";
				List<ETModel> queryList =  rawExecuteQuery(querySQl, new Object[]{currentId});
				if(queryList != null && queryList.size() >0){
					responseData = queryList.get(0);
				}
			 }
			 responseData.set("affectedRows", affectedRows);
		} catch (SQLException e) {
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while executeUpdate" , e.getMessage());
		} finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return responseData;
	}
	@SuppressWarnings("unchecked")
	public  List<ETModel> batchExecuteUpdate(String action, ETModel requestData){
		List<ETModel> responseData = new ArrayList<>();
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String dataSource = DataHandler.toUpperSplitCase((String)requestData.get("dataSource"));
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		String aIPKColumn = sqlConstructor.getPKAIColumn(dataSource);
		if(aIPKColumn == null || aIPKColumn.length()  < 1){
			throw new ETException("InvalidPKException", "ServiceExecutor throws InvalidPKException while executeUpdate" , 
					dataSource + " should have one primary key column with AUTO_INCREMENT/AUTO_SEQUENCE value");
		}
	  try {
			/*Transaction starts here!*/
			connection.setAutoCommit(false);
			ETModel sqlResponseData = sqlConstructor.getBatchExecuteSQL(action, requestData);
			String sql = (String) sqlResponseData.get("sql");
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			List<List<Object>> preparedParamsList = (List<List<Object>>) sqlResponseData.get("preparedParamsList");
			if(preparedParamsList != null){	
				for(List<Object> preparedParams: preparedParamsList){
					preparedStatement = statementHandler(preparedStatement,preparedParams);
					preparedStatement.addBatch();
				}
			}
			int[] affectedRows = preparedStatement.executeBatch();
			resultSet =  preparedStatement.getGeneratedKeys();
			if(affectedRows != null){
				for(int i =0; i < affectedRows.length; i++){
					ETModel processedRow = new ETModel();
					processedRow.set("affectedRows",affectedRows[i] );	
					responseData.add(processedRow);
				}
			}
			int count = 0;
			while(resultSet.next()){
			    int currentId = resultSet.getInt(1);
			    if(responseData.size() >= 0){
			    	ETModel etModel = responseData.get(count);
			    	etModel.set(aIPKColumn, currentId);
			    	responseData.set(count, etModel);
			    }
			    count++;
			}
				connection.commit();
		} catch (SQLException e) {
			try {
				/*Transaction RollBack here!*/
				connection.rollback();
			} catch (SQLException e1) {
				throw new ETException("SQLException", "ServiceExecutor throws SQLException while batchExecuteUpdate as Rollback failed" , e1.getMessage());
			}
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while batchExecuteUpdate" , e.getMessage());
		} finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return responseData;
	}
	public void uploadFiles(List<ETModel> fileList){
		
	}
	private  PreparedStatement statementHandler(PreparedStatement preparedStatement, Object[] preparedPamas) throws SQLException{
		if(preparedPamas != null){
			for(int i=0; i < preparedPamas.length; i++){
				Object object = preparedPamas[i];
				if(object instanceof Integer){
					preparedStatement.setInt(i+1, (Integer) object);
				}else if(object instanceof Double){
					preparedStatement.setDouble(i+1, (Double) object);
				}else if(object instanceof String){
					preparedStatement.setString(i+1, (String) object);
				}else if(object instanceof Date){
					preparedStatement.setTimestamp(i+1, new Timestamp(((Date) object).getTime()));
				}else if(object instanceof Long){
					preparedStatement.setLong(i+1, (Long) object);
				}else if(object instanceof Float){
					preparedStatement.setFloat(i+1, (Float) object);
				}else{
					preparedStatement.setObject(i+1, null);
				}
			}
		}
			return preparedStatement;
	}
	private  PreparedStatement statementHandler(PreparedStatement preparedStatement, List<Object> preparedPamas) throws SQLException{
		if(preparedPamas != null){
			for(int i=0; i < preparedPamas.size(); i++){
				Object object = preparedPamas.get(i);
				if(object instanceof Integer){
					preparedStatement.setInt(i+1, (Integer) object);
				}else if(object instanceof Double){
					preparedStatement.setDouble(i+1, (Double) object);
				}else if(object instanceof String){
					preparedStatement.setString(i+1, (String) object);
				}else if(object instanceof Date){
					preparedStatement.setTimestamp(i+1, new Timestamp(((Date) object).getTime()));
				}else if(object instanceof Long){
					preparedStatement.setLong(i+1, (Long) object);
				}else if(object instanceof Float){
					preparedStatement.setFloat(i+1, (Float) object);
				}else{
					preparedStatement.setObject(i+1, null);
				}
			}
		}
			return preparedStatement;
	}
	private  List<ETModel> queryResultHandler(ResultSet resultSet) throws SQLException, JSONException{
		List<ETModel> responseList = new ArrayList<>();
	    ResultSetMetaData metadata = resultSet.getMetaData();
	    while(resultSet.next()) {
	      int numColumns = metadata.getColumnCount();
	      	ETModel dataModel = new ETModel();
	      for (int i=1; i<numColumns+1; i++) {
	    	  String column_name = metadata.getColumnName(i);
	    	  Object object = resultSet.getObject(i);
	    	  dataModel.set(DataHandler.toCamelCase(column_name), object);
	      	}
	      responseList.add(dataModel);
	    }
		return responseList;
	}
	private Connection getDbConnection(){
		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
		return dbConnectionManager.getConnection();
	}
	public static void main(String[] args) {
		String  given = "admin@123";
		String dbPassword = DataHandler.hashPassword(given);
        System.out.println(dbPassword);
        boolean matched = DataHandler.isPasswordMatches("$2a$12$jrSOaTOLbBCTckY4nMQhLudPr9QELj7/752xsqL8IIpv5fe5DquXe", given);
        System.out.println(matched);
	}
}

