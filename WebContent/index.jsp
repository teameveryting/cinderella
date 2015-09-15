<%-- <%@page import="com.everyting.server.model.ETModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <% 
 if (session == null || session.getAttribute("userInfo") == null || ((ETModel)session.getAttribute("userInfo")).get("userName") == null) {
	 response.sendRedirect(getServletContext().getContextPath() +"/login");
}%> --%>
<!doctype html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <title>Everyting</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="teamEveryting">
    
  	<link rel="stylesheet" href="vendor/bootstrap/bootstrap.min.css">
    <link rel="stylesheet" href="vendor/metis-menu/metisMenu.min.css">
    <link rel="stylesheet" href="vendor/font-awesome/font-awesome.min.css" type="text/css">
    <link rel="stylesheet" href="vendor/loading-bar/loading-bar.css">
    <link rel="stylesheet" href="vendor/ng-toaster/toaster.css">
    <link rel="stylesheet" href="vendor/simple-line-icons/simple-line-icons.css" type="text/css">
    <link rel="stylesheet" href="vendor/code-mirror/codemirror.css">
    <link rel="stylesheet" href="vendor/code-mirror/eclipse.css">
    <link rel="stylesheet" href="vendor/code-mirror/eclipse.css">
    <link rel="stylesheet" href="vendor/code-mirror/lint/lint.css">
    <link rel="stylesheet" href="vendor/sb-admin2/sb-admin-2.css">
	<link rel="stylesheet" href="app/css/indexStyle.css">
	
	<script src="vendor/jquery/jquery.min.js"></script>
	 <script src="vendor/angular-js/angular.min.js"></script> 
	<script src="vendor/angular-js/angular-sanitize.min.js"></script>
	<script src="vendor/angular-js/angular-ui-router.min.js"></script>
    <script src="vendor/angular-js/angular-animate.min.js"></script>
	<script src="vendor/ng-storage/angular-local-storage.min.js"></script>
	<script src="vendor/ocLazyLoad/ocLazyLoad.min.js"></script>
	<script src="vendor/loading-bar/loading-bar.js"></script>
	<script src="vendor/metis-menu/metisMenu.min.js"></script>
	<script src="vendor/ui-bootstrap/ui-bootstrap-tpls-0.13.3.min.js"></script>
	<script src="vendor/ng-toaster/toaster.js"></script>
	<script src="vendor/ng-dialog/dialogs.min.js"></script>
	<script src="vendor/code-mirror/codemirror.js"></script>
	<script src="vendor/code-mirror/xml.js"></script>
	<script src="vendor/code-mirror/clike.js"></script>
	<script src="vendor/code-mirror/css.js"></script>
	<script src="vendor/code-mirror/javascript.js"></script>
	<script src="vendor/code-mirror/matchbrackets.js"></script>
	<!--CodeMirror Lint Add on-->
	<script src="vendor/code-mirror/lint/jshint.js"></script>
	<script src="vendor/code-mirror/lint/jsonlint.js"></script>
	<script src="vendor/code-mirror/lint/csslint.js"></script>
	<script src="vendor/code-mirror/lint/lint.js"></script>
	<script src="vendor/code-mirror/lint/javascript-lint.js"></script>
	<!-- <script src="vendor/code-mirror/lint/json-lint.js"></script> -->
	<script src="vendor/code-mirror/lint/css-lint.js"></script>
	
	
	
	
	
	<script src="vendor/code-mirror/ui-codemirror.js"></script>
	<script src="app/js/app.js"></script>
	<script src="app/js/services/app_services.js"></script>
	<script src="app/js/directives/app_directives.js"></script>
	</head>
 <body>
 	  <div ui-view ng-app="app"></div>
 	  <toaster-container toaster-options="{'time-out': 3000}"></toaster-container>
 </body>
</html>


