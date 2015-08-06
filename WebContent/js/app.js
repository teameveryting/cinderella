'use strict';
angular.module('cynderella', 
['ngSanitize','oc.lazyLoad','ui.router',
 'ui.bootstrap','angular-loading-bar','ngAnimate','ui.codemirror', 'ngJsTree', 'dialogs.main','ngFileUpload', ])

.config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
	    cfpLoadingBarProvider.includeBar = true;
	    cfpLoadingBarProvider.includeSpinner = true;
	    cfpLoadingBarProvider.latencyThreshold = 500;
}])
 .controller('mainCntrl', function($scope,$http,$rootScope,$state,$stateParams,platformUtils,$window) {
  
	var themes = ["#F44336","#E91E63","#9C27B0","#673AB7","#673AB7","#311B92","#3F51B5","#2196F3","#03A9F4","#00BCD4","#009688","#4CAF50","#FF9800",
      "#FF5722","#DD2C00","#795548","#3E2723","#212121"];
	var getRandomTheme = function(){
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
	}
  })

 .directive('topMenuLink',function(){
	return {
	template:'<a class="dropdown-toggle" data-toggle="dropdown" href="#"> <i class="{{icon}} fa-fw"></i><i class="fa fa-caret-down"></i></a>',
    restrict: 'AE',
    scope: true, 
    replace: true,
    require:"^ngController",
    link:function(scope, element, attrs, parentCntrl){
    	   scope.icon = attrs.icon;
    	   element.css('color', parentCntrl.theme.aLinkColor );
    	   element.parent().bind('mouseenter', function() {
    		   element.css('color', parentCntrl.theme.aLinkHoverInColor );
            });
            element.parent().bind('mouseleave', function() {
            	element.css('color', parentCntrl.theme.aLinkColor );
		            });
		    	}
			};
	})
 .directive('superHeader',function(){
 var html = '<div class="col-lg-12" style="padding: 0px">'
 		+'	<div class="sup-header">'
 		+'		<div style="margin-left:50px">'
		+'			<blockquote class="text-muted favicon-anim">'
		+'				<h3 style=" margin-top: 3px;  margin-bottom: 2px;">'
		+'					<em class="{{icon}}"></em>'
		+'						{{header}}'
		+'				</h3>'
		+'				<footer>{{subHeader}}</footer>'
		+'			</blockquote>'
		+'		 </div>'
		+'		</div>'
		+'</div>';
	return {
	template:html,
    restrict: 'AE',
		    scope: true, 
		    replace: true,
		    link:function(scope, element, attrs){
		    	   scope.icon = attrs.icon;
		    	   scope.header = attrs.header;
		    	   scope.subHeader = attrs.subHeader;
		    	   scope.customHtml = attrs.customHtml;
		    	}
			};
	})
	

.factory('dbUtils', [ '$q','$http' ,'$timeout', function( $q, $http,  $timeout){
return{
	post:function(operation, data){
		return $http.post("/EappBuilder/api/"+operation, data);
		},
	query:function(dataSource,requestdata){
		var q = $q.defer();
		requestdata.dataSource = dataSource;
		var stringifiedRequest = JSON.stringify(request);
		 this.post("query", stringifiedRequest).then(function(response){
			 var queryResult = response.data;
			if(queryResult.status){
					progress.isBusy = false;
					q.resolve(queryResult);
				}
			});
			 return q.promise;
		},
	save:function(dataSource, data){
		var q = $q.defer();
		var requestData = {};
		requestData.dataSource = dataSource;
		requestData.data = data;
		this.post("save", requestData).then(function(response){
			 var queryResult = response.data;
			if(queryResult.status){
					q.resolve(queryResult);
				}
			});
			 return q.promise;
		}	
	};
 }])

.factory('platformUtils', [ 'dialogs','Upload','$http','$q', function(dialogs, Upload, $http, $q){
return {
	
	post:function(url, data){
		return $http.post(url, data);
		},
	handleErrors:function(errorDetails){
		dialogs.error('Error',errorDetails,{});
		},
	 uploadFormData:function(data, fileMapping, files){
		 return  Upload.upload({
	            url: '/EappBuilder/api/uploadFileForm',
		           fields: {
		                'data': JSON.stringify(data),
		                'fileMapping':JSON.stringify(fileMapping || {})
		            },
		            file: files
		        });
	 },
	 uploadFile:function(files){
		 return  Upload.upload({
	            url: '/EappBuilder/api/uploadFile',
		            file: files
		        });
	 		},
	 logOut:function(){
		 var q = $q.defer();
		  this.post("/EappBuilder/logout", null).then(function(response){
			 q.resolve(response.data); 
		  });
		 return q.promise;
	 }
	};
}]);





