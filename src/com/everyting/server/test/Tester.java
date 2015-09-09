package com.everyting.server.test;

import java.util.Iterator;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.DataHandler;

public class Tester {

	public static void main(String[] args) {
	try{	
		//getRequestModelTester();
		//toUpperSplitCaseTester();
		//toCamelCaseTester();
	}catch(ETException ex){
		System.out.println("ErrorCode:" + ex.getErrorType()) ;
		System.out.println("ErrorTitle:" + ex.getLogInfo() );
		System.out.println("Msg:" + ex.getMessage());
	}
	}
	
	public static void exceptionTester(){
		try{
			throw new ETException("1000", "Exception testing success");
		}catch(ETException e){
			System.out.println("ErrorCode:" + e.getErrorType());
			System.out.println("Message" + e.getMessage());
		}
	}
	
	public static void getRequestModelTester(){
		//String testData = "{\"string\":\"String data\", \"number\":1000, \"double:10.00}";
		ETModel etModel = new ETModel();//DataHandler.getRequestModel(testData);
		Iterator<String> iterator = etModel.getKeyIterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			System.out.println("Key:" + key +" Value:" + etModel.get(key));
		}
	}
	
	public static void toUpperSplitCaseTester(){
		String testString = "firstSencondThirdplusFour";
		System.out.println(DataHandler.toUpperSplitCase(testString));
	}
	public static void toCamelCaseTester(){
		String testString = "FIRST_SENCOND_THIRDPLUS_FOUR";
		System.out.println(DataHandler.toCamelCase(testString));
	}

	
}
