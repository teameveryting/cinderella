var app = angular.module("app");
app.controller("ptPagesCntrl", function($scope, platformUtils){
	
	platformUtils.query("etPages").then(function(response){
		if(response.data && response.data.length > 0){
			$scope.platformPages = response.data;
		}
	});
});