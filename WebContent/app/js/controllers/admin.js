'use strict';
angular.module('app')
  .controller('adminCntrl', function($scope, $state) {
	  
	  $scope.test = "venkat5656787";
	  
	  $scope.goState = function(state){
		  if(state === 'platform-pages'){
			  $state.go('app.admin.platform-pages');
		  }
	  };
	 
  });