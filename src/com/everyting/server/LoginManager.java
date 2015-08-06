package com.everyting.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.EmailValidator;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.session.SessionManager;
import com.everyting.server.util.DataHandler;
import com.everyting.server.vendor.SQLConstructor;
import com.everyting.server.vendor.VendorManager;

public class LoginManager {
	
	@SuppressWarnings("deprecation")
	public static ETModel login(HttpServletRequest request, ETModel userInfo){
			ETModel loginResponse = new ETModel();
			String email = (String) userInfo.get("email");
			String rawPassword = (String) userInfo.get("password");
			/*Server side user credentials validation here!*/
			if(email == null || rawPassword == null){
				throw new ETException("This should not happen but fill all required fields");
			}
			if(!EmailValidator.getInstance().isValid(email)){
				throw new ETException("Something bad happen, please provide a valid email, not a spam");
			}
			ServiceExecutor serviceExecutor = new ServiceExecutor();
			SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
			List<ETModel> queryResults= serviceExecutor.executeQuery(sqlConstructor.getUserLoginSQL(), new Object[]{email});
			if(queryResults != null && queryResults.size() >0){
				ETModel dbUserInfo = (ETModel) queryResults.get(0);
				String dbHashedPassword = dbUserInfo.get("encryptPassword").toString();
				if(DataHandler.isPasswordMatches(dbHashedPassword, rawPassword)){
					/*Create user session here!*/
					SessionManager.createUserSession(request, dbUserInfo);
					/*Update user info in ET_USERS table*/
					/*Return a success status*/
					loginResponse.set("url", request.getServletContext().getContextPath() +"/index.html");
					loginResponse.set("details", "Please wait while you redirect!");
				}else{
					throw new ETException("Please verify the email and password combination!");
				}
			}else{
				throw new ETException("Invalid email address!");
			}
		return loginResponse;
		
	}

	public static ETModel logout(HttpServletRequest request) {
		ETModel responsetData = new ETModel();
		if(! SessionManager.invalidateUserSession(request)){
			responsetData.set("details", "Failed to logout.Please contact your system administrator");
		}else{
			responsetData.set("url", request.getServletContext().getContextPath() +"/login");
			responsetData.set("details", "Please wait while you redirect!");
		}
		return responsetData;
	}

}
