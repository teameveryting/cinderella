<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <% if (session != null && session.getAttribute("userInfo") != null && ((JSONObject)session.getAttribute("userInfo")).get("userName") != null) {
	 response.sendRedirect(getServletContext().getContextPath() +"/");
}%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html class="no-js" ng-app="cynderellaLogin">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Everyting</title>

 <link rel="stylesheet" href="vendor/font-awesome/font-awesome.min.css" type="text/css">
<link rel="stylesheet" href="vendor/semantic/semantic.min.css">
<link rel="stylesheet" href="app/css/login.css">

<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/angular-js/angular.min.js"></script>
<script src="vendor/semantic/semantic.min.js"></script>
<script src="app/js/controllers/login.js"></script>
</head>
<body ng-controller="loginCtrl">
	<div id="outPopUp">
		<div class="ui teal image header" id="logo"
			style="display: block; margin-top: -15px;">
			<h2 style="margin-top: 8px" class="element">
				<i class="fa fa-magic"></i> Cinderella
			</h2>
		</div>
		<div class="ui two column middle aligned very relaxed stackable grid"
			style="padding: 5px 0px 15px 15px;">
			<div class="column">
				<form class="ui large form" name="loginForm" novalidate>
					<div class="ui form">
						<div class="field">
							<label>Email</label>
							<div class="ui left icon input">
								<input type="email" ng-model="email" name="email"
									placeholder="E-mail address" required> 
									<i class="user icon"></i>
							</div>
						</div>
						<div class="field">
							<label>Password</label>
							<div class="ui left icon input">
								<input type="password" placeholder="Password"
									ng-model="password" required> <i class="lock icon"></i>
							</div>
						</div>
						<div ng-click="login()" class= "ui fluid large teal submit button"
						 ng-class="isBusy?'loading':''"
							ng-disabled="loginForm.email.$dirty && loginForm.email.$invalid">
							Login</div>
					</div>
				</form>
			</div>
			<div class="ui vertical divider">Or</div>
			<div class="center aligned column">
				<div class="ui big teal labeled icon button" style="width: 220px;">
					<i class="signup icon"></i> Sign Up
				</div>
			</div>

			<div class="ui message message-box" style="margin: 40px 0px 0px 4px;">
				Forgot password?&nbsp; <a href="#"
					style="text-decoration: underline;">click here!</a>
				<div class="ui error message" ng-show="status">
					<ul class="list">{{status}}
					</ul>
				</div>
			</div>
		</div>
	</div>
</body>
</html>