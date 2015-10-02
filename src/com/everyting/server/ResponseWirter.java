package com.everyting.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.exception.ETException;
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
	public void write(Object responseData ){
		JSONObject response = new JSONObject();
		try {
			response.put("status", "success");
			response.put("data", DataHandler.toJSONObject(responseData));
		} catch (JSONException e) {
			throw new ETException("JSONException", "DataHandler throws JSONException while toJSONResponse" 
					, e.getMessage());
		}
		printWriter.write(response.toString());
	}
	public void writeError(String errorType, String logInfo, String message){
		JSONObject jsonData = DataHandler.toJSONResponse(errorType, logInfo,  message);
		printWriter.write(jsonData.toString());
	}
	public void closeResources(){
		if(printWriter  != null){
			printWriter.flush();
			printWriter.close();
		}
	}
}
