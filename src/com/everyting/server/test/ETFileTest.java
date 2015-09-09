package com.everyting.server.test;

import org.json.JSONException;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;

public class ETFileTest {
	public static void main(String[] args) {
		try{	
			main();
		 }catch(ETException exception){
			 System.out.println(exception.getErrorType());
			 System.out.println(exception.getLogInfo());
			 System.out.println(exception.getMessage());
		 }catch(RuntimeException exception){
			 System.out.println("RuntimeException:" + exception.getMessage());
		 } catch (JSONException e) {
			 System.out.println("JSONException:" + e.getMessage());
		}
	}
	public static void main() throws JSONException{
		//ETModel blobData = DataHandler.toETModel(new JSONObject(getBlobFileData()));
		ETModel responseData = new ETModel();//apiManager.blobFileUpload(blobData);
		System.out.println(responseData);
	}
	public static String getBlobFileData(){
		String requestData = "{"
				+ "fileName:\"testfile2.txt\","
				+ "contentType:\"text/plain\","
				+ "blobContent:\"This is test2 file from testing\""
				+ "}";
		return requestData;
	}
}