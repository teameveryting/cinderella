var app = angular.module("app");
app.controller("etAppsCntrl", function($scope, platformUtils){
	
	$scope.platformUtils = platformUtils;
	platformUtils.query("etApps").then(function(response){
		if(response.data && response.data.length > 0){
			$scope.etApps = response.data;
		}
	});
});