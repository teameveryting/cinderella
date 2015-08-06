'use strict';
angular.module('cynderella')
  .controller('eLabCntrl', function($scope, DTOptionsBuilder, DTColumnBuilder,dbUtils,platformUtils) {
	  
	    $scope.dtOptions = DTOptionsBuilder.fromSource('https://l-lin.github.io/angular-datatables/data.json')
	        								.withOption('stateSave', true)
	        								.withPaginationType('full_numbers');
	    $scope.dtColumns = [
			        DTColumnBuilder.newColumn('id').withTitle('ID'),
			        DTColumnBuilder.newColumn('firstName').withTitle('First name'),
			        DTColumnBuilder.newColumn('lastName').withTitle('Last name').notVisible()
			        		];
	    $scope.newSource = 'https://l-lin.github.io/angular-datatables/data1.json';
	    $scope.dtInstance = {};
	    

	    $scope.reloadData = function() {
	        var resetPaging = true;
	        $scope.dtOptions.reloadData(callback, resetPaging);
	    };

	    function callback(json) {
	        console.log(json);
	    };
	    
	    $scope.changeData = function() {
	    	 $scope.dtOptions.sAjaxSource = $scope.newSource;
	    };
	    
	    $scope.saveWidget = function(){
	    	var query = dbUtils.query("ET_WIDGETS",{limit:20});
	    	query.then(function(response){
	    		
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
  });