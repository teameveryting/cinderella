package com.everyting.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.everyting.server.api.APIManager;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;

public class RequestResponseRouter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		if(requestURI.toLowerCase().contains("service/files/download")){
				ServiceManager.downloadETFileStream(request, response);
				return;
		}else if(requestURI.toLowerCase().contains("service/blob/download")){
			ServiceManager.downloadBlobStream(request, response);
			return;
		}else if(requestURI.toLowerCase().contains("/login")){
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/login.jsp");
			requestDispatcher.forward(request, response);
		}else{
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
			requestDispatcher.forward(request, response);
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResponseWirter responseWirter = ResponseWirter.getInstance(response);
		String requestURI = request.getRequestURI();
		Object responseData = new ETModel();
		try{
			if (requestURI.contains("/api/")) {
				String opeartion = getAPIOperation(requestURI);
				if (opeartion != null && opeartion.length() > 0) {
					if(opeartion.equalsIgnoreCase("query")){
						ETModel requestData = DataHandler.getRequestData(request);
					    responseData = APIManager.manageQuery(requestData);
					}
					else if(opeartion.equalsIgnoreCase("insert") ||
							opeartion.equalsIgnoreCase("update") ||
							opeartion.equalsIgnoreCase("delete")){
						ETModel requestData = DataHandler.getRequestData(request);
						responseData = APIManager.manageExecuteUpdate(opeartion,requestData);
					}
					else if(opeartion.equalsIgnoreCase("batchInsert") ||
							opeartion.equalsIgnoreCase("batchUpdate") ||
							opeartion.equalsIgnoreCase("batchDelete")){
						ETModel requestData = DataHandler.getRequestData(request);
						responseData = APIManager.manageBatchExecuteUpdate(opeartion,requestData);
					}
					else if(opeartion.contains("files/")){
						String fileOpration = opeartion.substring(6);
						if(fileOpration.contains("upload/")){
							
						}if(fileOpration.equalsIgnoreCase("uploadFileForm")){
						responseData = APIManager.manageFileFormDataUpload(request);
						}
					}
					else if(opeartion.equalsIgnoreCase("processOnServer")){
						
					}
				}
			}
			if(requestURI.contains("/login")){
				ETModel requestDataModel = DataHandler.getRequestData(request);
					responseData = ServiceManager.login(request, requestDataModel);
			}
			if(requestURI.contains("/logout")){
					responseData = ServiceManager.logout(request);
			}
					responseWirter.write(responseData);
					return;
		}catch(ETException ex){
			responseWirter.writeError(ex.getErrorType(), ex.getLogInfo(), ex.getMessage());
			return;
		}catch(RuntimeException ex){
			responseWirter.writeError("Exception", "RequestResponseRouter throws RuntimeException while Post",  ex.getMessage());
			return;
		}catch(Exception ex){
			responseWirter.writeError("Exception", "RequestResponseRouter throws Exception while Post",  ex.getMessage());
			return;
			
		}finally{
			responseWirter.closeResources();
		}	
	}
	private String getAPIOperation(String uri) {
		String subString = uri.substring(uri.indexOf("/api") + 5);
		return subString;
	}
}
