<%-- <%@page import="com.everyting.server.model.ETModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <% 
 if (session == null || session.getAttribute("userInfo") == null || ((ETModel)session.getAttribute("userInfo")).get("userName") == null) {
	 response.sendRedirect(getServletContext().getContextPath() +"/login");
}%> --%>
<!doctype html>
<html class="no-js" ng-app="app">
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
    <link rel="stylesheet" href="vendor/loading-bar/loading-bar.min.css">
    <link rel="stylesheet" href="vendor/font-awesome/font-awesome.min.css" type="text/css">
    <link rel="stylesheet" href="vendor/simple-line-icons/simple-line-icons.css" type="text/css">
    <link rel="stylesheet" href="vendor/semantic/semantic.min.css">
    <link rel="stylesheet" href="vendor/sb-admin2/sb-admin-2.css">
	<link rel="stylesheet" href="app/css/indexStyle.css">
	
	<script src="vendor/jquery/jquery.min.js"></script>
	<script src="vendor/bootstrap/bootstrap.min.js"></script>
	<script src="vendor/angular-js/angular.min.js"></script>
	<script src="vendor/angular-js/angular-sanitize.min.js"></script>
	<script src="vendor/angular-js/angular-ui-router.min.js"></script>
	<script src="vendor/ocLazyLoad/ocLazyLoad.min.js"></script>
	<script src="vendor/loading-bar/loading-bar.min.js"></script>
	<script src="vendor/metis-menu/metisMenu.min.js"></script>
	<script src="app/js/app.js"></script>
	<script src="app/js/services/app_services.js"></script>
	<script src="app/js/directives/app_directives.js"></script>
	
</head>
<body>
	<body  ng-controller="appCntrl">
    	<ng-include src="'app/partials/header.html'"> </ng-include>
    	<ng-include src="'app/partials/sidebar.html'"> </ng-include>
    		<div  class="page-wrapper">
				<div ui-view="indexUiView" class="ui-view-wrapper"></div>
        	</div>
    </body>
</body>
</html>


