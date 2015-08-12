'use strict';
var app = angular.module("app")
.factory('dbUtils', [ '$q','$http' ,'$timeout', function( $q, $http,  $timeout){
return{
	post:function(operation, data){
		return $http.post("/EappBuilder/api/"+operation, data);
		},
	query:function(dataSource,requestdata){
		var q = $q.defer();
		requestdata.dataSource = dataSource;
		var stringifiedRequest = JSON.stringify(request);
		 this.post("query", stringifiedRequest).then(function(response){
			 var queryResult = response.data;
			if(queryResult.status){
					progress.isBusy = false;
					q.resolve(queryResult);
				}
			});
			 return q.promise;
		},
	save:function(dataSource, data){
		var q = $q.defer();
		var requestData = {};
		requestData.dataSource = dataSource;
		requestData.data = data;
		this.post("save", requestData).then(function(response){
			 var queryResult = response.data;
			if(queryResult.status){
					q.resolve(queryResult);
				}
			});
			 return q.promise;
		}	
	};
 }])
.factory('platformUtils', [ 'dialogs','Upload','$http','$q', function(dialogs, Upload, $http, $q){
return {
	
	post:function(url, data){
		return $http.post(url, data);
		},
	handleErrors:function(errorDetails){
		dialogs.error('Error',errorDetails,{});
		},
	 uploadFormData:function(data, fileMapping, files){
		 return  Upload.upload({
	            url: '/EappBuilder/api/uploadFileForm',
		           fields: {
		                'data': JSON.stringify(data),
		                'fileMapping':JSON.stringify(fileMapping || {})
		            },
		            file: files
		        });
	 },
	 uploadFile:function(files){
		 return  Upload.upload({
	            url: '/EappBuilder/api/uploadFile',
		            file: files
		        });
	 		},
	 logOut:function(){
		 var q = $q.defer();
		  this.post("/EappBuilder/logout", null).then(function(response){
			 q.resolve(response.data); 
		  });
		 return q.promise;
	 }
	};
}]);