<%@page import="com.everyting.server.model.ETModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <% 
 if (session == null || session.getAttribute("userInfo") == null || ((ETModel)session.getAttribute("userInfo")).get("userName") == null) {
	 response.sendRedirect(getServletContext().getContextPath() +"/login");
}%>
<!doctype html>
<html class="no-js" ng-app="cynderella">
  <head>
    <meta charset="utf-8">
    <title>Everyting</title>
     <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    
 	    <link rel="stylesheet" href="lib/css/bootstrap.min.css" />    
 	
        <link rel="stylesheet" href="lib/css/metisMenu.min.css">
    	<link rel="stylesheet" href="lib/css/loading-bar.min.css">
    	<link rel="stylesheet" href="lib/css/font-awesome.min.css" type="text/css">
    	<link rel="stylesheet" href="lib/css/simple-line-icons.css" type="text/css">
    	<link rel="stylesheet" href="lib/css/main.css">
    	<link rel="stylesheet" href="lib/code_mirror/codemirror.css">
    	<link rel="stylesheet" type="text/css" href="lib/code_mirror/eclipse.css"/>
       <link rel="stylesheet" href="lib/css/jsTree/style.min.css">
       <link rel="stylesheet" href="lib/css/semantic.min.css">
       
    	
		<link rel="stylesheet" href="lib/css/sb-admin-2.css">
		<link rel="stylesheet" href="css/indexStyle.css">
  
	    <script src="lib/js/jquery.min.js"></script> 
	    <script  src="lib/js/jsTree/jquery-ui.js"></script>
	    <script src="lib/js/bootstrap.min.js"></script>
	    
	    
	    <script src="lib/js/ng-file-upload/ng-file-upload-shim.min.js"></script>
	    <script src="lib/js/angular.min.js"></script>
	    <script src="lib/js/ng-file-upload/ng-file-upload.min.js"></script>
	    <script src="lib/js/angular-animate.min.js"></script>
	    <script src="lib/js/angular-sanitize.min.js"></script>
	    <script src="lib/js/angular-ui-router.min.js"></script>
	    <script src="lib/js/json3.min.js"></script>
	    <script src="lib/js/ocLazyLoad.min.js"></script>
	    <script src="lib/js/loading-bar.min.js"></script>
	    <script src="lib/js/ui-bootstrap-tpls-0.11.2.min.js"></script>
	    <script src="lib/js/metisMenu.min.js"></script>
	    <script src="lib/js/semantic.min.js"></script>
	    <script src="lib/js/jsTree/jstree.js"></script>
		<script src="lib/js/jsTree/ngJsTree.js"></script>
		<script src="lib/js/ngDialogs/dialogs.min.js"></script>
	    <script src="lib/code_mirror/codemirror.js"></script>
      	<script src="lib/code_mirror/xml.js"></script>
      	<script src="lib/code_mirror/css.js"></script>
      	<script src="lib/code_mirror/javascript.js"></script>
      	<script src="lib/code_mirror/matchbrackets.js"></script>
        <script src="lib/code_mirror/ui-codemirror.js"></script>
        <script src="js/app.js"></script>
	 	<script src="js/base.js"></script>
  </head>
    <body  ng-controller="mainCntrl">
    	<ng-include src="'lib/directives/header/header.html'"> </ng-include>
    	<ng-include src="'lib/directives/sidebar/sidebar.html'"> </ng-include>
    		<div  class="page-wrapper">
				<div ui-view class="ui-view-wrapper"></div>
        	</div>
    </body>
</html>