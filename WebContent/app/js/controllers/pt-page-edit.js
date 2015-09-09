var app = angular.module("app");
app.controller("ptPagesEditCntrl", function($scope, $stateParams,$state, platformUtils){
	
	var pageName = $stateParams.pn;
	$scope.pageDetails = {};
	$scope.pageDetails.html = "<super-header icon=\"Your Icon here"+
	"\" header=\"Page header here\" "+
	"sub-header=\"Your sub heading goes here!\"> \n</super-header>";

	$scope.pageDetails.js = "var app = angular.module(\"app\");\n"+
		"app.controller(\"yourCntrl\", function($scope,platformUtils){\n\n"+
		"\t\t/*Write your code here!*/\n\n"+
		"});";

	if(pageName){
		var options = {
					where : "#pageName# = ?",
					whereParams : [pageName],
					beforeAPICall: "ETPlatformPageQuery",
					skipAPI: true
					};
			platformUtils.query("etPages", options).then(function(response){
					if(response.data){
						$scope.pageDetails = response.data;
					}else{
						$state.go('app.admin.platfrom-pages.pt-page-edit', {pn:null});
					}
		});
		
	}
	$scope.refresh = true;
	$scope.selectTab = function() {
	    $scope.refresh = !$scope.refresh;
	};

	$scope.editorOptions = function(mode){
		return{
			 lineNumbers: true,
		      indentWithTabs: true,
		      theme:'eclipse',
		      lineWrapping : true,
		      htmlMode: true,
		      mode:mode,
		      styleActiveLine: true,
			  matchBrackets: true,
			  onLoad : function(_cm){
			      $scope.setCodeMirrorMode = function(mode){
			        _cm.setOption("mode", mode.toLowerCase());
			      };
			  }
		};
	};
	
	$scope.isEtPagesBusy = false;
	$scope.save = function(){
		$scope.isEtPagesBusy = true;
		var options = {
				skipAPI : true,
				beforeAPICall : "ETPlatformPagesSave",
				//returnRows:true
		};
		platformUtils.save("etPages", $scope.pageDetails, options).then(function(response){
			if(response.data && response.data.length > 0){
				$scope.isEtPagesBusy = false;
				$state.go('app.admin.platfrom-pages.pt-page-edit', {pn:response.data[0].pageName});
			}
		});
	};
	$scope.clear = function(){
		platformUtils.clearCache();
	};
	
});