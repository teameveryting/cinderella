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

public class DBExecutor{
	
	public static List<ETModel> rawExecuteQuery(String rawQuery, Object[] preparedParams){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(rawQuery);
			preparedStatement = statementHandler(preparedStatement,preparedParams);
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
	public static List<ETModel> executeQuery(ETModel requestData){
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
	/*Returns Auto generated Id only for Insert*/
	public static int rawExecuteUpdate(String rawSQL, Object[] preparedParams){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		/*Check for proper where clause, where clause Params*/
		if(rawSQL.trim().toUpperCase().startsWith("UPDATE") || rawSQL.trim().toUpperCase().startsWith("DELETE")){
			Pattern pattern = Pattern.compile("\\s+WHERE\\s+", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(rawSQL);
			if(!matcher.find()){
				throw new ETException("InvalidWhereClause", "ServiceExecutor throws InvalidWhereClause Exception while rawExecuteUpdate", 
						"Update/Delete operation should have valid 'where'");
			}
			if(preparedParams == null){
				throw new ETException("InvalidWhereClausePamars", "ServiceExecutor throws InvalidWhereClausePamars Exception while rawExecuteUpdate", 
					"Update/Delete operation should have valid 'where', 'wherePamars'");
			}
		}
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(rawSQL, Statement.RETURN_GENERATED_KEYS);
			preparedStatement = statementHandler(preparedStatement, preparedParams);
			preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys();
			int currentId = -1;
			 if (resultSet.next()){
			     currentId = resultSet.getInt(1);
			}
			 return currentId;
		} catch (SQLException e) {
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while rawExecuteUpdate" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
	}
	/*Returns ETModel of row, no.of affected rows for action Update/Delete*/
	@SuppressWarnings("unchecked")
	public static List<ETModel> executeUpdate(String action, ETModel requestData){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		String dataSource = DataHandler.toUpperSplitCase((String)requestData.get("dataSource"));
		String pkColumn = sqlConstructor.getPKAIColumn(dataSource);
		if(pkColumn == null || pkColumn.length()  < 1){
			throw new ETException("InvalidPKException", "ServiceExecutor throws InvalidPKException while executeUpdate" , 
					dataSource + " should have one primary key column with AUTO_INCREMENT/AUTO_SEQUENCE value");
		}
		/*Check for where clause, if not there construct by PK column*/
		ETModel updateSQLModel = sqlConstructor.getExecuteSQL(action, requestData);
		try {
			 preparedStatement = connection.prepareStatement((String) updateSQLModel.get("sql"), Statement.RETURN_GENERATED_KEYS);
			 preparedStatement = statementHandler(preparedStatement,((List<Object>) updateSQLModel.get("preparedParams")));
			 preparedStatement.executeUpdate();
			 resultSet = preparedStatement.getGeneratedKeys();
			 int currentId = -1;
			 if (resultSet.next()){
			     currentId = resultSet.getInt(1);
			}
			 boolean returnRows = ((requestData.get("returnRows") == null) ? false :  true);
			 /*For Action Insert*/
			 if(currentId > -1 && returnRows ){
				List<ETModel> queryList =  new ArrayList<>();
				ETModel valueMap = (ETModel) requestData.get("valueMap");
				valueMap.set(pkColumn, currentId);
				queryList.add(valueMap);
				return queryList;
			 }
			 /*For Action Update*/
			else if( returnRows && requestData.get("where") != null && "update".equalsIgnoreCase(action)){
				ETModel whereModel =  sqlConstructor.getWhereClause((String)requestData.get("where"), (List<Object>)requestData.get("whereParams"));
				 String whereClause = (String) whereModel.get("whereClause");
				 List<Object> whereClauseParams = (List<Object>) whereModel.get("whereClauseParams");
				 Object[] whereParamsArray = new Object[whereClauseParams.size()];
				 for(int i=0; i < whereClauseParams.size(); i++){
					 whereParamsArray[i] = whereClauseParams.get(i);
				 }
				 String querySQL = "SELECT * FROM " +dataSource+" " +whereClause;
				 List<ETModel> queryResult = rawExecuteQuery(querySQL, whereParamsArray);
				 return queryResult;
			 }
			
		} catch (SQLException e) {
			throw new ETException("SQLException", "ServiceExecutor throws SQLException while executeUpdate" , e.getMessage());
		} finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return new ArrayList<>();
	}
	
	public static int[] rawBatchExecuteUpdate(String rawSQL, List<Object[]> preparedParamsList){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		if(rawSQL.trim().toUpperCase().startsWith("UPDATE") || rawSQL.trim().toUpperCase().startsWith("DELETE")){
			Pattern pattern = Pattern.compile("\\s+WHERE\\s+", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(rawSQL);
			if(!matcher.find()){
				throw new ETException("InvalidWhereClause", "ServiceExecutor throws InvalidWhereClause Exception while rawExecuteUpdate", 
						"Update/Delete operation should have valid 'where'");
			}
		}
		try{
			/*Transaction starts here!*/
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(rawSQL, Statement.RETURN_GENERATED_KEYS);
			if(preparedParamsList != null){
				for(Object[] preparedParams: preparedParamsList){
					preparedStatement = statementHandler(preparedStatement,preparedParams);
					preparedStatement.addBatch();
				}
			}
			int[] affectedRows = preparedStatement.executeBatch();
			resultSet =  preparedStatement.getGeneratedKeys();
			return affectedRows;
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
		
	}
	@SuppressWarnings("unchecked")
	public static List<ETModel> batchExecuteUpdate(String action, ETModel requestData){
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
					processedRow.set("affectedRowsCount",affectedRows[i] );	
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
	
	

	private static PreparedStatement statementHandler(PreparedStatement preparedStatement, Object[] preparedPamas) throws SQLException{
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
	private static PreparedStatement statementHandler(PreparedStatement preparedStatement, List<Object> preparedPamas) throws SQLException{
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
	private static List<ETModel> queryResultHandler(ResultSet resultSet) throws SQLException, JSONException{
		List<ETModel> responseList = new ArrayList<>();
	    ResultSetMetaData metadata = resultSet.getMetaData();
	    while(resultSet.next()) {
	      int numColumns = metadata.getColumnCount();
	      	ETModel dataModel = new ETModel();
	      for (int i=1; i<numColumns+1; i++) {
	    	  String column_name = metadata.getColumnName(i);
	    	  Object object = resultSet.getObject(i);
	    	  if(object instanceof byte[]){
	    		  object = new String((byte[]) object);
	    	  }
	    	  dataModel.set(DataHandler.toCamelCase(column_name), object);
	      	}
	      responseList.add(dataModel);
	    }
		return responseList;
	}
	
	private static Connection getDbConnection(){
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