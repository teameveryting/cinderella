'use strict';
angular.module('app')
  .controller('myappsCntrl', function($scope, $sanitize,$timeout,$log, $http, dialogs) {

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
	                	console.log("error!");
	                    $log.error('treeCtrl: error from js tree - ' + angular.toJson(error));
	                },
	                check_callback : false,
	            },
	           
	            plugins : ['themes','types','dnd']
	        };
	    
	    $scope.codeMirrorConfig = {
			      lineNumbers: true,
			      indentWithTabs: true,
			      theme:'eclipse',
			      lineWrapping : true,
			      htmlMode: true,
			      styleActiveLine: true,
				  matchBrackets: true,
				  onLoad : function(_cm){
				      $scope.setCodeMirrorMode = function(mode){
				        _cm.setOption("mode", mode.toLowerCase());
				      };
				  }
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
				 $scope.codeTabs.push({id:id, title : title, content: content, active:true});
				 lastActiveTab = tab;
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
	  
	 /* $resource(URL).get().$promise.then(function(response) {
		    $scope.treeData = response;
		    // remaining ng-jstree initialization code
		});*/

	  /*Ng Js Tree integration*/	  
	  $scope.applyModelChanges = function() {
			 return !ignoreChanges;
		 };
	
	   $http.get('test.json').success(function(data) {
				    $scope.ignoreChanges = true;
				    $scope.treeData = data;
		            $scope.treeConfig.version++;
			});
			 
		 /*Tree events start here!*/
		 $scope.treeReady = function() {
	         $timeout(function() {
	        	 ignoreChanges = false;
	         });
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
						            	 $scope.addTab(data.id, data.text, data.content);
								    	 $scope.$apply();
								    	 var mode = "xml";
						            	 if(ext.toLowerCase() === 'html'){
						            		 mode = "xml";
						            	 }
						            	 if(ext.toLowerCase() === 'js' ||  ext.toLowerCase() === 'min.js'){
						            		 mode = "javascript";
						            	 }
						            	 if(ext.toLowerCase() === 'css' || ext.toLowerCase() === 'min.css'){
						            		 mode = "css";
						            		 console.log(ext);
						            	 }
						            	 $scope.setCodeMirrorMode(mode);
						             }
						          } 
						    	 break;
						    	}
			    		}
			    	}
			    	
			    }
		  };
		  
		  $scope.launchDialog = function(which){
			    var dlg = null;
			    switch(which){
			      // Error Dialog
			      case 'error':
			       dialogs.error('This is my error message');
			        break;
			      // Notify Dialog
			      case 'notify':
			       dialogs.notify('Something Happened!','Something happened that I need to tell you.');
			        break;
			      // Confirm Dialog
			      case 'confirm':
			        dialogs.confirm('Please Confirm','Is this awesome or what?');
			        dlg.result.then(function(btn){
			          $scope.confirmed = 'You thought this quite awesome!';
			        },function(btn){
			          $scope.confirmed = 'Shame on you for not thinking this is awesome!';
			        });
			        break;
			    }; // end switch
			  }; // end launch
		  
		  
	  
  });
