package com.everyting.server.test;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.ServiceExecutor;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;

public class ServiceExcecutorTest {

	public static void main(String[] args) {
		 try{	
			main();
		 }catch(ETException exception){
			 System.out.println(exception.getErrorCode());
			 System.out.println(exception.getTitle());
			 System.out.println(exception.getMessage());
		 }catch(RuntimeException exception){
			 System.out.println("RuntimeException:" + exception.getMessage());
		 } catch (JSONException e) {
			 System.out.println("JSONException:" + e.getMessage());
		}
	}
	
	public static void main() throws JSONException {
		ServiceExecutor serviceExecutor = new ServiceExecutor();

		/*Insert Operation Test*/
		/*ETModel insertETModel = DataHandler.toETModel(getJSONInsert());
		ETModel insertResult = serviceExecutor.executeUpdate("insert",insertETModel);
		System.out.println(insertResult.toString());*/
		
		/*Update Operation Test*/
		/*ETModel updateETModel = DataHandler.toETModel(getJSONUpdate());
		ETModel updateResult = serviceExecutor.executeUpdate("update",updateETModel);
		System.out.println(updateResult.toString());*/
				
		
		/*Delete Operation Test*/
		/*ETModel deleteETModel = DataHandler.toETModel(getJSONDelete());
		ETModel deleteResult = serviceExecutor.executeUpdate("delete",deleteETModel);
		System.out.println(deleteResult.toString());*/
		
		/*Batch Insert Operation Test*/
		ETModel batchInsertETModel = DataHandler.toETModel(getJSONBatchInsert());
		List<ETModel> batchInsertResult = serviceExecutor.batchExecuteUpdate("batchInsert", batchInsertETModel);
		System.out.println(batchInsertResult.toString());
		
		/*Batch Update Operation Test*/
		/*ETModel batchUpdateETModel = DataHandler.toETModel(getJSONBatchUpdate());
		List<ETModel> batchUpdateResult = serviceExecutor.batchExecuteUpdate("batchUpdate", batchUpdateETModel);
		System.out.println(batchUpdateResult.toString());*/
		
		/*Batch Delete Operation Test*/
		/*ETModel batchDeleteETModel = DataHandler.toETModel(getJSONBatchDelete());
		List<ETModel> batchDeleteResult = serviceExecutor.batchExecuteUpdate("batchDelete", batchDeleteETModel);
		System.out.println(batchDeleteResult.toString());*/
		
	}
	/* Insert operation
	 * RequestData:
	 * {
	 * 		dataSource :"ET_APPS"
	 * 		valueMap:{
	 * 					title:"test title 1",
	 * 					theme:"ionic",
	 * 					templateId : 5,
	 * 					createdBy: 1,
	 * 					creationDate: null,
	 * 				}
	 * }
	 * */
	public static JSONObject getJSONInsert() throws JSONException{
		String rawJSON = "{";
			   rawJSON += "dataSource:\"ET_APPS\",";
			   rawJSON += "valueMap:{";
			   rawJSON += "			title:\"test title 1\",";
			   rawJSON += "			theme:\"ionic\",";
			   rawJSON += "			templateId : 5,";
			   rawJSON += "			createdBy: 5,";
			   rawJSON += "		}";
			   rawJSON += "}";
		return new JSONObject(rawJSON);
	}
	/* Update Operation
	 * RequestData:
	 * {
	 * 		dataSource :"ET_APPS"
	 * 		setMap:{
	 * 					title:"My App updated",
	 * 					theme:"Angle",
	 * 					templateId : 8
	 * 				},
	 * 		where: "#title# = ? AND TEMPLATE_ID = ?",
	 * 		whereParams:["test title 1", 5 ]							
	 * }
	 * */
	public static JSONObject getJSONUpdate() throws JSONException{
		String rawJSON = "{";
			   rawJSON += "dataSource :\"ET_APPS\",";
			   rawJSON += "setMap:{";
			   rawJSON += "			title:\"My App updated\",";
			   rawJSON += "			theme:\"Angle\",";
			   rawJSON += "			templateId :8";
			   rawJSON += "		},";
			   rawJSON += "where: \"#title# = ? AND TEMPLATE_ID = ?\",";
			   rawJSON += "whereParams:[\"test title 1\", 5 ]";
			   rawJSON += "}";
		return new JSONObject(rawJSON);
	}
	/* Delete Operation
	 * RequestData:
	 * {
	 * 		where: "#title# = ? AND TEMPLATE_ID = ?",
	 * 		whereParams:["test title 1", 5 ]							
	 * }
	 * */
	public static JSONObject getJSONDelete() throws JSONException{
		String rawJSON = "{";
			   rawJSON += "dataSource :\"ET_APPS\",";
			   rawJSON += "where: \"#title# = ? AND TEMPLATE_ID = ?\",";
			   rawJSON += "whereParams:[\"My App updated\", 8 ]";
			   rawJSON += "}";
		return new JSONObject(rawJSON);
	}
	/* Batch Insert Operation
	 * RequestData:
	 * {
	 * 	 dataSource :"ET_APPS",
	 *   valueMapList :[
	 *   		{
	 * 					title:"Test Title 5",
	 * 					theme:"ionic",
	 * 					templateId : 15
	 * 			},
	 * 	 		{
	 * 					title:"Test Title 6",
	 * 					theme:"ionic",
	 * 					templateId : 16
	 * 			},
	 * 	  		{
	 * 					title:"Test Title 7",
	 * 					theme:"ionic",
	 * 					templateId : 17
	 * 			}
	 *   ]						
	 * }
	 * */
	public static JSONObject getJSONBatchInsert() throws JSONException{
		String rawJSON = "{";
			   rawJSON += "dataSource :\"ET_APPS\",";
			   rawJSON += "valueMapList :[";
			   rawJSON += "{ title:\"Test Title 5\",";
			   rawJSON += "theme:\"ionic\",";
			   rawJSON += "templateId : 15 },";
			   rawJSON += "{ title:\"Test Title 6\",";
			   rawJSON += "theme:\"ionic\",";
			   rawJSON += "templateId : 16 },";
			   rawJSON += "{ title:\"Test Title 7\",";
			   rawJSON += "theme:\"ionic\",";
			   rawJSON += "templateId : 17 } ]";
			   rawJSON += "}";
		return new JSONObject(rawJSON);
	}
	/* Batch Update Operation
	 * RequestData:
	 * {
	 * 	 dataSource :"ET_APPS",
	 *   setMapList :[
	 *   		{
	 * 					title:"Test Title 8",
	 * 					theme:"Angle",
	 * 					templateId : 18
	 * 			},
	 * 	 		{
	 * 					title:"Test Title 9",
	 * 					theme:"Angle",
	 * 					templateId : 19
	 * 			}
	 *   	],	
	 *   where: "#title# = ? AND #templateId# = ?"	
	 *   whereParamsList:[ ["test title 6", 16 ], ["test title 7", 17 ] ]
	 * }
	 * */
	public static JSONObject getJSONBatchUpdate() throws JSONException{
		String rawJSON = "{";
			   rawJSON += "dataSource :\"ET_APPS\",";
			   rawJSON += "setMapList :[";
			   rawJSON += "{ title:\"Test Title 8\",";
			   rawJSON += "theme:\"Angle\",";
			   rawJSON += "templateId : 21 },";
			   rawJSON += "{ title:\"Test Title 9\",";
			   rawJSON += "theme:\"Angle\",";
			   rawJSON += "templateId : 22 } ],";
			   rawJSON += "where: \"  #templateId# = ?\",";
			   rawJSON += "whereParamsList:[";
			   rawJSON += "[18],";
			   rawJSON += "[19] ]";
			   rawJSON += "}";
		return new JSONObject(rawJSON);
	}
	/* Batch Delete Operation
	 * RequestData:
	 * {
	 * 	 dataSource :"ET_APPS",
	 *   where: "#title# = ? AND #templateId# = ?"	
	 *   whereParamsList:[ ["test title 6", 16 ], ["test title 7", 17 ] ]
	 * }
	 * */
	public static JSONObject getJSONBatchDelete() throws JSONException{
		String rawJSON = "{";
			   rawJSON += "dataSource :\"ET_APPS\",";
			   rawJSON += "where: \" #templateId# = ?\",";
			   rawJSON += "whereParamsList:[";
			   rawJSON += "[ 15 ],";
			   rawJSON += "[ 16 ],";
			   rawJSON += "[ 21 ],";
			   rawJSON += "[ 22 ],";
			   rawJSON += "[ 25 ],";
			   rawJSON += "[ 17 ] ]";
			   rawJSON += "}";
		return new JSONObject(rawJSON);
	}
}
