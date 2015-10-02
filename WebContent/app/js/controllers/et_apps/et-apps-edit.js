var app = angular.module("app");
app.controller("etAppsEditCntrl", function($stateParams,$state,platformUtils, $scope,$timeout,$rootScope, $window) {
	
	var appId = $stateParams.id;
	$scope.appName = $stateParams.name;
	if(!$scope.appName || !$scope.appName) $state.go('app.etApps');
	var project = [];
	var root = {
		        id: "1000",
		        parent: "#",
		        icon: "icon-folder",
		        text: $scope.appName,
		        state: {
		            opened: true
		        }
	    	};
	project.push(root);
	  var lastActiveTab = 0;
	  $scope.codeTabs = [];
	  $scope.selectedNodeId = null;
	  var ignoreChanges = false;
	  $scope.treeData = [];
	  $scope.treeConfig = {
	            core : {
	                multiple : false,
	                animation: true,
	                error : function(error) {
	                   // $log.error('treeCtrl: error from js tree - ' + angular.toJson(error));
	                },
	                check_callback : false,
	            },
	            plugins : ['themes','types','dnd']
	        };
	  $scope.applyModelChanges = function() {
			 return !ignoreChanges;
		 };
		 /*Tree events start here!*/
	 $scope.treeReady = function() {
	         $timeout(function() {
	        	 ignoreChanges = false;
	         });
	     };
		$scope.codeMirrorConfig = {
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
			     /* mode: {name: mode, globalVars: true},*/
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
	  $scope.activateTab = function(index){
		  if($scope.codeTabs && $scope.codeTabs.length >0 && $scope.codeTabs[index]){
			  $scope.codeTabs[lastActiveTab].active = false;
			  $scope.codeTabs[index].active = true;
			  lastActiveTab = index;
		  }
	  };
	  $scope.addTab = function(id, title, content){
		  var tab = $scope.codeTabs.length;
		  var isTabOped = false;
		  var openedTabIndex = -1;
		  if($scope.codeTabs){
			  
			  if( $scope.codeTabs[lastActiveTab]){
			  	$scope.codeTabs[lastActiveTab].active = false;
			  }
			 for(var i=0; i< $scope.codeTabs.length; i++){
				 var codeTab = $scope.codeTabs[i];
				 if(codeTab.id === id){
					 isTabOped = true;
					 openedTabIndex = i;
					 break;
				 }
			 }
			 if(!isTabOped){
				 var codeMirrorContent = {id:id, title : title, content: content, active:true};
				 $scope.codeTabs.push(codeMirrorContent);
				 lastActiveTab = tab;
				 platformUtils.registerOnDataStack("etAppsEdit", id, codeMirrorContent);
			 }else{
				 $scope.activateTab(openedTabIndex);
				 lastActiveTab = openedTabIndex;
			 }
			  
		  }
	  };
	  $scope.closeTab =function(event, index){
		  event.preventDefault();
	      event.stopPropagation();
	      if($scope.codeTabs && $scope.codeTabs.length >0){
	    	  var codeMirrorContent =  $scope.codeTabs[index];
	    	  var isContentChanged = platformUtils.isModelChanges("etAppsEdit", codeMirrorContent.id, codeMirrorContent);
	    	  if(isContentChanged){
	    		  platformUtils.alert("info", "Info", "Please save the changes before closing the tab!");
	    		  return;
	    	  }
	    	  /*Delete the model from DataStack*/
	    	  platformUtils.deleteDataStackModel("etAppsEdit", codeMirrorContent.id);
		      $scope.codeTabs.splice(index, 1);
	    	  lastActiveTab =  index-1;
		      if(lastActiveTab < 0){
		    	  lastActiveTab = 0;
		      }
		      if($scope.codeTabs[lastActiveTab]){
		    	  $scope.codeTabs[lastActiveTab].active = true;
		      }
	      }
	  };
		$scope.selectNode = function(nodeId){
			angular.element('#treeid').jstree().select_node(nodeId);
			$scope.selectedNodeId = nodeId;
		};
		 $scope.selectNodeCallBack = function(evt, data){
			 if(data){
				 $scope.selectedNodeId = data.selected[0];
			 }
		  };
  $scope.dblClickCallback = function(evt, data){
	    var node = $(evt.target).closest("li");
	    if(node[0]){
	    	var itemId = node[0].id;
	    	for(var i=0; i < $scope.treeData.length; i++){
	    		var data = $scope.treeData[i];
	    		if(data.id === itemId){
	    			if(data.content){
				    	 var startIndex = data.text.search(/\.[a-zA-z]+/i);
				         if(startIndex > -1){
				             var ext = data.text.substring(startIndex+1);
				             if(ext){
						    	 var mode = "xml";
				            	 if(ext.toLowerCase() === 'html'){
				            		 mode = "xml";
				            	 }
				            	 if(ext.toLowerCase() === 'js' ||  ext.toLowerCase() === 'min.js'){
				            		 mode = "javascript";
				            	 }
				            	 if(ext.toLowerCase() === 'css' || ext.toLowerCase() === 'min.css'){
				            		 mode = "css";
				            	 }
				            	 if(ext.toLowerCase() === 'png' || ext.toLowerCase() === 'jpg' || ext.toLowerCase() === 'jpeg' || ext.toLowerCase() === 'gif'){
				            		 	return;
				            	 }
				            	 $scope.addTab(data.id, data.text, data.content);
						    	 $scope.$apply();
				            	 $scope.setCodeMirrorMode(mode);
				             }
				          } 
				    	 break;
				    }
	    		}
	    	}
	    	
	    }
  };
	var options = {
			where : "#appId# = ?",
			whereParams : [appId],
			limit:1000
	};
	 platformUtils.query("etAppStructure", options).then(function(response){
		 if(response.data && response.data.length > 0){
			 for(var i=0; i < response.data.length; i++){
				 var data = response.data[i];
				 var item = {};
				 item.id = data.uid;
				 item.parent = (data.parentUid === "#" ? "1000" : data.parentUid);
				 item.icon = (data.isFolder ==="Y" ? "icon-folder" : "icon-doc");
				 item.text = data.name;
				 if(data.fileContent) item.content = data.fileContent;
				 project.push(item);
			 }	
			 	$scope.ignoreChanges = true;
			    $scope.treeData = project;
			    $scope.treeConfig.version++;
		 }else $state.go('app.etApps');
	 });
	 var getUnsavedData = function(){
		 var unsavedData = [];
		 for(var i=0; i < $scope.codeTabs.length; i++){
			 var codeMirrorContent =  $scope.codeTabs[i];
			 var isContentChanged = platformUtils.isModelChanges("etAppsEdit", codeMirrorContent.id, codeMirrorContent);
			 if(isContentChanged){
				 unsavedData.push(codeMirrorContent);
		   	  }
		 }
		 return unsavedData;
	 };
	 $scope.isEtAppsBusy = false;
	 $scope.save = function(){
		 var unsavedData = getUnsavedData();
   	  	if(unsavedData && unsavedData.length > 0){
   	  	 $scope.isEtAppsBusy = true;
   	  		var options = {beforeAPICall : "EtAppStructureBulkContentSave", skipAPI:true};
	   	  	platformUtils.save("etAppStructure", unsavedData,options).then(function(response){
				if(response.data && response.data.length > 0){
					 for(var i=0; i < response.data.length; i++){
						 var savedContent = response.data[i];
						 /*Update $rootscope.unSavedDataStack*/
						 platformUtils.updateModelChanges("etAppsEdit", savedContent.id, savedContent);
						 /*Update project array for UI Model*/
						 for(var j=0; j < project.length; j++){
							 var codeContent = project[j];
							 if(codeContent.id === savedContent.id){
								 project[j].content = savedContent.content;
								 break;
							 }
						 }
					 }
					 $scope.isEtAppsBusy = false;
					 platformUtils.pop("success", "Success", "Changes saved successfuly");
				}
			});
   	  	}else{
   	  		platformUtils.pop("info", "Info", "Nothing to save!");
   	  	}
	 };
	 
	 $scope.$on('$destroy', function() {
		 window.onbeforeunload=null;
		 platformUtils.deleteDataStackGroup("etAppsEdit");
       });
	 $scope.$on('$stateChangeStart', function (event){
		 var unsavedData = getUnsavedData();
	   	  	if(unsavedData && unsavedData.length > 0){
	   	  		event.preventDefault();
	   	  		 platformUtils.alert("info", "Info", "Please save the changes before closing the tab!");
	   	  	}
		});
	 window.onbeforeunload = function (e) {
		   var unsavedData = getUnsavedData();
	   	  	if(unsavedData && unsavedData.length > 0){
	   	  	return "Please save the changes before closing the window!"; 
	   	  	}
		};
		/* $rootScope.$on('$locationChangeStart', function() {
		 //alert("$locationChangeStart");
      });*/
});
