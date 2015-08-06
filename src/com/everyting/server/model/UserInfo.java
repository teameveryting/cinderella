package com.everyting.server.model;

import java.io.Serializable;

public class UserInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int userId = -1;
	private String userName = "";
	private String displyName = "";
	private String EMAIL_ADDRESS = "";
	private String AVATAR_URL = "";
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public String getDisplyName() {
		return displyName;
	}
	public String getEMAIL_ADDRESS() {
		return EMAIL_ADDRESS;
	}
	public String getAVATAR_URL() {
		return AVATAR_URL;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setDisplyName(String displyName) {
		this.displyName = displyName;
	}
	public void setEMAIL_ADDRESS(String eMAIL_ADDRESS) {
		EMAIL_ADDRESS = eMAIL_ADDRESS;
	}
	public void setAVATAR_URL(String aVATAR_URL) {
		AVATAR_URL = aVATAR_URL;
	}
	
	

}
