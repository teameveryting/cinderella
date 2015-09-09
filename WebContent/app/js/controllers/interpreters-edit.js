var app = angular.module("app");
app.controller("interpreterEditCntrl", function($scope, $stateParams,$state, platformUtils, toaster){
		
	var inptrName = $stateParams.inptr;
	$scope.inptrDetails = {};
	$scope.inptrDetails.content = "/*Your Java Code goes here!*/";
	
	if(angular.isDefined(inptrName)){
		var options = {
				where : "#name# = ?",
				whereParams : [inptrName]
					};
			platformUtils.query("etInterpreters", options).then(function(response){
				if(response.data && response.data.length > 0){
					$scope.inptrDetails = response.data[0];
				}else{
					$state.go('app.admin.interpreters.inptr-edit', {inptr:null});
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
	$scope.isEtInterpreterBusy = false;
	$scope.save = function(){
		$scope.isEtInterpreterBusy = true;
		platformUtils.save("etInterpreters", $scope.inptrDetails, {returnRows:true}).then(function(response){
			if(response.data && response.data.length > 0){
				$scope.isEtInterpreterBusy = false;
				$state.go('app.admin.interpreters.inptr-edit', {inptr:response.data[0].name});
			}
		});
	};
	
	/*  
	 * var myCodeMirror = CodeMirror(document.body);
	 * var javaEditor = CodeMirror.fromTextArea(document.getElementById("java-code"), {
		    lineNumbers: true,
		    matchBrackets: true,
		    mode: "text/x-java"
		  });
		  var mac = CodeMirror.keyMap.default == CodeMirror.keyMap.macDefault;
		  CodeMirror.keyMap.default[(mac ? "Cmd" : "Ctrl") + "-Space"] = "autocomplete";*/
});