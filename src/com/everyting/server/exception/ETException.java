package com.everyting.server.exception;

import java.io.Serializable;

public class ETException extends RuntimeException implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String errorCode = "";
	private String title = "";
	public ETException(){
		super();
	}
	public ETException(String message){
		super(message);
	}
	public ETException(String errorCode, String message){
		this(message);
		this.errorCode = errorCode;
	}
	public ETException(String errorCode, String title, String message){
		this(message);
		this.title = title;
		this.errorCode = errorCode;
	}
	public String getErrorCode(){
		return errorCode;
	}
	
	public String getTitle(){
		return title;
	}

}
