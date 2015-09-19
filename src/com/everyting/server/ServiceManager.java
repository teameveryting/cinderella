package com.everyting.server;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.session.SessionManager;
import com.everyting.server.util.DataHandler;
import com.everyting.server.util.ETUtils;
import com.everyting.server.util.FileIOManager;
import com.everyting.server.vendor.SQLConstructor;
import com.everyting.server.vendor.VendorManager;

public class ServiceManager {
	
	public static ETModel downloadETFileStream(HttpServletRequest request, HttpServletResponse response){
		ResponseWirter responseWirter = ResponseWirter.getInstance(response);
		try{
		 	ETModel requestData = new ETModel();
		    requestData.set("fileId", request.getParameter("fid"));
		    requestData.set("sessionId", request.getParameter("sid"));
		    
		    
		   // FileIOManager.writeFileStreamToResponse(responseData, response);
			}catch(ETException ex){
				responseWirter.writeError(ex.getErrorType(), ex.getLogInfo(), ex.getMessage());
			}catch(Exception ex){
				responseWirter.writeError("Exception", "RequestResponseRouter throws Exception while files/download", ex.getMessage());
			}finally{
				responseWirter.closeResources();
			}
		return null;
	}
	public static void downloadBlobStream(HttpServletRequest request, HttpServletResponse response){
		ResponseWirter responseWirter = null;
		try{
			String requestURI = request.getRequestURI();
			String fid = null;
			// String sid = request.getParameter("sid");
			boolean isInline = false;
			if(request.getParameter("inline") != null){
				isInline = Boolean.parseBoolean(request.getParameter("inline"));
			}
			if(requestURI.contains("blob/download/")){
			String subString = requestURI.substring((requestURI.indexOf("blob/download/") +14));
				if(subString.contains("/")){
					String[] strArr = subString.split("/");
					fid = strArr[0];
				}
			}
		  	List<ETModel>  queryResponse = DBExecutor.rawExecuteQuery("SELECT * FROM ET_BLOBS WHERE ID = ?", new Object[]{fid});
		    if(queryResponse != null && queryResponse.size() > 0){
		    	ETModel blob = (ETModel) queryResponse.get(0);
		    	FileIOManager.writeFileStreamToResponse(blob, response, isInline);
		    }else{
		    	throw new ETException("InvalidBlobFile", "ServiceManager throws InvalidBlobFile while downloadBlobStream", 
		    			"No blob content exists to download as file with id:" + fid);
		    }
			}catch(ETException ex){
				 responseWirter = ResponseWirter.getInstance(response);
				responseWirter.writeError(ex.getErrorType(), ex.getLogInfo(), ex.getMessage());
			}catch (RuntimeException ex) {
				 responseWirter = ResponseWirter.getInstance(response);
				responseWirter.writeError("Exception", "RequestResponseRouter throws RuntimeException while files/download", ex.getMessage());
			}catch(Exception ex){
				 responseWirter = ResponseWirter.getInstance(response);
				responseWirter.writeError("Exception", "RequestResponseRouter throws Exception while files/download", ex.getMessage());
			}finally{
				if(responseWirter != null)responseWirter.closeResources();
			}
	}
	public static int uploadETFileStream(ETModel fileData){
		    String fileName = (String) fileData.get("fileName");
		    if(fileName == null || !(fileName.trim().length() > 0)) 
		    	throw new ETException("InvalidFileName", "ServiceManager throws InvalidFileName while uploadETFileStream", "Missing file name!");
		    else fileName = fileName.trim();
		    InputStream fileStream = (InputStream) fileData.get("fileStream");
			int fileSize =  (fileData.get("fileSize") != null ? ETUtils.safeLongToInt((long)fileData.get("fileSize")) : -1) ;
 			String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
			String fileUrl = FileIOManager.writeStreamToDisk(fileName, fileStream);
			return DBExecutor.rawExecuteUpdate("INSERT INTO ET_FILES(FILE_NAME,CONTENT_TYPE,FILE_SIZE,FILE_PATH) VALUES(?,?,?,?)", 
					new Object[]{fileName,contentType,fileSize,fileUrl });
	}
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
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor();
		List<ETModel> queryResults= DBExecutor.rawExecuteQuery(sqlConstructor.getUserLoginSQL(), new Object[]{email});
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
	public static void uploadStream(InputStream inputStream){
		
	}
}
