var app = angular.module("app");
app.controller("templateEditCntrl", function($scope,Upload, $stateParams,$state, platformUtils, toaster, $timeout){
	
	var files = {};
	var validateThumbnail = function(){
		files.thumbnail = [];
		   if($scope.thumbnail && $scope.thumbnail.length == 1){
	            if($scope.thumbnail[0].type.search('image/') > -1){
	            	files.thumbnail.push($scope.thumbnail[0]);
	            	return true;
	            }else  platformUtils.alert("error", "Invalid Image type",  "Only valid images are accepted!");
	        }else  platformUtils.alert("error", "Invalid thumbnail",  "Please choose thumbnail file");
		   return false;
	};
	var validateProjectFile = function(){
		files.project = [];
		   if($scope.project && $scope.project.length == 1){
	            if($scope.project[0].type.search('application/zip') > -1){
	            	files.project.push($scope.project[0]);
	            	return true;
	              }else platformUtils.alert("error", "Invalid Project type",  "Only zip project file is accepted!");
	         }else platformUtils.alert("error", "Invalid Project File",  "Please choose project file!");
		   return false;
	};
	$scope.reset = function(){
		$scope.project = [];
		$scope.thumbnail = [];
		$scope.templateDetails = {};
	};
	$scope.saveTemplate = function(){
		if(!validateThumbnail()){return;}
		if(!validateProjectFile()){return;}
		var fileMapping = {thumbNail:$scope.thumbnail[0].name, project: $scope.project[0].name};
		var files = [];
		files.push($scope.thumbnail[0]);
		files.push($scope.project[0]);
		var options = {beforeAPICall :"ETTemplatesSave"};
		platformUtils.uploadFileFormData($scope.templateDetails,fileMapping,files, options)
	       .progress(function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope.log = 'progress: ' + progressPercentage + '% ';
                }).success(function (data) {
                    $timeout(function() {
                    	if(data.status === "error"){
                    		platformUtils.alert("error", data.data.type, data.data.message );
                    	}else{
                    		$scope.log = $scope.templateDetails.templateName + ' template successfully saved';
                    		$scope.reset();
                    	}
                    });
           });
	};
});