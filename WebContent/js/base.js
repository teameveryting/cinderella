'use strict';
angular.module('cynderella')
.config(['$stateProvider','$urlRouterProvider','$ocLazyLoadProvider', 
			function ($stateProvider,$urlRouterProvider,$ocLazyLoadProvider) { 
$urlRouterProvider.otherwise('/dashboard');
$stateProvider
	 .state('dashboard', {
		url:'/dashboard',
		controller: 'dashboardCntrl',
     templateUrl: 'partials/dashboard.html',
     resolve: {
	        loadMyFiles:function($ocLazyLoad) {
	          return $ocLazyLoad.load({
	            name:'cynderella',
	            files:[
	            'js/controllers/dashboard.js',
	            ]
	          });
	        }
	      }
	  })
	 .state('myApps', {
		url:'/myApps',
		controller: 'myAppsCntrl',
     templateUrl: 'partials/myapps/myApps.html',
	     resolve: {
	        loadMyFiles:function($ocLazyLoad) {
	          return $ocLazyLoad.load({
	            name:'cynderella',
	            files:[
	            'js/controllers/myApps.js',
	            ]
	          });
	        }
	      }
	  })
	.state('eLab', {
		 url:'/eLab',
		 controller: 'eLabCntrl',
	     templateUrl: 'partials/everyting_lab/elab_home.html',
	     resolve: {
		        loadMyFiles:function($ocLazyLoad) {
		          return $ocLazyLoad.load({
		            name:'cynderella',
		            files:[	
		              'js/controllers/eLab.js',
		            ]
		          });
		        }
		     }
	     
		  })
	 .state('templates', {
		url:'/templates',
		controller: 'templatesCntrl',
		templateUrl: 'partials/everyting_lab/elab_templates.html',
		resolve: {
		     loadMyFiles:function($ocLazyLoad) {
		       return $ocLazyLoad.load({
		         name:'cynderella',
		         files:[
		         'js/controllers/templates.js',
			      		            ]
			      		          });
			      		        }
			      		     }
				        
					  })
		.state('tutorials', {
			url:'/tutorials',
			controller: 'tutorialsCntrl',
		 templateUrl: 'partials/tutorials.html',
		 resolve: {
			        loadMyFiles:function($ocLazyLoad) {
			          return $ocLazyLoad.load({
			            name:'cynderella',
			            files:[
			            'js/controllers/tutorials.js',
			            ]
			          });
			        }
			     }
		 
		})
		.state('eAppStore', {
		url:'/eAppStore',
		controller: 'eAppStoreCntrl',
		templateUrl: 'partials/eAppStore.html',
		resolve: {
		     loadMyFiles:function($ocLazyLoad) {
		       return $ocLazyLoad.load({
		         name:'cynderella',
		         files:[
		         'js/controllers/eAppStore.js',
			      		            ]
			      		          });
			      		        }
			      }
				        
			 });
}]);