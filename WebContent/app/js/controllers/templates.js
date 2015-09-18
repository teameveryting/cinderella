var app = angular.module("app");
app.controller("templateCntrl", function($scope, platformUtils){
	
	$scope.platformUtils = platformUtils;
	platformUtils.query("etTemplates").then(function(response){
		if(response.data && response.data.length > 0){
			$scope.templates = response.data;
		}
	});
});