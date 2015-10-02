var app = angular.module("app");
app.controller("etAppsNewCntrl", function($scope,Upload, $stateParams,$state, platformUtils){
		$scope.etApp = {};
		platformUtils.query("etTemplates").then(function(response){
			if(response.data && response.data.length >0){
					$scope.templates = response.data;
			}
		});
		$scope.selectTemplate = function(template){
			if(template){
				$scope.etApp.template = template.templateName;
				$scope.etApp.templateId = template.id;
			}
		};
		$scope.save = function(){
			var options = {afterAPICall: "ETAppStructureSave"};
			platformUtils.save("etApps", $scope.etApp,options );
		};
});