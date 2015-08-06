package com.everyting.server.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.everyting.server.ServiceExecutor;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.FileIOManager;

public class APIManager {
	
	/*Query Call
	 * RequestData					    	
			{							
				dataSource:"dataSource", (Required)
				select:"d1.#column1# AS myColumn1, (SUM(d1.#column2#) )+( SUM(d2.#column2#) ) AS complexColumn...", (Optional advanced/usage)
				from:"#dataSource1# d1 JOIN #dataSource2# d2" (Optional advanced/usage)	    	
				where:"#cloumn1# < ? AND #column2# IN (?,?)", (Optional)
				whereParams:[1212, "yesterday", "today"],	(Optional)	
				orderBy:"#column# ASC", (Optional)
				groupBy:"#column1#, #column2# ..." (Optional),
				having: "#column1# > 1000 AND #column2# > 60" (Optional),
				limit:40, (Optional Default 20)		
				offset:45, (Optional Default 0)
				beforeAPICall:Interpreter1.java, (Optional)
				afterAPICall:Interpreter2.java (Optional)
			}
	 *	ResponseData
			{
				status:"success"
				data:[{Record1},{Record2},{Record3}....]
			}
	 */
	public ETModel manageQuery(ETModel requestData){
		ETModel responseData = new ETModel();
		ServiceExecutor serviceExecutor = new ServiceExecutor();
		/*Check for before Interpreters*/
		if(requestData.get("beforeAPICall") != null){
		}
		  if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			  List<ETModel> queryResult = serviceExecutor.executeQuery(requestData);
			  responseData.set("data", queryResult);
		  	}
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			
		}
		return responseData;
	}
	
	/*Insert/Update/Delete Call
	 RequestData					    	
			{							
				dataSource:"dataSource",
				valueMap:{column1:value1, column2:value2....}, (for Insert)
				setMap:{column1:value1, column2:value2....},(for update)	
				where:"#column1# => ? AND #column2# IN (?,?)", (for update/delete)
				whereParams:[4545, "from" ,"to"],
				beforeAPICall:Interpreter1.java,
				afterAPICall:Interpreter2.java	    	
			}
	*Response Data:
			{
				status:"success"
				data:{primaryKey:value} -- Successfully executed record's PK and value 
			}
	 */
	public ETModel manageExecuteUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		ServiceExecutor serviceExecutor = new ServiceExecutor();
		/*Check for before Interpreters*/
		if(requestData.get("beforeAPICall") != null){
		}
		if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			  ETModel updateResult = serviceExecutor.executeUpdate(action, requestData);
			  responseData.set("data", updateResult);
		 }
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			
		}
		return responseData;
	}
	/*Batch Insert/Update/Delete Call
	 RequestData					    	
			{							
				dataSource:"dataSource",
				valueMapList:[{column1:value1, column2:value2....},{column1:value1, column2:value2....}....],
				setMapList:[{column1:value1, column2:value2....},{column1:value1, column2:value2....}....],(for update)	
				where:"#column1# => ? AND #column2# IN (?,?)", (for update/delete)
				whereParams:[4545, "from" ,"to"],
				beforeAPICall:Interpreter1.java,
				afterAPICall:Interpreter2.java	    	
			}
	*Response Data:
			{
				status:"success"
				data:[{primaryKey:value},{primaryKey:value}...] (list of successfully executed record's PK and value)
			}
	 */
	public ETModel manageBatchExecuteUpdate(String action, ETModel requestData){
		ETModel responseData = new ETModel();
		ServiceExecutor serviceExecutor = new ServiceExecutor();
		/*Check for before Interpreters*/
		if(requestData.get("beforeAPICall") != null){
		}
		if(requestData.get("skipAPI") == null || !(boolean)requestData.get("skipAPI")){
			  List<ETModel> batchResults = serviceExecutor.batchExecuteUpdate(action, requestData);
			  responseData.set("data", batchResults);
		 }
		/*Check for after InterPreters*/
		if(requestData.get("afterAPICall") != null){
			
		}
		return responseData;
	}
	
	public void manageUploadFiles(HttpServletRequest request){
		List<ETModel> fileStreamList = FileIOManager.getUploadedFiles(request);
		if(fileStreamList != null){
			for(int i=0; i< fileStreamList.size(); i++){
				ETModel fileStreamData = fileStreamList.get(i);
			}
		}
	}
	
}
