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

	$scope.editorOptions = function(mode, editor){
		return{
			 enter: "newline-and-indent",
	         ctrl_enter: "reparse-buffer",
	         ctrl_z: "undo",
	         ctrl_y: "redo",
			 autoCloseBrackets: true,
		     matchTags: true,
		     showTrailingSpace: true,
			 lineNumbers: true,
		      indentWithTabs: true,
		      highlightSelectionMatches: true,
		      theme:'eclipse',
		      lineWrapping : true,
		      lint: true,
		      foldGutter: true,
		      gutters: ['CodeMirror-lint-markers','CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
		      mode: mode,
		      styleActiveLine: true,
			  matchBrackets: true,
			  tab: 'indentAuto',
			  keywords : ["requestData", "VendorManager"],
			  extraKeys: {
			       
			        'Ctrl-Q': function(cm) {
			             cm.foldCode(cm.getCursor());
			         },
			         'Ctrl-Space': 'autocomplete'
			  },
			  onLoad : function(_cm){
			      $scope.setCodeMirrorMode = function(mode){
			        _cm.setOption("mode", mode.toLowerCase());
			      };
			  },
			 
		};
	};
	$scope.isEtInterpreterBusy = false;
	$scope.save = function(){
		$scope.isEtInterpreterBusy = true;
		platformUtils.save("etInterpreters", $scope.inptrDetails, {returnRows:true}).then(function(response){
			if(response.data && response.data.length > 0){
				platformUtils.pop("success", "Success", "Changes saved successfuly");
				$scope.isEtInterpreterBusy = false;
				$state.go('app.admin.interpreters.inptr-edit', {inptr:response.data[0].name});
			}
		});
	};
});