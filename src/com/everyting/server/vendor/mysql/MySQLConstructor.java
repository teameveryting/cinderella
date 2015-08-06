package com.everyting.server.vendor.mysql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.ServiceExecutor;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;
import com.everyting.server.vendor.SQLConstructor;

public class MySQLConstructor implements SQLConstructor{
	
	public  final String ET_USERS_QUERY = "SELECT USER_ID,USER_NAME,DISPLAY_NAME,EMAIL_ADDRESS,ENCRYPT_PASSWORD,"
			+ "AVATAR_URL,LAST_LOGIN_DATE,LAST_LOGIN_IP"
			+ " FROM ET_USERS WHERE EMAIL_ADDRESS= ?";
	/* Provides SQL's Prepared select statement with array of values for '?' tags
	 * requestData ETModel(action, dataSource, whereClause, whereClauseParams...)
	 * ETModel (SQL (SQL Query String), preparedParams(Object[])
	 */
	@Override
	public  ETModel constructQuerySQL(ETModel requestData){
		ETModel responseData = new ETModel();
		StringBuilder sqlBuilder = new StringBuilder();
		String selectClause = "SELECT * ";
		String fromClause = "FROM ";
		String whereClause = "";
		String orderByClause = "";
		String groupByClause = "";
		String havingClause = "";
		String limitClause = "LIMT 20 ";
		String offsetClause = "OFFSET 0";
		Object[] preparedParamsArray = null;
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
				ETModel whereModel = getWhereClause((String)requestData.get("where"),(Object[])requestData.get("whereParams"));
				whereClause = (String)whereModel.get("whereClause");
				preparedParamsArray = (Object[])whereModel.get("whereClauseParams");
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
			/*Prevents NullPointerException at Runtime*/
			if(preparedParamsArray == null){
				preparedParamsArray = ArrayUtils.nullToEmpty(preparedParamsArray);
			}
			responseData.set("preparedParams", preparedParamsArray);
		return responseData;
	}
	/**
	 * Provides SQL's Prepared insert/update/delete statement with array of values for '?' tags
	 * @param requestData ETModel(action, dataSource, whereClause, whereClauseParams...)
	 * @return ETModel (SQL (SQL Query String), preparedParams(Object[]))
	 */
	@Override
	public ETModel constructExecuteUpdateSQL(String action, ETModel requestData){
		ETModel etModel = new ETModel();
		StringBuilder sqlBuilder = new StringBuilder();
		Object[] preparedParamsArray = null;
		Object[] valueParamsArray = null;
		Object[] setParamsArray = null;
		Object[] whereParamsArray = null;
		boolean isValidWhereCluase = false;
		if(action != null){
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
					valueParamsArray = (Object[])valueModel.get("valueParams");
				}
				else if("setMap".equalsIgnoreCase(key)){
					ETModel setModel = getSetCluase((ETModel)requestData.get("setMap"));
					setClause = (String) setModel.get("setClause");		
					setParamsArray = (Object[])setModel.get("setParams");
				}
				else if( "where".equalsIgnoreCase(key) || "whereParams".equalsIgnoreCase(key)){
					ETModel whereModel = getWhereClause((String)requestData.get("where"),(Object[])requestData.get("whereParams"));
					whereClause = (String)whereModel.get("whereClause");
					whereParamsArray = (Object[])whereModel.get("whereClauseParams");
					if(whereParamsArray != null && !ArrayUtils.isEmpty(whereParamsArray)){
						isValidWhereCluase = true;
					}
				}	
			}
			if(!isValidWhereCluase){
				if("update".equalsIgnoreCase(action) || "delete".equalsIgnoreCase(action)){
					throw new ETException("ET-NoWhereClause-001", "SQLConstructor throws NoWhereClause Exception while getWhereClause", 
							"Update/Delete operation should have valid whereClause");
				}
			}
			sqlBuilder.append(actionStatement);
			sqlBuilder.append(table);
			sqlBuilder.append(valueClause);
			sqlBuilder.append(setClause);
			sqlBuilder.append(whereClause);
			preparedParamsArray = ArrayUtils.addAll(valueParamsArray, setParamsArray);
			preparedParamsArray = ArrayUtils.addAll(preparedParamsArray, whereParamsArray);
		}
		etModel.set("sql", sqlBuilder.toString());
		/*Prevents NullPointerException at Runtime*/
		if(preparedParamsArray == null){
			preparedParamsArray = ArrayUtils.nullToEmpty(preparedParamsArray);
		}
		etModel.set("preparedParams", preparedParamsArray);
		return etModel;
	}
	@Override
	public List<ETModel> constructBatchExecuteUpdateSQL(String action,ETModel requestModel) {
		 List<ETModel> responseData = new ArrayList<>();
		if(action != null){
			if("batchInsert".equalsIgnoreCase(action)){
				 Object[] batchData = (Object[]) requestModel.get("valueMapList");
				 if(batchData != null){
					 for(int i=0; i < batchData.length; i++){
						 ETModel record = (ETModel) batchData[i];
						 requestModel.set("valueMap", record);
						 ETModel batchInsertSQL = constructExecuteUpdateSQL("insert", requestModel);
						// batchInsertSQL.set("requestData", batchData);
						 responseData.add(batchInsertSQL);
					 }
				 }
			}else if("batchUpdate".equalsIgnoreCase(action)){
				 Object[] batchData = (Object[]) requestModel.get("setMapList");
				 if(batchData != null){
					 for(int i=0; i < batchData.length; i++){
						 ETModel record = (ETModel) batchData[i];
						 requestModel.set("setMap", record);
						 ETModel batchUpdateSQL = constructExecuteUpdateSQL("update", requestModel);
						 batchUpdateSQL.set("requestData", batchData);
						 responseData.add(batchUpdateSQL);
					 }
				 }
			}else if("batchDelete".equalsIgnoreCase(action)){
				 ETModel batchDeleteSQL = constructExecuteUpdateSQL("delete", requestModel);
				 batchDeleteSQL.set("requestData", null);
				 responseData.add(batchDeleteSQL);
			}
		}
		return responseData;
	}
	public String constructTableDescSQL(String table){
		StringBuilder sqlBuilder = new StringBuilder("DESC ");
		sqlBuilder.append(DataHandler.toUpperSplitCase(table));
		return sqlBuilder.toString();
	}
	@SuppressWarnings("deprecation")
	public String getPKAIColumn(String table){
		String tableDescSql = constructTableDescSQL(table);
		ServiceExecutor serviceExecutor = new ServiceExecutor();
		List<ETModel> dsAttrList = serviceExecutor.executeQuery(tableDescSql, null);
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
		selectClause = replaceWithProperNames(selectClause);
		selectBuilder.append(selectClause);
		selectBuilder.append(" ");
		return selectBuilder.toString();
	}
	private String getFromClause(String fromCluase){
		if(fromCluase == null) return "FROM ";
		StringBuilder fromBuilder = new StringBuilder("FROM ");
		/*#dataSource1# d1 JOIN d2.#dataSource2#*/
		fromCluase = replaceWithProperNames(fromCluase);
		fromBuilder.append(fromCluase);
		fromBuilder.append(" ");
		return fromBuilder.toString();
	}
	private  ETModel getWhereClause(String whereClause, Object[] whereClauseParams){
		/*Ex: #column1# <= ? #column2# != ? AND #column3# in (?,?,?)*/
		ETModel etModel = new ETModel();
		StringBuilder whereClauseBuilder = new StringBuilder("WHERE ");
		Pattern pattern = Pattern.compile("#([a-zA-Z\\d]+)#");
		Matcher   matcher = pattern.matcher(whereClause);
		int valueCount = -1;
		while(matcher.find()){
			String rawColumnName = matcher.group().substring(1, matcher.group().length()-1);
			String tableCoumnName = DataHandler.toUpperSplitCase(rawColumnName);
			whereClause = whereClause.replaceAll("#"+rawColumnName+"#", tableCoumnName);
		};
		valueCount = StringUtils.countMatches(whereClause, "?");
		if(whereClauseParams != null){
			if(valueCount <= -1 || valueCount != whereClauseParams.length){
				throw new ETException("ET-InvalidWhereClauseParams-001", "SQLConstructor throws InvalidWhereClauseParams Exception while getWhereClause", 
										"WhereClauseParams are not matching with given whereClause");
			}
		}
		whereClauseBuilder.append(whereClause);
		whereClauseBuilder.append(" ");
		etModel.set("whereClause", whereClauseBuilder.toString());
		/*Prevents NullPointerException at Runtime*/
		if(whereClauseParams == null){
			whereClauseParams = ArrayUtils.nullToEmpty(whereClauseParams);
		}
		etModel.set("whereClauseParams", whereClauseParams);
		return etModel;
	}
	
	private  ETModel getValueCluase(ETModel valueMap){
		ETModel etModel = new ETModel();
		Object[] preparedParamsArray = new Object[valueMap.getSize()];
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
				preparedParamsArray[count] = valueMap.get(key);
			count++;
		}
		columnBuilder.append(") ");
		valueBuilder.append(") ");
		valueMapBuilder.append(columnBuilder);
		valueMapBuilder.append(valueBuilder);
		etModel.set("valueClause", valueMapBuilder.toString());
		etModel.set("valueParams", preparedParamsArray);
		return etModel;
	}
	private  ETModel getSetCluase(ETModel setMap){
		ETModel etModel = new ETModel();
		Object[] preparedParamsArray = new Object[setMap.getSize()];
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
				preparedParamsArray[count] = setMap.get(key);
			count++;
		}
		setMapBuilder.append(" ");
		etModel.set("setClause", setMapBuilder.toString());
		etModel.set("setParams", preparedParamsArray);
		return etModel;
	}
	
	private String getGroupByCluase(String groupByClause){
		if(groupByClause == null) return "";
		StringBuilder groupByBuilder = new StringBuilder("GROUP BY ");
		/*O389689.#itemNo#, O389689.#itemDesc1#, O537496.#lotNo#, O537496.#whseCode#*/
		groupByClause = replaceWithProperNames(groupByClause);
		groupByBuilder.append(groupByClause);
		groupByBuilder.append(" ");
		return groupByBuilder.toString();
	}
	
	private String getHavingCluase(String havingClause){
		if(havingClause == null) return "";
		StringBuilder havingClauseBuilder = new StringBuilder("HAVING ");
		/*O389689.#itemNo#, O389689.#itemDesc1#, O537496.#lotNo#, O537496.#whseCode#*/
		havingClause = replaceWithProperNames(havingClause);
		havingClauseBuilder.append(havingClause);
		havingClauseBuilder.append(" ");
		return havingClauseBuilder.toString();
	}

	private String getOrderByCluase(String orderByClause){
		if(orderByClause == null) return "";
		StringBuilder orderByClauseBuilder = new StringBuilder("ORDER BY ");
		/*O537496.#column1# ASC, O389689.#myColumn2# ASC, SUM(O537496.#column3#) ASC*/
		orderByClause = replaceWithProperNames(orderByClause);
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
	private String replaceWithProperNames(String rawString){
		Pattern pattern = Pattern.compile("#([a-zA-Z\\d]+)#");
		Matcher matcher = pattern.matcher(rawString);
		while(matcher.find()){
			String rawColumnName = matcher.group().substring(1, matcher.group().length()-1);
			String tableCoumnName = DataHandler.toUpperSplitCase(rawColumnName);
			rawString = rawString.replaceAll("#"+rawColumnName+"#", tableCoumnName);
		};
		return rawString;
	}
	
	public String  getUserLoginSQL(){
		return this.ET_USERS_QUERY;
	}
	
	/*Test here!*/
	@SuppressWarnings("unused")
	public static void main(String[] args) throws JSONException {
		try{
			
			String rawQuerySQL = "{ "
								+ "\"dataSource\": \"etTemplateStructure\", "
								+ "\"where\":\"#templateId# = ? AND #lastUpdatedDate# >= sysdate AND #uid# in (?,?,?)\","
								+"\"whereParams\":[1001,1002,1003,1004 ],"
								+"limit:40,"
								+"offset:30"
								+ "}";
			
			String rawInsertSQL = "{"
								+ " \"dataSource\": \"etTemplateStructure\", "
								+ "\"valueMap\":{uid:\"7889-552898-8989\","
								+ 				"type:\"folder\","
								+				"title:\"Silde Menu with tabs\","
								+				"parentUid:\"8585-989-6363\""
								+				"},"
								+ "}";
			String rawBatchInsertSQL = "{"
					+ " \"dataSource\": \"etTemplateStructure\", "
					+ "\"valueMapList\":["
					+ "						{	uid:\"7889-552898-8989\","
					+ 							"type:\"folder\","
					+							"title:\"Item1\","
					+							"parentUid:\"8585-989-6363\""
					+						"},"
					+ "						{	uid:\"7889-552898-8990\","
					+ 							"type:\"file\","
					+							"title:\"Silde Menu with tabs\","
					+							"parentUid:\"7889-552898-8989\""
					+						"},"
					+ "						{	uid:\"7889-552898-7070\","
					+ 							"type:\"folder\","
					+							"title:\"Item3\","
					+							"parentUid:\"7889-552898-8990\""
					+						"}"
					+ 			"],"
					+ "}";
			
			String rawUpdateSQL = "{"
					+ " \"dataSource\": \"etTemplateStructure\", "
					+ "\"setMap\":{  uid:\"7889-552898-8989\","
					+ 				"type:\"folder\","
					+				"title:\"Silde Menu with tabs\","
					+				"parentUid:\"8585-989-6363\""
					+				"},"
					+ "\"where\":\"#templateId# = ? AND #lastUpdatedDate# >= sysdate AND #uid# in (?,?,?)\","
					+"\"whereParams\":[1001,1002,1003,\"test\" ]"
					+ "}";
			String rawBatchUpdateSQL = "{"
					+ " \"dataSource\": \"etTemplateStructure\", "
					+ "\"setMapList\":["
					+ "						{	uid:\"7889-552898-8989\","
					+ 							"type:\"folder\","
					+							"title:\"Item1\","
					+							"parentUid:\"8585-989-6363\""
					+						"},"
					+ "						{	uid:\"7889-552898-8990\","
					+ 							"type:\"file\","
					+							"title:\"Silde Menu with tabs\","
					+							"parentUid:\"7889-552898-8989\""
					+						"},"
					+ "						{	uid:\"7889-552898-7070\","
					+ 							"type:\"folder\","
					+							"title:\"Item3\","
					+							"parentUid:\"7889-552898-8990\""
					+						"}"
					+ 			"],"
					+ "\"where\":\"#templateId# = ? AND #lastUpdatedDate# >= sysdate AND #uid# in (?,?,?)\","
					+"\"whereParams\":[1001,1002,1003,1004 ]"
					+ "}";
			String rawDeleteSQL = "{"
					+ " \"dataSource\": \"etTemplateStructure\", "
					+ "\"where\":\"#templateId# = ? AND #lastUpdatedDate# >= sysdate AND #uid# in (?,?,?)\","
					+"\"whereParams\":[1001,1002,1003,1004 ]"
					+ "}";
			
			JSONObject  myJson = new JSONObject(rawQuerySQL);
			ETModel test = DataHandler.toETModel(myJson);
			
			MySQLConstructor mySQLConstructor = new MySQLConstructor();
			ETModel responseModel = mySQLConstructor.constructQuerySQL(test);
			String sql = (String) responseModel.get("sql");
			Object[] params = (Object[]) responseModel.get("preparedParams");
			
			System.out.println(sql);
			for(int i=0 ;i < params.length ;i++){
				System.out.println(params[i]);
			}
			
			
		/*	JSONObject rawJSONObject = new JSONObject(rawBatchUpdateSQL);
			ETModel etModel = DataHandler.toETModel(rawJSONObject);
			
			ETModel responseModel = mySQLConstructo	JSONObject rawJSONObject = new JSONObject(rawBatchUpdateSQL);
			ETModel etModel = DataHandler.toETModel(rawJSONObject);
			
			/*ETModel responseModel = mySQLConstructor.constructQuerySQL(etModel);
			ETModel responseModel = mySQLConstructor.constructExecuteUpdateSQL("update",etModel);
			Object[] preparedParams = (Object[]) responseModel.get("preparedParams");
			System.out.println(responseModel.get("sql"));
			for(int i=0; i< preparedParams.length; i++){
				System.out.println( i+":" + preparedParams[i]);
			}
			
			List<ETModel> batchResultModel = mySQLConstructor.constructBatchExecuteUpdateSQL("batchUpdate", etModel);
				for(ETModel batchEntry: batchResultModel){
					System.out.println("SQL:" + batchEntry.get("sql"));
					for(Object valueEntry: (Object[]) batchEntry.get("preparedParams")){
						System.out.println("Value:" + valueEntry);
					}
				}
			
			String select = "O389689.#itemNo# AS ITEM_NO, O389689.#itemDesc1# AS ITEM_DESC1, O537496.#lotNo# AS LOT_NO, "
								 + "O537496.#whseCode# AS WHSE_CODE, ( SUM(O537496.#loctOnhand#) )+( SUM(O537496.#commitQty#) ) AS CALCULATION0, "
								 + "SUM(O537496.#commitQty#) AS COMMIT_QTY_SUM, SUM(O537496.#loctOnhand#) AS LOCT_ONHAND_SUM";
			String from = "GMI.#icItemMstB# O389689, APPS.#icItemInvV# O537496";
			String where = "((O537496.#itemId# = O389689.#itemId# ) ) AND ( O537496.#whseCode# = 'EU' ) AND "
								+"( O537496.#location# <> 'NONE' AND O537496.#location# <> '460' AND O537496.#location# <> '325' )";
			String groupBy = "O389689.#itemNo#, O389689.#itemDesc1#, O537496.#lotNo#, O537496.WHSE_CODE";
			String having = "#column1# > 1000 AND #column2# > 60";
			String oderBy = "O537496.#whseCode# ASC, O389689.#itemNo# ASC, SUM(O537496.#loctOnhand#) ASC";
			String limit = "30";
			String offset ="20";
			
			ETModel queryModel = new ETModel();
			queryModel.set("select", select);
			queryModel.set("from", from);
			queryModel.set("where", where);
			queryModel.set("groupBy", groupBy);
			queryModel.set("having", having);
			queryModel.set("orderBy", oderBy);
			queryModel.set("limit", limit);
			queryModel.set("offset", offset);r.constructQuerySQL(etModel);
			ETModel responseModel = mySQLConstructor.constructExecuteUpdateSQL("update",etModel);
			Object[] preparedParams = (Object[]) responseModel.get("preparedParams");
			System.out.println(responseModel.get("sql"));
			for(int i=0; i< preparedParams.length; i++){
				System.out.println( i+":" + preparedParams[i]);
			}
			
			List<ETModel> batchResultModel = mySQLConstructor.constructBatchExecuteUpdateSQL("batchUpdate", etModel);
				for(ETModel batchEntry: batchResultModel){
					System.out.println("SQL:" + batchEntry.get("sql"));
					for(Object valueEntry: (Object[]) batchEntry.get("preparedParams")){
						System.out.println("Value:" + valueEntry);
					}
				}
			
			String select = "O389689.#itemNo# AS ITEM_NO, O389689.#itemDesc1# AS ITEM_DESC1, O537496.#lotNo# AS LOT_NO, "
								 + "O537496.#whseCode# AS WHSE_CODE, ( SUM(O537496.#loctOnhand#) )+( SUM(O537496.#commitQty#) ) AS CALCULATION0, "
								 + "SUM(O537496.#commitQty#) AS COMMIT_QTY_SUM, SUM(O537496.#loctOnhand#) AS LOCT_ONHAND_SUM";
			String from = "GMI.#icItemMstB# O389689, APPS.#icItemInvV# O537496";
			String where = "((O537496.#itemId# = O389689.#itemId# ) ) AND ( O537496.#whseCode# = 'EU' ) AND "
								+"( O537496.#location# <> 'NONE' AND O537496.#location# <> '460' AND O537496.#location# <> '325' )";
			String groupBy = "O389689.#itemNo#, O389689.#itemDesc1#, O537496.#lotNo#, O537496.WHSE_CODE";
			String having = "#column1# > 1000 AND #column2# > 60";
			String oderBy = "O537496.#whseCode# ASC, O389689.#itemNo# ASC, SUM(O537496.#loctOnhand#) ASC";
			String limit = "30";
			String offset ="20";
			
			ETModel queryModel = new ETModel();
			queryModel.set("select", select);
			queryModel.set("from", from);
			queryModel.set("where", where);
			queryModel.set("groupBy", groupBy);
			queryModel.set("having", having);
			queryModel.set("orderBy", oderBy);
			queryModel.set("limit", limit);
			queryModel.set("offset", offset);*/
			
			//System.out.println(""+ mySQLConstructor.constructQuerySQL(queryModel).get("sql"));
		}catch(ETException e){
			System.out.println("ErrorCode:" + e.getErrorCode());
			System.out.println("Title:" + e.getTitle());
			System.out.println("Msg:" + e.getMessage());
		} catch(RuntimeException e){
			System.out.println("RuntimeException:" + e.getMessage());
		} 
	}
}
