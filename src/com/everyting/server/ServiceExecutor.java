package com.everyting.server;

import java.io.InputStream;
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

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;

import com.everyting.server.db.DBConnectionManager;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;
import com.everyting.server.vendor.SQLConstructor;
import com.everyting.server.vendor.VendorManager;

public class ServiceExecutor{
	@Deprecated
	/*Usually RAW SQL statements are Deprecated since the vendor specifications*/
	public  List<ETModel> executeQuery(String rawQuery, Object[] preparedParamsList){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(rawQuery);
			preparedStatement = statementHandler(preparedStatement,preparedParamsList);
			resultSet = preparedStatement.executeQuery();
			return queryResultHandler(resultSet);
		} catch (SQLException e) {
			throw new ETException("ET-SQL-000", "ServiceExecutor throws SQLException while executeQuery" , e.getMessage());
		} catch (JSONException e) {
			throw new ETException("ET-JSON-001", "ServiceExecutor throws JSONException while executeQuery" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
	}	
	public  List<ETModel> executeQuery(ETModel requestData){
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		ETModel querySQLModel = sqlConstructor.constructQuerySQL(requestData);
		try {
			preparedStatement = connection.prepareStatement((String)querySQLModel.get("sql"));
			preparedStatement = statementHandler(preparedStatement,(Object[]) querySQLModel.get("preparedParams"));
			resultSet = preparedStatement.executeQuery();
				return queryResultHandler(resultSet);
		} catch (SQLException e) {
			throw new ETException("ET-SQL-001", "ServiceExecutor throws SQLException while executeQuery", e.getMessage());
		} catch (JSONException e) {
			throw new ETException("ET-JSON-002", "ServiceExecutor throws JSONException while executeQuery" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
	}
	@Deprecated
	/*Usually RAW SQL statements are Deprecated since the vendor specifications*/
	/*Returns ETModel of insert row for action Insert, no.of affected rows for action Update/Delete*/
	public  ETModel executeUpdate(String rawSQL,String table, Object[] preparedParamsList){
		ETModel responseData = new ETModel();
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		table = DataHandler.toUpperSplitCase(table);
		String AIPKColumn = sqlConstructor.getPKAIColumn(table);
		if(rawSQL.trim().toUpperCase().startsWith("UPDATE") || rawSQL.trim().toUpperCase().startsWith("DELETE")){
			if(preparedParamsList == null || ArrayUtils.isEmpty(preparedParamsList)){
			throw new ETException("ET-NoWhereClause-002", "ServiceExecutor throws NoWhereClause Exception while getWhereClause", 
					"Update/Delete operation should have valid whereClause");
			}
		}
		if(AIPKColumn == null || AIPKColumn.length()  < 1){
			throw new ETException("ET-ET-000", "ServiceExecutor throws SQLException while executeUpdate" , 
					table + " should have one primary key column with AUTO_INCREMENT/AUTO_SEQUENCE value");
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
				 String querySQl = "SELECT * FROM " + table + " WHERE " + AIPKColumn +"=?";
				List<ETModel> queryList =  executeQuery(querySQl, new Object[]{currentId});
				if(queryList != null && queryList.size() >0){
					responseData = queryList.get(0);
				}
			 }
			 responseData.set("affectedRows", affectedRows);
		} catch (SQLException e) {
			throw new ETException("ET-SQL-003", "ServiceExecutor throws SQLException while executeUpdate" , e.getMessage());
		}finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return responseData;
	}
	/*Returns ETModel of insert row for action Insert, no.of affected rows for action Update/Delete*/
	public  ETModel executeUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		ETModel updateSQLModel = sqlConstructor.constructExecuteUpdateSQL(action, requestData);
		String dataSource = (String)requestData.get("dataSource");
		String table = DataHandler.toUpperSplitCase(dataSource);
		String AIPKColumn = sqlConstructor.getPKAIColumn(table);
		if(AIPKColumn == null || AIPKColumn.length()  < 1){
			throw new ETException("ET-ET-000", "ServiceExecutor throws SQLException while executeUpdate" , 
					table + " should have one primary key column with AUTO_INCREMENT/AUTO_SEQUENCE value");
		}
		try {
			preparedStatement = connection.prepareStatement((String) updateSQLModel.get("sql"), Statement.RETURN_GENERATED_KEYS);
			preparedStatement = statementHandler(preparedStatement,((Object[]) updateSQLModel.get("preparedParams")));
			int affectedRows = preparedStatement.executeUpdate();
			 resultSet = preparedStatement.getGeneratedKeys();
			 int currentId = -1;
			 if (resultSet.next()){
			     currentId = resultSet.getInt(1);
			}
			 if(currentId > -1){
				 String querySQl = "SELECT * FROM " + table + " WHERE " + AIPKColumn +"=?";
				List<ETModel> queryList =  executeQuery(querySQl, new Object[]{currentId});
				if(queryList != null && queryList.size() >0){
					responseData = queryList.get(0);
				}
			 }
			 responseData.set("affectedRows", affectedRows);
		} catch (SQLException e) {
			throw new ETException("ET-SQL-004", "ServiceExecutor throws SQLException while executeUpdate" , e.getMessage());
		} finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return responseData;
	}
	public  List<ETModel> batchExecuteUpdate(String action, ETModel requestData){
	
		List<ETModel> responseData = new ArrayList<>();
		Connection connection = getDbConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
	  try {
		/*Transaction starts here!*/
		connection.setAutoCommit(false);
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
			List<ETModel> sqlResponseDataList = sqlConstructor.constructBatchExecuteUpdateSQL(action, requestData);
			if(sqlResponseDataList != null){
				
				ETModel sqlResponseModel = sqlResponseDataList.get(0);
				String sql = (String) sqlResponseModel.get("sql");
				preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				for(int i=0; i< sqlResponseDataList.size(); i++){
					ETModel sqlResponseData = sqlResponseDataList.get(i);
					Object[] preparedPamas = (Object[]) sqlResponseData.get("preparedParams");
					preparedStatement = statementHandler(preparedStatement,preparedPamas);
					preparedStatement.addBatch();
				}
				 preparedStatement.executeBatch();
				 resultSet =  preparedStatement.getGeneratedKeys();
				if (resultSet.next()){
				    int currentId = resultSet.getInt(1);
				    String column = resultSet.getMetaData().getColumnName(1);
				    ETModel etModel = new ETModel();
				    etModel.set(column, currentId);
				    responseData.add(etModel);
				}
			}
				/*Transaction Ends here!*/
				connection.commit();
		} catch (SQLException e) {
			try {
				/*Transaction RollBack here!*/
				connection.rollback();
			} catch (SQLException e1) {
				throw new ETException("ET-SQL-005", "ServiceExecutor throws SQLException while batchExecuteUpdate as Rollback failed" , e1.getMessage());
			}
			throw new ETException("ET-SQL-006", "ServiceExecutor throws SQLException while batchExecuteUpdate" , e.getMessage());
		} finally{
			DBConnectionManager.closeDbResouces(connection, preparedStatement, resultSet);
		}
		return responseData;
	}
	public List<ETModel> descDataSource(String dataSource){
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		String sql = sqlConstructor.constructTableDescSQL(dataSource);
		return executeQuery(sql, null);
	}
	public void uploadFiles(List<ETModel> fileList){
		if(fileList != null && fileList.size() >0){
			for(int i=0; i< fileList.size(); i++){
				ETModel fileData = fileList.get(i);
				String fileName = (String) fileData.get("fileName");
				InputStream fileStream = (InputStream) fileData.get("fileStream");
				/*batch insert into ET_FILES*/
				ETModel insertModel = new ETModel();
				insertModel.set("dataSource", "ET_FILES");
				
			}
		}
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

