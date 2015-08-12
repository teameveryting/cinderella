'use strict';
var app = angular.module("app")
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
	});