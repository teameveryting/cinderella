package com.everyting.server.exception;

import java.io.Serializable;

public class ETException extends RuntimeException implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String errorType = "";
	private String logInfo = "";
	public ETException(){
		super();
	}
	public ETException(String message){
		super(message);
	}
	public ETException(String logInfo, String message){
		this(message);
		this.logInfo = logInfo;
	}
	public ETException(String errorType, String logInfo, String message){
		this(message);
		this.logInfo = logInfo;
		this.errorType = errorType;
	}
	public String getErrorType(){
		return errorType;
	}
	
	public String getLogInfo(){
		return logInfo;
	}

}
