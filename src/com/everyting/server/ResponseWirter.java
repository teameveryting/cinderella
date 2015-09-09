package com.everyting.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;

public class ResponseWirter {
	
	private static ResponseWirter openedInstance;
	private PrintWriter printWriter;
	private ResponseWirter(HttpServletResponse response){
		if(printWriter == null){ 
			try {
				printWriter = response.getWriter();
			} catch (IOException e) {
				throw new ETException("IOException", "ResponseWirter throws IOException while ResponseWirter",
									e.getMessage());
			}
		}
	}
	public static ResponseWirter getInstance(HttpServletResponse response){
		if(openedInstance != null){
			return openedInstance;
		}
			return new ResponseWirter(response);
	}
	public void write(String data){
		printWriter.write(data);
	}
	@SuppressWarnings("unchecked")
	public void write(ETModel responseData ){
		Object object = responseData.get("data");
		JSONObject jsonData = new JSONObject();
		try {
			JSONObject objectIntrl = null;
			jsonData.put("status", "success");
			jsonData.put("data", objectIntrl);
		} catch (JSONException e) {
			throw new ETException("JSONException", "ResponseWriter throws JSONException while write" 
					, e.getMessage());
		}
		if(object instanceof ETModel){
			 jsonData = DataHandler.toJSONResponse((ETModel)responseData.get("data"));
		}else if(object instanceof List<?>){
			 jsonData = DataHandler.toJSONResponse((List<ETModel>)responseData.get("data"));
		}
		printWriter.write(jsonData.toString());
	}
	public void writeError(String type, String logInfo, String message){
		JSONObject jsonData = DataHandler.toJSONResponse(type, logInfo,  message);
		printWriter.write(jsonData.toString());
	}
	public void closeResources(){
		if(printWriter  != null){
			printWriter.flush();
			printWriter.close();
		}
	}
}
