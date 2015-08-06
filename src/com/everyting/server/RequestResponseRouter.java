package com.everyting.server;

import java.io.IOException;
import java.io.PrintWriter;

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
		if(requestURI.toLowerCase().contains("/login")){
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/login.jsp");
			requestDispatcher.forward(request, response);
		}else{
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
			requestDispatcher.forward(request, response);
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter printWriter = response.getWriter();
		String requestURI = request.getRequestURI();
		ETModel responseData = new ETModel();
		try{
			if (requestURI.contains("/api/")) {
				APIManager apiManager = new APIManager();
				String opeartion = getAPIOperation(requestURI);
				if (opeartion != null && opeartion.length() > 0) {
					if(opeartion.equalsIgnoreCase("query")){
						ETModel requestData = DataHandler.getRequestData(request);
					    responseData = apiManager.manageQuery(requestData);
					}
					else if(opeartion.equalsIgnoreCase("insert") ||
							opeartion.equalsIgnoreCase("update") ||
							opeartion.equalsIgnoreCase("delete")){
						ETModel requestData = DataHandler.getRequestData(request);
						responseData = apiManager.manageExecuteUpdate(opeartion,requestData);
					}
					else if(opeartion.equalsIgnoreCase("batchInsert") ||
							opeartion.equalsIgnoreCase("batchUpdate") ||
							opeartion.equalsIgnoreCase("batchDelete")){
						ETModel requestData = DataHandler.getRequestData(request);
						responseData = apiManager.manageBatchExecuteUpdate(opeartion,requestData);
					}
					else if(opeartion.equalsIgnoreCase("uploadFiles")){
						
					}
					else if(opeartion.equalsIgnoreCase("uploadFileForm")){
						
					}
					else if(opeartion.equalsIgnoreCase("downloadFile")){
						
					}
					else if(opeartion.equalsIgnoreCase("processOnServer")){
						
					}
				}
			}
			if(requestURI.contains("/login")){
				ETModel requestDataModel = DataHandler.getRequestData(request);
					ETModel	responseDataModel = LoginManager.login(request, requestDataModel);
					printWriter.write(DataHandler.toJSONResponse(responseDataModel).toString());
					return;
			}
			
			if(requestURI.contains("/logout")){
					ETModel	responseDataModel = LoginManager.logout(request);
					printWriter.write(DataHandler.toJSONResponse(responseDataModel).toString());
					return;
			}
			printWriter.write(DataHandler.toJSONResponse(responseData).toString());
			return;
		}catch(ETException ex){
				printWriter.write(DataHandler.toJSONResponse(ex.getTitle(), ex.getMessage()).toString());
		}catch(RuntimeException ex){
			printWriter.write(DataHandler.toJSONResponse("", ex.getMessage()).toString());
		}finally{
			printWriter.flush();
			printWriter.close();
		}	
	}
	private String getAPIOperation(String uri) {
		String subString = uri.substring(uri.indexOf("/api") + 5);
		return subString;
	}
}
