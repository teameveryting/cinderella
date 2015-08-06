package com.everyting.server.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;

public class SessionManager {
	
	public static void createUserSession(HttpServletRequest request, ETModel userObject){
		HttpSession session = request.getSession(false);
		if(session != null){
			session.invalidate();
		}
		session = request.getSession(true);
		session.setAttribute("userInfo", userObject);
		session.setMaxInactiveInterval(12 * 60 * 60);//One day active session
	}
	
	public static JSONObject getUserSession(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session != null){
			return (JSONObject) session.getAttribute("userInfo");
		}
		return null;
	}
	
	public static ETModel getUserInfo(HttpServletRequest request){
		 return (ETModel) request.getSession(false).getAttribute("userInfo");
	}

	public static boolean invalidateUserSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null){
			try{
				session.removeAttribute("userInfo");
				session.invalidate();
				return true;
			}catch(Exception e){
				throw new ETException("ET-SESSION-MANAGER", "Exception while session invalidate", e.getMessage());
			}
		}
		return false;
	}
	
}
