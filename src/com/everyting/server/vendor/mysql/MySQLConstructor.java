package com.everyting.server.vendor.mysql;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.everyting.server.ServiceExecutor;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;
import com.everyting.server.vendor.SQLConstructor;

public class MySQLConstructor implements SQLConstructor{  
	//For request, response data formats please refer EtingDataFormats.txt
	//Intended for Interpreter.java only, developer has to ensure, 
	//code(SQL syntax) will work for all types DBs
	
	public  final String ET_USERS_QUERY = "SELECT USER_ID,USER_NAME,DISPLAY_NAME,EMAIL_ADDRESS,ENCRYPT_PASSWORD,"
			+ "AVATAR_URL,LAST_LOGIN_DATE,LAST_LOGIN_IP"
			+ " FROM ET_USERS WHERE EMAIL_ADDRESS= ?";
		
	/*Returns ETModel contains sql(String), preparedParams(List<Object>)*/
	@SuppressWarnings("unchecked")
	@Override
	public  ETModel getQuerySQL(ETModel requestData){
		checkForQuerySQLEssentails(requestData);
		ETModel responseData = new ETModel();
		StringBuilder sqlBuilder = new StringBuilder();
		String selectClause = "SELECT * ";
		String fromClause = "FROM ";
		String whereClause = "";
		String orderByClause = "";
		String groupByClause = "";
		String havingClause = "";
		String limitClause = " LIMIT 20 ";
		String offsetClause = "OFFSET 0";
		List<Object> preparedParamsList = new ArrayList<>();
		Iterator<String> modelIterator = requestData.getKeyIterator();
		while(modelIterator.hasNext()){
			String key = modelIterator.next();
			if("select".equalsIgnoreCase(key)){
				selectClause = getSelectClause((String)requestData.get("select"));
			}
			else if("dataSource".equalsIgnoreCase(key)){
				String table = getTableName((String) requestData.get("dataSource"));
				fromClause += table;
			}
			else if("from".equalsIgnoreCase(key)){
				fromClause = getFromClause((String)requestData.get("from"));
			}
			else if("where".equalsIgnoreCase(key)){
				ETModel whereModel = getWhereClause((String)requestData.get("where"),(List<Object>)requestData.get("whereParams"));
				whereClause = (String)whereModel.get("whereClause");
				preparedParamsList = (List<Object>)whereModel.get("whereClauseParams");
			}
			else if("orderBy".equalsIgnoreCase(key)){
				orderByClause = getOrderByCluase((String)requestData.get("orderBy"));
			}
			else if("groupBy".equalsIgnoreCase(key)){
				groupByClause = getGroupByCluase((String)requestData.get("groupBy"));
			}
			else if("having".equalsIgnoreCase(key)){
				havingClause = getHavingCluase((String)requestData.get("having"));
			}
			else if("limit".equalsIgnoreCase(key)){
				limitClause = getLimitClause(String.valueOf(requestData.get("limit")));
			}
			else if("offset".equalsIgnoreCase(key)){
				offsetClause  = getOffsetClause(String.valueOf(requestData.get("offset")));
			}
		}
			sqlBuilder.append(selectClause);
			sqlBuilder.append(fromClause);
			sqlBuilder.append(whereClause);
			sqlBuilder.append(groupByClause);
			sqlBuilder.append(havingClause);
			sqlBuilder.append(orderByClause);
			sqlBuilder.append(limitClause);
			sqlBuilder.append(offsetClause);
			responseData.set("sql", sqlBuilder.toString());
			responseData.set("preparedParams", preparedParamsList);
		return responseData;
	}
	@SuppressWarnings("unchecked")
	@Override
	public ETModel getExecuteSQL(String action, ETModel requestData){
		checkForExecuteSQLEssentials(action, requestData);
		ETModel etModel = new ETModel();
		StringBuilder sqlBuilder = new StringBuilder();
		List<Object> preparedParamsList = new ArrayList<>();
		List<Object> valueParamsList = new ArrayList<>();
		List<Object> setParamsList = new ArrayList<>();
		List<Object> whereParamsList = new ArrayList<>();
			String actionStatement = "";
			String table ="";
			String valueClause ="";
			String setClause = "";
			String whereClause = "";
			if("insert".equalsIgnoreCase(action)){
				actionStatement = "INSERT INTO ";
			}
			else if("update".equalsIgnoreCase(action)){
				actionStatement = "UPDATE ";
			}
			else if("delete".equalsIgnoreCase(action)){
				actionStatement = "DELETE FROM ";
			}
			Iterator<String> modelIterator = requestData.getKeyIterator();
			while(modelIterator.hasNext()){
				String key = modelIterator.next();
				if("dataSource".equalsIgnoreCase(key)){
					 table = getTableName((String) requestData.get("dataSource"));
				}
				else if("valueMap".equalsIgnoreCase(key)){
					ETModel valueModel = getValueCluase((ETModel)requestData.get("valueMap"));
					valueClause = (String) valueModel.get("valueClause");
					valueParamsList = (List<Object>)valueModel.get("valueParams");
				}
				else if("setMap".equalsIgnoreCase(key)){
					ETModel setModel = getSetCluase((ETModel)requestData.get("setMap"));
					setClause = (String) setModel.get("setClause");		
					setParamsList = (List<Object>)setModel.get("setParams");
				}
				else if( "where".equalsIgnoreCase(key) || "whereParams".equalsIgnoreCase(key)){
					ETModel whereModel = getWhereClause((String)requestData.get("where"),
														(List<Object>)requestData.get("whereParams"));
					whereClause = (String)whereModel.get("whereClause");
					whereParamsList = (List<Object>)whereModel.get("whereClauseParams");
				}	
			}
			sqlBuilder.append(actionStatement);
			sqlBuilder.append(table);
			sqlBuilder.append(valueClause);
			sqlBuilder.append(setClause);
			sqlBuilder.append(whereClause);
			preparedParamsList.addAll(valueParamsList);
			preparedParamsList.addAll(setParamsList);
			preparedParamsList.addAll(whereParamsList);
		etModel.set("sql", sqlBuilder.toString());
		etModel.set("preparedParams", preparedParamsList);
		return etModel;
	}
	@SuppressWarnings("unchecked")
	@Override
	public ETModel getBatchExecuteSQL(String action, ETModel requestData) {
		checkForBatchExecuteSQLEssentails(action, requestData);
		ETModel responseData = new ETModel();
		String sql = "";
		List<List<Object>> preparedParamsList = new ArrayList<>();
		if("batchInsert".equalsIgnoreCase(action)){
			ETModel insertModel = new ETModel();
			String dataSource = (String) requestData.get("dataSource");
			insertModel.set("dataSource", dataSource);
			List<ETModel> valueMapList = (List<ETModel>) requestData.get("valueMapList");
			for(ETModel valueMap: valueMapList){
				insertModel.set("valueMap", valueMap);
				ETModel eachInsertResponse = getExecuteSQL("insert", insertModel);
				sql = (String) eachInsertResponse.get("sql");
				preparedParamsList.add((List<Object>) eachInsertResponse.get("preparedParams"));
			}
		}
		if("batchUpdate".equalsIgnoreCase(action)){
			ETModel updateModel = new ETModel();
			String dataSource = (String) requestData.get("dataSource");
			updateModel.set("dataSource", dataSource);
			List<ETModel> setMapList = (List<ETModel>) requestData.get("setMapList");
			String where = (String) requestData.get("where");
			List<List<Object>> whereParamsList = (List<List<Object>>) requestData.get("whereParamsList");
			for(int i=0; i< setMapList.size(); i++){
				ETModel setMap = setMapList.get(i);
				updateModel.set("setMap", setMap);
				updateModel.set("where", where);
				/*Since the setMapList size and whereParamsList size should equal always*/
				List<Object> whereParams = whereParamsList.get(i);
				updateModel.set("whereParams", whereParams);
				ETModel eachUpdateResponse = getExecuteSQL("update", updateModel);
				sql = (String) eachUpdateResponse.get("sql");
				preparedParamsList.add((List<Object>) eachUpdateResponse.get("preparedParams"));
			}
		}
		if("batchDelete".equalsIgnoreCase(action)){
			ETModel deleteModel = new ETModel();
			String dataSource = (String) requestData.get("dataSource");
			deleteModel.set("dataSource", dataSource);
			String where = (String) requestData.get("where");
			List<List<Object>> whereParamsList = (List<List<Object>>) requestData.get("whereParamsList");
			for(int i=0; i< whereParamsList.size(); i++){
				deleteModel.set("where", where);
				List<Object> whereParams = whereParamsList.get(i);
				deleteModel.set("whereParams", whereParams);
				ETModel eachDeleteResponse = getExecuteSQL("delete", deleteModel);
				sql = (String) eachDeleteResponse.get("sql");
				preparedParamsList.add((List<Object>) eachDeleteResponse.get("preparedParams"));
			}
		}
		responseData.set("sql", sql);
		responseData.set("preparedParamsList", preparedParamsList);
		return responseData;
	}
	@Override
	public String constructTableDescSQL(String table){
		StringBuilder sqlBuilder = new StringBuilder("DESC ");
		sqlBuilder.append(DataHandler.toUpperSplitCase(table));
		return sqlBuilder.toString();
	}
	@Override
	public String getPKAIColumn(String table){
		String tableDescSql = constructTableDescSQL(table);
		ServiceExecutor serviceExecutor = new ServiceExecutor();
		List<ETModel> dsAttrList = serviceExecutor.rawExecuteQuery(tableDescSql, null);
		for(int i=0; i< dsAttrList.size(); i++){
			ETModel columnInfoModel = dsAttrList.get(i);
			String extra =  (String)columnInfoModel.get("extra");
			if(extra != null && "auto_increment".equalsIgnoreCase(extra)){
				String columnKey = (String)columnInfoModel.get("columnKey");
				if(columnKey != null && "PRI".equalsIgnoreCase(columnKey)){
					return (String) columnInfoModel.get("columnName");
				}
			}
		}
		return null;
	}
	@Override
	public String  getUserLoginSQL(){
		return this.ET_USERS_QUERY;
	}
	@SuppressWarnings("unchecked")
	private void checkForBatchExecuteSQLEssentails(String action, ETModel requestData){
		if(action == null || !(("batchInsert".equalsIgnoreCase(action)) || 
				 			   ("batchUpdate".equalsIgnoreCase(action)) ||
				               ("batchDelete".equalsIgnoreCase(action))) ){
			throw new ETException("InvalidAction", 
					  "MySQLConstructor throws InvalidAction Exception while checkForBatchExecuteSQLEssentails",
					  "Invalid/No action is specified, only batch Insert/update/delete are allowed");
			}
		if(requestData.get("dataSource") == null){
			throw new ETException("InvalidDataSource", 
					  "MySQLConstructor throws InvalidDataSource Exception while checkForBatchExecuteSQLEssentails",
					  "Invalid/No DataSource is specified");
		}
		if("batchInsert".equalsIgnoreCase(action)){
			List<Object> valueMapList = (List<Object>) requestData.get("valueMapList");
			if(valueMapList == null || !(valueMapList.size() > 0)){
				throw new ETException("InvalidValueMapList", 
						  "MySQLConstructor throws InvalidValueMapList Exception while checkForBatchExecuteSQLEssentails",
						  "Invalid/No 'valueMapList' is specified for batchInsert action");
			}
		}
		if("batchUpdate".equalsIgnoreCase(action)){
			List<Object> setMapList = (List<Object>) requestData.get("setMapList");
			if(setMapList == null || !(setMapList.size() > 0)){
				throw new ETException("InvalidSetMapList", 
						  "MySQLConstructor throws InvalidSetMapList Exception while checkForBatchExecuteSQLEssentails",
						  "Invalid/No 'setMapList' is specified for batchUpdate action");
			}
		}
		if("batchUpdate".equalsIgnoreCase(action) || "batchDelete".equalsIgnoreCase(action)){
			if(requestData.get("where") == null){
				throw new ETException("InvalidWhereClause", 
						  "MySQLConstructor throws InvalidWhereClause Exception while checkForBatchExecuteSQLEssentails",
						  "batchUpdate/batchDelete actions should require a valid whereClause");
			}
			if(requestData.get("whereParamsList") == null){
				throw new ETException("InvalidWhereParamsList", 
						  "MySQLConstructor throws InvalidWhereParamsList Exception while checkForBatchExecuteSQLEssentails",
						  "batchUpdate/batchDelete actions should require a valid whereParamsList");
			}
			if("batchUpdate".equalsIgnoreCase(action)) {
				List<Object> setMapList = (List<Object>) requestData.get("setMapList");
				List<Object> whereParamsList = (List<Object>) requestData.get("whereParamsList");
				if(!(whereParamsList.size() == setMapList.size())){
					throw new ETException("InvalidWhereParamsList", 
							  "MySQLConstructor throws InvalidWhereParamsList Exception while checkForBatchExecuteSQLEssentails",
							  "'whereParamsList' size is not matching with 'setMapList'");
				}
			}
		}
	}
	private void checkForQuerySQLEssentails(ETModel requestData){
		if(requestData == null){
			throw new ETException("InvalidData", 
					  "MySQLConstructor throws InvalidData Exception while checkForQuerySQLEssentails",
					  "Invalid/No Data is specified for Query operation");
		}
		if(requestData.get("dataSource") == null){
			throw new ETException("InvalidDataSource", 
					  "MySQLConstructor throws InvalidDataSource Exception while checkForQuerySQLEssentails",
					  "Invalid/No DataSource is specified");
		}
	}
	private void checkForExecuteSQLEssentials(String action, ETModel requestData){
		if(action == null || !(("insert".equalsIgnoreCase(action)) || 
				 			  ("update".equalsIgnoreCase(action)) ||
				              ("delete".equalsIgnoreCase(action)))){
			throw new ETException("InvalidAction", 
					  "MySQLConstructor throws InvalidAction Exception while checkForExecuteSQLEssentials",
					  "Invalid/No action is specified, only insert/update/delete are allowed");
			}
		if(requestData.get("dataSource") == null){
			throw new ETException("InvalidDataSource", 
					  "MySQLConstructor throws InvalidDataSource Exception while checkForExecuteSQLEssentials",
					  "Invalid/No DataSource is specified");
		}
		if("insert".equalsIgnoreCase(action)){
			if(requestData.get("valueMap") == null){
				throw new ETException("InvalidValueMap", 
						  "MySQLConstructor throws InvalidValueMap Exception while checkForExecuteSQLEssentials",
						  "Invalid/No 'valueMap' is specified for insert action");
			}
		}
		if("update".equalsIgnoreCase(action)){
			if(requestData.get("setMap") == null){
				throw new ETException("InvalidSetMap", 
						  "MySQLConstructor throws InvalidSetMap Exception while checkForExecuteSQLEssentials",
						  "Invalid/No 'setMap' is specified for update action");
			}
		}
		if("update".equalsIgnoreCase(action) || "delete".equalsIgnoreCase(action)){
			if(requestData.get("where") == null){
				throw new ETException("InvalidWhereClause", 
						  "MySQLConstructor throws InvalidWhereClause Exception while checkForExecuteSQLEssentials",
						  "Update/Delete actions should require a valid whereClause");
			}
		}
	}
	
	private  String getTableName(String dataSource){
		StringBuilder tableBuilder = new StringBuilder();
		tableBuilder.append(DataHandler.toUpperSplitCase(dataSource));
		tableBuilder.append(" ");
		return  tableBuilder.toString();
	}
	private String getSelectClause(String selectClause){
		if(selectClause == null) return "SELECT * ";
		StringBuilder selectBuilder = new StringBuilder("SELECT ");
		/*d1.#column1# AS myColumn1, (SUM(d1.#column2#) )+( SUM(d2.#column2#) ) AS complexColumn... advanced/usage*/
		selectClause = DataHandler.replaceAllByProperNames(selectClause);
		selectBuilder.append(selectClause);
		selectBuilder.append(" ");
		return selectBuilder.toString();
	}
	private String getFromClause(String fromCluase){
		if(fromCluase == null) return "FROM ";
		StringBuilder fromBuilder = new StringBuilder("FROM ");
		/*#dataSource1# d1 JOIN d2.#dataSource2#*/
		fromCluase = DataHandler.replaceAllByProperNames(fromCluase);
		fromBuilder.append(fromCluase);
		fromBuilder.append(" ");
		return fromBuilder.toString();
	}
	private  ETModel getWhereClause(String whereClause, List<Object> whereClauseParams){
		/*Ex: #column1# <= ? #column2# != ? AND #column3# in (?,?,?)*/
		ETModel etModel = new ETModel();
		StringBuilder whereClauseBuilder = new StringBuilder("WHERE ");
		int valueCount = -1;
		whereClause = DataHandler.replaceAllByProperNames(whereClause);
		valueCount = StringUtils.countMatches(whereClause, "?");
		if(whereClauseParams != null){
			if(valueCount != whereClauseParams.size()){
				throw new ETException("InvalidWhereClauseParams", 
									  "MySQLConstructor throws InvalidWhereClauseParams Exception while getWhereClause", 
										"WhereClauseParams are not matching with given whereClause");
			}
		}
		whereClauseBuilder.append(whereClause);
		whereClauseBuilder.append(" ");
		etModel.set("whereClause", whereClauseBuilder.toString());
		/*Prevents NullPointerException at Runtime*/
		if(whereClauseParams == null){
			whereClauseParams = new ArrayList<>();
		}
		etModel.set("whereClauseParams", whereClauseParams);
		return etModel;
	}
	private  ETModel getValueCluase(ETModel valueMap){
		ETModel etModel = new ETModel();
		List<Object> preparedParamsList = new ArrayList<>();
		StringBuilder valueMapBuilder = new StringBuilder();
		StringBuilder columnBuilder = new StringBuilder("(");
		StringBuilder valueBuilder = new StringBuilder("VALUES(");
		Iterator<String> iterator = valueMap.getKeyIterator();
		int count = 0;
		while(iterator.hasNext()){
			String key = iterator.next();
			String columnName = DataHandler.toUpperSplitCase(key);
			if(count > 0){
				columnBuilder.append(",");
				columnBuilder.append(columnName);
				valueBuilder.append(",");
				valueBuilder.append("?");
			}else{
				columnBuilder.append(columnName);
				valueBuilder.append("?");
			}
			preparedParamsList.add(valueMap.get(key));
			count++;
		}
		columnBuilder.append(") ");
		valueBuilder.append(") ");
		valueMapBuilder.append(columnBuilder);
		valueMapBuilder.append(valueBuilder);
		etModel.set("valueClause", valueMapBuilder.toString());
		etModel.set("valueParams", preparedParamsList);
		return etModel;
	}
	private  ETModel getSetCluase(ETModel setMap){
		ETModel etModel = new ETModel();
		List<Object> preparedParamsList = new ArrayList<>();
		StringBuilder setMapBuilder = new StringBuilder("SET ");
		Iterator<String> iterator = setMap.getKeyIterator();
		int count = 0;
		while(iterator.hasNext()){
			String key = iterator.next();
			String columnName = DataHandler.toUpperSplitCase(key);
			if(count > 0){
				setMapBuilder.append(",");
				setMapBuilder.append(columnName);
				setMapBuilder.append("=");
				setMapBuilder.append("?");
				
			}else{
				setMapBuilder.append(columnName);
				setMapBuilder.append("=");
				setMapBuilder.append("?");
			}
				preparedParamsList.add(setMap.get(key));
				count++;
		}
		setMapBuilder.append(" ");
		etModel.set("setClause", setMapBuilder.toString());
		etModel.set("setParams", preparedParamsList);
		return etModel;
	}
	private String getGroupByCluase(String groupByClause){
		if(groupByClause == null) return "";
		StringBuilder groupByBuilder = new StringBuilder("GROUP BY ");
		/*O389689.#itemNo#, O389689.#itemDesc1#, O537496.#lotNo#, O537496.#whseCode#*/
		groupByClause = DataHandler.replaceAllByProperNames(groupByClause);
		groupByBuilder.append(groupByClause);
		groupByBuilder.append(" ");
		return groupByBuilder.toString();
	}
	private String getHavingCluase(String havingClause){
		if(havingClause == null) return "";
		StringBuilder havingClauseBuilder = new StringBuilder("HAVING ");
		/*O389689.#itemNo#, O389689.#itemDesc1#, O537496.#lotNo#, O537496.#whseCode#*/
		havingClause = DataHandler.replaceAllByProperNames(havingClause);
		havingClauseBuilder.append(havingClause);
		havingClauseBuilder.append(" ");
		return havingClauseBuilder.toString();
	}

	private String getOrderByCluase(String orderByClause){
		if(orderByClause == null) return "";
		StringBuilder orderByClauseBuilder = new StringBuilder("ORDER BY ");
		/*O537496.#column1# ASC, O389689.#myColumn2# ASC, SUM(O537496.#column3#) ASC*/
		orderByClause = DataHandler.replaceAllByProperNames(orderByClause);
		orderByClauseBuilder.append(orderByClause);
		orderByClauseBuilder.append(" ");
		return orderByClauseBuilder.toString();
	}
	private String getLimitClause(String limtValue){
		if(limtValue == null) return "LIMIT 20 ";
		StringBuilder offSetBuilder = new StringBuilder("LIMIT ");
		offSetBuilder.append(limtValue);
		offSetBuilder.append(" ");
		return offSetBuilder.toString();
	}
	private String getOffsetClause(String offsetValue){
		if(offsetValue == null) return "OFFSET 0";
		StringBuilder offSetBuilder = new StringBuilder("OFFSET ");
		offSetBuilder.append(offsetValue);
		offSetBuilder.append(" ");
		return offSetBuilder.toString();
	}
}
