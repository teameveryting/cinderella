'use strict';
angular.module('cynderella').controller('templatesCntrl', function($scope, Upload, dbUtils,platformUtils,$timeout) {
	
	$scope.templateInfo = {};
		

	 $scope.addTemplate = function () {
		 
		 	/*File validation starts here!*/
		 	var files = [];
	        if($scope.thumbnail && $scope.thumbnail.length == 1){
	            if($scope.thumbnail[0].type.search('image/') > -1){
	            	files.push($scope.thumbnail[0]);
	            }else{
	                platformUtils.handleErrors("Don't show your smartness select only valid image");
	                return;
	            }
	        }else{
	             platformUtils.handleErrors("Plaese choose thumbnail file");
	             return;
	        }
	        if($scope.project && $scope.project.length == 1){
	            if($scope.project[0].type.search('application/zip') > -1){
	            	files.push($scope.project[0]);
	              }else{
	                  platformUtils.handleErrors("Don't show the smartness select only valid zip project file");
	                  return;
	               }
	         }else{
	                  platformUtils.handleErrors("Plaese choose project file");
	                  return;
	             }
		      if(files && files.length < 2){
		            platformUtils.handleErrors("should choose required files");
		            return;
		        }
		      /*File validation Ends here!*/
		
		var fileMapping = {thumbNail:$scope.thumbnail[0].name, project: $scope.project[0].name};
		platformUtils.uploadFormData( '/EappBuilder/api/proessOnServer',$scope.templateInfo,fileMapping,files)
	       .progress(function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope.log = 'progress: ' + progressPercentage + '% ';
                }).success(function (data) {
                    $timeout(function() {
                        $scope.log =  ' Response: \n' + JSON.stringify(data);
                    });
                });
    };

});

