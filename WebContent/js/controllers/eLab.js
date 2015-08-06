'use strict';
angular.module('cynderella')
  .controller('eLabCntrl', function($scope,dbUtils,platformUtils) {
	  
	    $scope.queryWidget = function(){
	    	dbUtils.query("ET_WIDGETS",{limit:20}).then(function(response){
	    		
	    		if(response.status === 'success'){
	    			console.log("----Query Data-----");
	    			console.log(response.data.details);
	    		}else if(response.status === 'error'){
	    			platformUtils.handleErrors(response.data.details);
	    		}
	    	}, null, function(progress){
	    		console.log(progress);
	    	});
	    };
	    
	    $scope.widgetInfo = {};
	    
	    $scope.saveWidget = function(){
	    	dbUtils.save("ET_WIDGETS", $scope.widgetInfo).then(function(response){
	    		if(response.status === 'success'){
	    			console.log("----Insert status-----");
	    			console.log(response.data.details);
	    		}else if(response.status === 'error'){
	    			platformUtils.handleErrors(response.data.details);
	    		}
	    	});
	    	
	    	
	    };
  });