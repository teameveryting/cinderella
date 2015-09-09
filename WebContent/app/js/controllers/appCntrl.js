'use strict';
angular.module('app')
.controller("appCntrl", function( $scope,$http,$rootScope,$state,$stateParams,$location,
		$window, platformUtils, localStorageService) {
   $state.etPages = $rootScope.etPages;
   var getRandomTheme = function(){
		var themes = ["#F44336","#E91E63","#9C27B0","#673AB7","#673AB7","#311B92","#3F51B5","#2196F3","#03A9F4","#00BCD4","#009688","#4CAF50","#FF9800",
		              "#FF5722","#DD2C00","#795548","#3E2723","#212121"];
		return themes[Math.floor(Math.random() * themes.length)];
	};
  $scope.theme = {};
  $scope.theme.background= getRandomTheme();
  $scope.theme.aLinkHoverInColor = $scope.theme.background;
  $scope.theme.aLinkColor = "#fff";
  this.theme = $scope.theme;
 
  $scope.changeLinkColor = function(eleID, mouse){
	  if(eleID && mouse === 'hoverIn'){
  angular.element("#" + eleID).css({ color: $scope.theme.aLinkHoverInColor });
  }else if(eleID && mouse === 'hoverOut'){
  angular.element("#" + eleID).css({ color:  $scope.theme.aLinkColor });
		  }
	  };
	  	$rootScope.$state = $state;
		this.state = $state;
		
	$scope.logOut = function(){
		platformUtils.logOut().then(function(response){
			if(response.status === "success"){
				$window.location.href = response.data.url;
			}
		});
	};
});
