var $stateProviderRef = null;
var platformUtilsRef = null;
var vendorBundleRef = null;
var app = angular.module("app",['ui.bootstrap', 'ui.router', 'oc.lazyLoad', 'chieffancypants.loadingBar', 
                                'toaster','ngAnimate',   'ngSanitize',  'dialogs.main', 'LocalStorageModule', 
                                ]);
app.config(function( $stateProvider,$httpProvider, $locationProvider, $urlRouterProvider, $ocLazyLoadProvider, localStorageServiceProvider, cfpLoadingBarProvider) {
	 	/*Loading Bar configuration*/
		cfpLoadingBarProvider.includeBar = true;
	    cfpLoadingBarProvider.includeSpinner = true;
	    cfpLoadingBarProvider.latencyThreshold = 500;
	    //cfpLoadingBarProvider.parentSelector = '.ui-view-wrapper';
	    /*Local Storage fixing*/
	    localStorageServiceProvider.setPrefix('cinderella');
	    /*XSRF token naming*/
	    $httpProvider.defaults.xsrfHeaderName = 'x-dt-csrf-header';
	    $httpProvider.defaults.xsrfCookieName = 'X-CSRF-TOKEN';	
	    $urlRouterProvider.deferIntercept();
	    $urlRouterProvider.otherwise('/app/admin');
	    $locationProvider.html5Mode({enabled: false});
	    $stateProviderRef = $stateProvider;
});
app.run(['$q', '$rootScope', '$http', '$urlRouter','$location','$stateParams', 'platformUtils','$state', '$ocLazyLoad','VENDOR_BUNDLE',
         function($q, $rootScope, $http, $urlRouter, $location,$stateParams, platformUtils,$state, $ocLazyLoad, VENDOR_BUNDLE) {
   /*Store DS in browser cache using LocalStorageModule*/
    platformUtils.storeDS();
    platformUtilsRef = platformUtils;
    vendorBundleRef = VENDOR_BUNDLE;
    $stateProviderRef
    .state('app',getStateDetails('/app',"@", 'app/partials/app.html','appCntrl', 'app/js/controllers/appCntrl.js' ))
    .state('app.admin',getStateDetails('/admin',"@app", 'app/partials/admin/admin.html','adminCntrl', 'app/js/controllers/admin.js' ))
    .state('app.admin.platfrom-pages', getStateDetails('/platfrom-pages',"@app", 'app/partials/admin/platform-pages.html','ptPagesCntrl', 'app/js/controllers/platform-pages.js' ))
	.state('app.admin.platfrom-pages.pt-page-edit',getStateDetails('/edit/:pn',"@app", 'app/partials/admin/pt-page-edit.html','ptPagesEditCntrl','app/js/controllers/pt-page-edit.js',["code_mirror"] ))
	.state('app.admin.interpreters',getStateDetails('/interpreters',"@app", 'app/partials/admin/interpreters.html','interpreterCntrl','app/js/controllers/interpreters.js'))
	.state('app.admin.interpreters.inptr-edit', getStateDetails('/edit/:inptr',"@app", 'app/partials/admin/interpreters-edit.html','interpreterEditCntrl', 'app/js/controllers/interpreters-edit.js',["code_mirror"] ))
	.state('app.admin.templates', getStateDetails('/tempaltes',"@app", 'app/partials/admin/templates.html','templateCntrl', 'app/js/controllers/templates.js' ))
    .state('app.admin.templates.tmp-edit', getStateDetails('/edit/:tmp',"@app", 'app/partials/admin/templates-edit.html','templateEditCntrl', 'app/js/controllers/template-edit.js' ));
    /*Query the ET_PAGES for dynamic states, templates, controllers, lazy loaded files*/
    getETPages($q).then(function(response){
    	$rootScope.etPages = response.data;
    	var dynamicStates = getDynamicStates($state, response.data);
    	syncDynamicStates($stateProviderRef, $urlRouter, dynamicStates);
    });
  }
])
.constant('VENDOR_BUNDLE', {
     code_mirror:{
    	 				js:["vendor/code-mirror/ui-codemirror.js",
    	 				    "vendor/code-mirror/xml.js","vendor/code-mirror/clike.js",
    	 				    "vendor/code-mirror/css.js","vendor/code-mirror/javascript.js","vendor/code-mirror/show-hint/show-hint.js",
    	 				    "vendor/code-mirror/show-hint/javascript-hint.js","vendor/code-mirror/show-hint/html-hint.js",
    	 				    "vendor/code-mirror/matchbrackets.js","vendor/code-mirror/edit/closetag.js","vendor/code-mirror/fold/foldcode.js",
    	 				    "vendor/code-mirror/fold/foldgutter.js","vendor/code-mirror/fold/brace-fold.js","vendor/code-mirror/fold/xml-fold.js",
    	 				    "vendor/code-mirror/lint/jshint.js","vendor/code-mirror/lint/jsonlint.js","vendor/code-mirror/lint/csslint.js",
    	 				    "vendor/code-mirror/lint/lint.js","vendor/code-mirror/lint/javascript-lint.js","vendor/code-mirror/lint/css-lint.js",
    	 				    "vendor/code-mirror/dialog/dialog.js","vendor/code-mirror/search/searchcursor.js",
    	 				    "vendor/code-mirror/search/search.js","vendor/code-mirror/scroll/annotatescrollbar.js","vendor/code-mirror/search/match-highlighter.js"
                        ],
                     css:["vendor/code-mirror/codemirror.css","vendor/code-mirror/eclipse.css",
                          "vendor/code-mirror/lint/lint.css","vendor/code-mirror/show-hint/show-hint.css","vendor/code-mirror/fold/foldgutter.css",
                          "vendor/code-mirror/dialog/dialog.css","vendor/code-mirror/search/matchesonscrollbar.css"
                          ]
     			}
  });
var getStateDetails = function(url,parent,templateUrl,controller, cntrlUrl, vendorBundles){
	var obj = {};
	var depends = {};
	obj.url = url;
	obj.views = {};
	obj.views[parent] = {};
	obj.views[parent].templateUrl = templateUrl;
	obj.views[parent].controller = controller;
	if(vendorBundles){
		depends.vendorBundles = vendorBundles;
	}
	depends.cntrl = cntrlUrl;
	obj.resolve = lazilyLoad(depends);
	return obj;
};
var getETPages = function($q){
	var defer = $q.defer();
	platformUtilsRef.query("etPages", {where: "#showInMenu# = ?",whereParams : ["Y"]}).then(function(response){
    	if(response.data && response.data.length > 0){
    		defer.resolve(response);
    	}else{
    		defer.resolve([]);
    	}
    });
	return defer.promise;
};
var getDynamicStates = function($state, dataArr){
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
			 var depends = {};
			 var requiredFiles = data.lazyLoad;
			 if(requiredFiles){
				 requiredFiles = JSON.parse(requiredFiles);
				 if(requiredFiles){
					 depends.js = requiredFiles.js;
					 depends.css = requiredFiles.css;
					 depends.vendorBundles = requiredFiles.vendorBundles;
				 }
			 }
			 var controllerURL = "service/blob/download/" + data.jsBlobId +"/"+ data.pageName+ "t_js.js?inline=true";
			/* platformUtils.getDownloadBlobUrl(data.jsBlobId, data.pageName, true)*/
			 depends.cntrl = controllerURL;
			 states[i].lazyLoad = depends;
		} 
	}
	return states;
};
var lazilyLoad = function(depends){
	var obj = {};
	var jsArr = [];
	var cssArr = [];
	var vendorBundles = depends.vendorBundles;
	if(vendorBundles && angular.isArray(vendorBundles) && vendorBundles.length > 0){
		for(var i=0; i < vendorBundles.length; i++ ){
			var vendorBundle = vendorBundles[i];
				if(vendorBundle){
					var vendorBundleJs = vendorBundleRef[vendorBundle].js;
					var vendorBundleCss =  vendorBundleRef[vendorBundle].css;
					if(vendorBundleJs && vendorBundleJs.length > 0) jsArr =  jsArr.concat(vendorBundleJs);
					if(vendorBundleCss && vendorBundleCss.length > 0) cssArr =  cssArr.concat(vendorBundleCss);
				}
			}
	}
	var dependsJs = depends.js;
	if(dependsJs && angular.isArray(dependsJs) && dependsJs.length > 0){
		jsArr =  jsArr.concat(dependsJs);
	}
	var dependsCss = depends.css;
	if(dependsCss && angular.isArray(dependsCss) && dependsCss.length > 0){
		cssArr =  cssArr.concat(dependsCss);
	}
	obj["loadJS"] = ['$ocLazyLoad', function($ocLazyLoad) {
		if(jsArr && jsArr.length > 0) {
			return $ocLazyLoad.load(jsArr);
		}
    }];
	obj["loadCtrl"] = ['$ocLazyLoad', function($ocLazyLoad) {
    	return $ocLazyLoad.load(depends.cntrl);
    }];
	/*Inject unique css links here!*/
	platformUtilsRef.injectCSS(cssArr);
	return obj;
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