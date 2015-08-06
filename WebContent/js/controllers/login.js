'use strict';
angular.module('cynderellaLogin', []).controller('loginCtrl', function($scope, $http,$window) {
	  
	  $scope.email = "";
	  $scope.password = "";
	  $scope.status = "";
	  $scope.isBusy = false;
	  
	  $scope.login = function(){
		  
		if(!$scope.email || $scope.email === ""){
			 $scope.status = "Please provide valid email address";
			return;
		}else{
			$scope.status = undefined;
		}
		
		if(!$scope.password || $scope.password === "" || !($scope.password.length > 5)){
			$scope.status = "Please provide valid password";
			return;
		}else{
			$scope.status = undefined;
		}
		
		if(!$scope.isBusy){
			$scope.isBusy = true;
			var request = {email:$scope.email,password:$scope.password};
			$http.post("/EappBuilder/login", request).then(function(result){
				var response = result.data;
				if(response && response.status === 'success'){
						$scope.isBusy = false;
						$scope.status = response.data.details;
						$window.location.href = response.data.url;
				}else if(response && response.status === 'error'){
					 $scope.isBusy = false;
					 $scope.status = response.data.details;
				}
			});
		}
	  };
  })
  .config(function($httpProvider) {
     $httpProvider.interceptors.push(function($q) {
        return {
          responseError: function(rejection) {
                if(rejection.status == 0) {
                   alert("no connection");
                    return;
                }
                return $q.reject(rejection);
            }
        };
    });
});