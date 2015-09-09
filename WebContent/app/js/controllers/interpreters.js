var app = angular.module("app");
app.controller("interpreterCntrl", function($scope, platformUtils){
	
	$scope.platformUtils = platformUtils;
	platformUtils.query("etInterpreters").then(function(response){
		if(response.data && response.data.length > 0){
			$scope.interpreters = response.data;
		}
	});
});