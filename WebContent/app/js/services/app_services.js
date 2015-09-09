'use strict';
var app = angular.module("app");
app.factory("platformUtils",  [ 'dialogs','$http','$q','$rootScope','$location','localStorageService',
                                function(dialogs, $http, $q, $rootScope, $location, localStorageService){ 
	var platformUtils = {};
	var update = function(dataSource, requestData,pkColumnName, options){
			var data = {};
			data.dataSource = dataSource;
			data.setMap = requestData;
			for (var key in options) {
				  if (options.hasOwnProperty(key)) {
					  if(options[key]){
						  data[key] = options[key]; 
					  }
				  }
				}
			/*Construct default where clause, if user doesn't supply one*/
			if(!(requestData.where)){
				data.where = pkColumnName +" = ?";
				var pkColumnValue = requestData[platformUtils.toCamelCase(pkColumnName)];
				if(!pkColumnValue){
					platformUtils.alert("error", "InvalidWhereClause", 
										"The requested data for " + dataSource + " do not have suffient data to build default where clause" );
					 return {};
				}
				data.whereParams = [pkColumnValue];
			}
			var stringifiedData = JSON.stringify(data);
			return platformUtils.post("api/update", stringifiedData);
	};
	var insert = function(dataSource, requestData, options){
			var data = {};
			data.dataSource = dataSource;
			data.valueMap = requestData;
			data.returnRows = true;
			for (var key in options) {
				  if (options.hasOwnProperty(key)) {
					  if(options[key]){
						  data[key] = options[key]; 
					  }
				  }
			}
			var stringifiedData = JSON.stringify(data);
			return platformUtils.post("api/insert", stringifiedData);
	};
	
	platformUtils.getBaseUrl = function(){
		return $location.protocol() + "://" + $location.host() + ":" + $location.port() + "/EappBuilder/";
	};
	platformUtils.getDownloadBlobUrl = function(blobID, fileName, isInline){
		return "service/blob/download/" + blobID +"/"+ fileName +"_index.html?inline = " +isInline;
	};
	platformUtils.getResourceUrl = function(resoureName){
		return platformUtils.getBaseUrl() + resoureName;
	};
	platformUtils.post = function(operation, data){
			return $http.post( platformUtils.getBaseUrl()+operation, data);	
	};
	platformUtils.alert = function(type, header, message){
		if(type === 'error'){
			dialogs.error( header,message,{});
		}if(type === 'info'){
			dialogs.confirm( header,message,{});
		}
	};
	platformUtils.getRandomColor = function(){
		var themes = ["red", "pink", "purple", "deep-purple", "indigo", 
		              "blue", "light-blue", "cyan", "teal", "green", 
		              "yellow", "amber", "orange", "brown", "deep-orange", 
		              "grey", "blue-grey"];
		return  themes[Math.floor(Math.random() * themes.length)];
	};
	platformUtils.toUpperSplitCase = function(string){
		if(!string) return string;
		if((string.toUpperCase() === string) || (string.indexOf('_') > -1)) return string;
		var stringArr = string.split("");
		var resultString = string;
		for(var i=0; i < stringArr.length; i++){
			var char = stringArr[i];
			if(i == 0) resultString = char.toUpperCase();
			else{
				if( char.toUpperCase() === char) char = "_" + char;
				else char = char.toUpperCase();
				resultString += char;
			}	
		}
		return resultString;
	};
	platformUtils.toCamelCase = function(string){
		if(!string) return string;
		var stringArr = string.split("_");
		var resultString = "";
		for(var i=0; i < stringArr.length; i++){
			var splitString = stringArr[i];
			if(resultString.length > 0){
				resultString += splitString.substring(0,1).toUpperCase() + splitString.substring(1).toLowerCase();
			}else{
				resultString = splitString.toLowerCase();
			}
		}
		return resultString;
	};
	platformUtils.getLocalStorage = function(){
			return localStorageService;
	};
	platformUtils.storeDS = function(){
		/*Query for ET_DS & ET_DS_ATTRS*/
	    var options = {
	    		ignoreLocalCheck: true,
	    		beforeAPICall : "ETDSInfoResolve"
	    	};
	    platformUtils.query("ET_DS", options ).then(function(response){
	    	if(response.data && response.data.length > 0){
	    		/*Clear All of localStorageService*/
	    		localStorageService.clearAll();
	    		for(var i=0; i <  response.data.length; i++){
	    			var item = response.data[i];
	    			var tableName = item.dsInfo.tableName;
	    			tableName = platformUtils.toCamelCase(tableName);
	    			/*For the sake of uniqueness from other variables*/
	    			localStorageService.set(tableName + "#ds#",JSON.stringify(item));
	    		}
	    	}
	    });
	};
	platformUtils.clearCache = function(){
		localStorageService.clearAll();
	};
	platformUtils.getDSInfo =function(dataSource){
		var ds = dataSource+"#ds#";
		var storedDsInfo =  JSON.parse(localStorageService.get(ds));
		if(storedDsInfo === null){
			platformUtils.alert("error", "Invalid DataSource", "\""+dataSource +"\" no such datasource is exists");
		}
		return storedDsInfo;
	};

	platformUtils.query = function(dataSource, options){
		var ignoreLocalCheck = null;
			if(options != null){
			ignoreLocalCheck = options.ignoreLocalCheck;
		}
		var deffered = $q.defer();
			if((!(ignoreLocalCheck !== null && ignoreLocalCheck)) && (!platformUtils.getDSInfo(dataSource))) 
				{deffered.resolve({});return deffered.promise;};
			var data =  options || {};
			data.dataSource = dataSource;
			var stringifiedData = JSON.stringify(data);
			
			platformUtils.post("api/query", stringifiedData).then(function(response){
				if(response.data.status === "success"){
					var responseData = response.data;
					deffered.resolve(responseData);
				}else if(response.data.status === "error"){
					platformUtils.alert("error", response.data.data.type, response.data.data.message );
				}
			});
		return deffered.promise;
	};
	platformUtils.save =function(dataSource, requestData, options){
		options =options || {};
		var ignoreLocalCheck = options.ignoreLocalCheck;
		var deffered = $q.defer();
		var pkColumnName = null;
		if(!(ignoreLocalCheck !== null && ignoreLocalCheck)){
			var localDS = platformUtils.getDSInfo(dataSource);
			if(!localDS) {deffered.resolve({});return deffered.promise;}
			pkColumnName = localDS.dsInfo.pkName;
		}
		if(options.pkColumn) pkColumnName = options.pkColumn;
		pkColumnName = platformUtils.toCamelCase(pkColumnName);
		/*Request is to update*/
		if(pkColumnName && requestData[pkColumnName]){
			update(dataSource, requestData, pkColumnName, options).then(function(response){
				if(response.data.status === "success"){
					deffered.resolve(response.data);
				}else if(response.data.status === "error"){
					platformUtils.alert("error", response.data.data.type, response.data.data.message );
					deffered.resolve({});
				};
			});
		}else{
			insert(dataSource, requestData, options).then(function(response){
				if(response.data.status === "success"){
					deffered.resolve(response.data);
				}else if(response.data.status === "error"){
					platformUtils.alert("error", response.data.data.type, response.data.data.message );
					deffered.resolve({});
				};
			});
		}
		return deffered.promise;
	};
	platformUtils.processOnserver = function(){
			
	};
	platformUtils.logout = function(){
		
	};
	return platformUtils;
}]);

/*Query Call
RequestData
	{	select : "COLUMN1, #column2#", (optional)
		dataSource:"dataSource",
		from : "#table1#, #table2#", (optional)
		where: "#table1#.#columnName# = ? AND #table2#.columnName IN [?,?]" (optional)
		whereParams:['test', 'test1', 'test2'] (Required only if where presents)
		orderBy:"#coumn1# ASC" (optional)
		groupBy: "custom group by", (optional)
		having:"custom having", (optional)
		limit: "30", (optional, Default: 20)
		offset: "20" (optional, Default: 0)
	}
 Response Data:
	{
		status:"success"
		data:[{Row1}, {Row2} ...]
		or
		status:"error"
		data:{type:"error type", message: "error message"}
	}
*/

/*Insert Call
RequestData					    	
		{							
			dataSource:"dataSource",
			valueMap:{column1:value1, column2:value2....}, (for Insert)
			beforeAPICall:Interpreter1.java,
			afterAPICall:Interpreter2.java	    	
		}
Response Data:
		{
			status:"success"
			data:{Inserted Record columns, affectedRows:-- } -- Successfully executed record's PK and value 
			or
			status:"error"
			data:{type:"error type", message: "error message"}
		}
*/

/* UPdate Call
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