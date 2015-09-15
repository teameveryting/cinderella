var $stateProviderRef = null;
var $urlRouterProviderRef = null;
var app = angular.module("app",['ui.bootstrap', 'ui.router', 'oc.lazyLoad', 'chieffancypants.loadingBar', 
                                'toaster','ngAnimate',   'ngSanitize',  'dialogs.main', 'LocalStorageModule', 
                                'ui.codemirror']);
app.config(function( $stateProvider,$httpProvider, $locationProvider, $urlRouterProvider, $ocLazyLoadProvider, localStorageServiceProvider, cfpLoadingBarProvider) {
	 	/*Loading Bar config*/
		cfpLoadingBarProvider.includeBar = true;
	    cfpLoadingBarProvider.includeSpinner = true;
	    cfpLoadingBarProvider.latencyThreshold = 500;
	    //cfpLoadingBarProvider.parentSelector = '.ui-view-wrapper';
	    /*Local Storage fixing*/
	    localStorageServiceProvider.setPrefix('cinderella');
	    /*XSRF token naming*/
	    $httpProvider.defaults.xsrfHeaderName = 'x-dt-csrf-header';
	    $httpProvider.defaults.xsrfCookieName = 'X-CSRF-TOKEN';	
	    /*Fixed states for platform to run*/
	    $stateProvider
	    .state('app',getStateDetails('/app',"@", 'app/partials/app.html','appCntrl', ['app/js/controllers/appCntrl.js'] ))
	    .state('app.admin',getStateDetails('/admin',"@app", 'app/partials/admin/admin.html','adminCntrl', ['app/js/controllers/admin.js'] ))
	    .state('app.admin.platfrom-pages', getStateDetails('/platfrom-pages',"@app", 'app/partials/admin/platform-pages.html','ptPagesCntrl', ['app/js/controllers/platform-pages.js'] ))
		.state('app.admin.platfrom-pages.pt-page-edit',getStateDetails('/edit/:pn',"@app", 'app/partials/admin/pt-page-edit.html','ptPagesEditCntrl',['app/js/controllers/pt-page-edit.js'] ))
		.state('app.admin.interpreters',getStateDetails('/interpreters',"@app", 'app/partials/admin/interpreters.html','interpreterCntrl',['app/js/controllers/interpreters.js'] ))
		.state('app.admin.interpreters.inptr-edit', getStateDetails('/edit/:inptr',"@app", 'app/partials/admin/interpreters-edit.html','interpreterEditCntrl', ['app/js/controllers/interpreters-edit.js'] ));
	    /*URL Router configs*/
	    $urlRouterProvider.deferIntercept();
	    $urlRouterProvider.otherwise('/app/admin');
	    $locationProvider.html5Mode({enabled: false});
	    $stateProviderRef = $stateProvider;
	    $urlRouterProviderRef = $urlRouterProvider;
});
app.run(['$q', '$rootScope', '$http', '$urlRouter','$location','$stateParams', 'platformUtils','$state', '$ocLazyLoad',
         function($q, $rootScope, $http, $urlRouter, $location,$stateParams, platformUtils,$state, $ocLazyLoad) {
   /*Store DS in browser cache using LocalStorageModule*/
    platformUtils.storeDS();
    /*Query the ET_PAGES for dynamic states, templates, controllers, lazy loaded files*/
    getETPages($q, platformUtils).then(function(response){
    	$rootScope.etPages = response.data;
    	var dynamicStates = getDynamicStates($state,platformUtils, response.data);
    	syncDynamicStates($stateProviderRef, $urlRouter, dynamicStates);
    });
  }
]);
var lazilyLoad = function(lazyFileArr){
	var obj = {};
	obj["loadCtrl"] = ['$ocLazyLoad', function($ocLazyLoad) {
    	return $ocLazyLoad.load(lazyFileArr);
    }];
	return obj;
};
var getStateDetails = function(url,parent,templateUrl,controller, resolveFiles){
	var obj = {};
	obj.url = url;
	obj.views = {};
	obj.views[parent] = {};
	obj.views[parent].templateUrl = templateUrl;
	obj.views[parent].controller = controller;
	obj.resolve = {};
	obj.resolve["resolve"] = ['$ocLazyLoad', function($ocLazyLoad) {
    	return $ocLazyLoad.load(resolveFiles);
    }];
	return obj;
};
var getETPages = function($q, platformUtils){
	var defer = $q.defer();
    platformUtils.query("etPages", {where: "#showInMenu# = ?",whereParams : ["Y"]}).then(function(response){
    	if(response.data && response.data.length > 0){
    		defer.resolve(response);
    	}else{
    		defer.resolve([]);
    	}
    });
	return defer.promise;
};
var getDynamicStates = function($state, platformUtils, dataArr){
	var states = [];
	if(!(dataArr != null && dataArr.length > 0)) return states;
	for(var i=0; i < dataArr.length; i++){
		var data = dataArr[i];
		var existingState = $state.get(data.state);
		if(existingState === null){
			var refState = {};
			refState.url = data.url;
			refState.views = {
	    	      "@app": {
    	    	  	templateUrl: "service/blob/download/" + data.htmlBlobId +"/"+ data.pageName +"_index.html?inline = false",
    	  	      	controller: data.controller,
    	      }
    	    };
			 states[i] = {};
			 states[i] = refState;
			 states[i].state = data.state;
			 var depends = [];
			 var controllerURL = "service/blob/download/" + data.jsBlobId +"/"+ data.pageName+ "t_js.js?inline=true";
			/* platformUtils.getDownloadBlobUrl(data.jsBlobId, data.pageName, true)*/
			 depends.push(controllerURL);
			 states[i].lazyLoad = depends;
		} 
	}
	return states;
};
var syncDynamicStates = function($stateProviderRef,$urlRouter, states){
	if(!(states != null && states.length > 0)){
		$urlRouter.sync();
		$urlRouter.listen();
		return;
	}
	for(var i=0; i< states.length; i++){
		var data = states[i];
		$stateProviderRef
		.state( data.state, {
			 	url: data.url,
			 	views: data.views,
			 	resolve: lazilyLoad(data.lazyLoad)
		});
	}
	$urlRouter.sync();
	$urlRouter.listen();
};