var app = angular.module("app");
app.controller("ptPagesEditCntrl", function($scope, $stateParams,$state, platformUtils){
	
	var pageName = $stateParams.pn;
	$scope.pageDetails = {};
	$scope.pageDetails.html = "<div class=\"col-lg-12\" style=\"padding: 0px;  margin-bottom: 20px;\">\n"+
								"\t<div class=\"sup-header\" style=\"float: left; width: 100%; height: 68px;\">\n"+
								"\t\t<div style=\"margin-left: 50px;  float: left;\">\n"+
									"\t\t\t<blockquote class=\"text-muted favicon-anim\">\n"+
										"\t\t\t\t<h3 style=\"margin-top: 3px; margin-bottom: 2px;\">\n"+
											"\t\t\t\t\t<em class=\"icon-notebook\"></em> Heading\n"+
										"\t\t\t\t</h3>\n"+
										"\t\t\t\t<footer>Page description</footer>\n"+
									"\t\t\t</blockquote>\n"+
								"\t\t</div>\n"+
								"\t\t\t\t<!-- Your custom componets for super header here!-->\n"+
							"\t</div>\n"+
							"</div>\n\n"+
							"<div class=\"row page-view\">\n\t\t\t<!--Your HTML code here!-->\n\t\t\t{{test}}\n</div>";
	$scope.pageDetails.js = "var app = angular.module(\"app\");\n"+
		"app.controller(\"yourCntrl\", function($scope,platformUtils){\n\n"+
		"\t\t/*Your controller here!*/\n"+
		"\t\t$scope.test = \"Page Workig....\"\n\n"+
		"});";
	$scope.pageDetails.lazyLoad = "{\n[]}";

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
	CodeMirror.commands.autocomplete = function(cm) {
        cm.showHint({hint: CodeMirror.hint.anyword});
      };
	$scope.editorOptions = function(mode, editor){
		return{
			 enter: "newline-and-indent",
	         ctrl_enter: "reparse-buffer",
	         ctrl_z: "undo",
	         ctrl_y: "redo",
			 autoCloseBrackets: true,
			 autoCloseTags: true,
		     matchTags: true,
		     showTrailingSpace: true,
			 lineNumbers: true,
		     indentWithTabs: true,
		     highlightSelectionMatches: true,
		      theme:'eclipse',
		      lineWrapping : true,
		      htmlMode: true,
		      lint: true,
		      foldGutter: true,
		      gutters: ['CodeMirror-lint-markers','CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
		      mode: {name: mode, globalVars: true},
		      styleActiveLine: true,
			  matchBrackets: true,
			  tab: 'indentAuto',
			  extraKeys: {
			        'Ctrl-Q': function(cm) {
			             cm.foldCode(cm.getCursor());
			         },
			         'Ctrl-Space': 'autocomplete',
			         'Alt-F': "findPersistent"
			  },
			  onLoad : function(_cm){
			      $scope.setCodeMirrorMode = function(mode){
			        _cm.setOption("mode", mode.toLowerCase());
			      };
			  },
			  hintOptions: { keywords : ["k1", "k2"] }
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