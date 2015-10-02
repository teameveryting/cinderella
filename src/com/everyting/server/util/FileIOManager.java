package com.everyting.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;
public class FileIOManager {
	
	// upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 5;  // 5MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 120; // 120MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 150; // 150MB

    public static void writeBlobAsStream(ETModel blobData, HttpServletResponse response, boolean isInline){
    	if(blobData == null) return;
    	OutputStream outputStream = null;
    	try {	
				String fileName = (String) blobData.get("fileName");
				String contentType = (String) blobData.get("contentType");
				contentType = (contentType == null || !(contentType.length() >0 )) ? "application/octet-stream": contentType;
				String content = (String) blobData.get("content");
				byte[] fileStream = content.getBytes();
				response.setContentType(contentType);
				String downloadType = "attachment";
				if(isInline) downloadType = "inline";
				response.setHeader("Content-Disposition", downloadType + "; filename=\"" + fileName + "\"");
				outputStream = response.getOutputStream();
		        outputStream.write(fileStream);
		         return;
			}catch (IOException e) {
				throw new ETException("IOException", "FileIOManager throws IOException while writeFileStreamToResponse", e.getMessage());
			}finally{
				if(outputStream != null){
					 try {
						outputStream.flush();
						outputStream.close();
					} catch (IOException e) {}
				}
			}
    }
    public static void writeETFileAsStream(ETModel fileData, HttpServletResponse response, boolean isInline){
    	if(fileData == null) return;
    	OutputStream outputStream = null;
    	FileInputStream fileInputStream = null;
    	try{
    		String url = (String) fileData.get("filePath");
    		if(url == null || !(url.length() >0)) 
    			throw new ETException("InvalidUrl", "FileIOManager throws InvalidUrl:", "Missing URL to download file!");
    		String fileName = (String) fileData.get("fileName");
			String contentType = (String) fileData.get("contentType");
				   contentType = (contentType == null || !(contentType.length() >0 )) ? "application/octet-stream": contentType;
    		response.setContentType(contentType);
			String downloadType = "attachment";
			if(isInline) downloadType = "inline";
			response.setHeader("Content-Disposition", downloadType + "; filename=\"" + fileName + "\"");
			outputStream = response.getOutputStream();
			fileInputStream = new FileInputStream(url);
			IOUtils.copy(fileInputStream, outputStream);
			return;
    	}catch (IOException e) {
			throw new ETException("IOException", "FileIOManager throws IOException while writeETFileAsStream", e.getMessage());
		}finally{
				try {
					if(fileInputStream != null) fileInputStream.close();
						if(outputStream != null){
								outputStream.flush();
								outputStream.close();
							}
				}catch (IOException e) {}
		}
    }
	public static String writeStreamToDisk(String fileName, InputStream fileStream){
		String fileStoragePath = new DBPropsManager().getFileStoragePath();
		String filePath = fileStoragePath;
		String ext = "txt";
		try {
			if(fileName.contains(".")){
				ext = fileName.substring(fileName.indexOf("."));
			}
			String randomFileName = RandomStringUtils.randomAlphanumeric(8);
			filePath += randomFileName + ext;
			File file= new File(filePath);
			FileUtils.copyInputStreamToFile(fileStream, file);
			return file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			throw new ETException("InvalidFileStoragePath", "FileIoManager throws FileNotFoundException while writeStreamToDisk", 
					"No path with " + fileStoragePath + " found on this system, please update db.properties or contact administrator");
		} catch (IOException e) {
			throw new ETException("IOException", "FileIoManager throws IOException while writeStreamToDisk", 
					e.getMessage());
		}finally{
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {}
			}
		}
	}
    /*Response:List (fileName, fileStream)*/
    public static List<ETModel> readUploadedFiles(HttpServletRequest request){
    	List<ETModel> fileStreamList = new ArrayList<>();
    	try {
        	DiskFileItemFactory factory = new DiskFileItemFactory();
        	factory.setSizeThreshold(MEMORY_THRESHOLD);
    		ServletFileUpload upload = new ServletFileUpload(factory);
    		upload.setFileSizeMax(MAX_FILE_SIZE);
    		upload.setSizeMax(MAX_REQUEST_SIZE);
 			List<FileItem> formItems = upload.parseRequest(request);
 			Iterator<FileItem> iter = formItems.iterator();
 			while (iter.hasNext()) {//Get data files here!
 				FileItem item = (FileItem) iter.next();
 			   if(!item.isFormField()){
 				   ETModel fileStreamData = new ETModel();
 				   fileStreamData.set("fileName", item.getName());
 				   fileStreamData.set("fileStream", item.getInputStream());
 				  fileStreamData.set("fileSize", item.getSize());
 				   fileStreamList.add(fileStreamData);
	    	   }
 			}
    	}catch (FileUploadException e) {
				throw new ETException("FileUploadException", "FileIOManager throws FileUploadException while readUploadedFiles", e.getMessage());
		} catch (IOException e) {
			throw new ETException("IOException", "FileIOManager throws IOException while readUploadedFiles", e.getMessage());
		} 
    	return fileStreamList;
    }
   /*Request Data: data:stringifiedJsonForm, fileMapping:stringifiedJson, file:Array of Files*/
	public static ETModel extractFileFormData(HttpServletRequest request){
		ETModel etModel = new ETModel();
		Map<String, ETModel> uplodedFileMap = new HashMap<String, ETModel>();
		// checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
        	  throw new RuntimeException("Error: Form must has enctype=multipart/form-data.");
        }
        try {
        	DiskFileItemFactory factory = new DiskFileItemFactory();
        	factory.setSizeThreshold(MEMORY_THRESHOLD);
    		ServletFileUpload upload = new ServletFileUpload(factory);
    		upload.setFileSizeMax(MAX_FILE_SIZE);
    		upload.setSizeMax(MAX_REQUEST_SIZE);
 			List<FileItem> formItems = upload.parseRequest(request);
 			Iterator<FileItem> iter = formItems.iterator();
 			while (iter.hasNext()) {//Get form data files here!
 				FileItem item = (FileItem) iter.next();
 			   if(item.isFormField()){
	    		    InputStream inputStream = item.getInputStream();
	            	String stringfiedJson = read(inputStream);
	            	JSONObject requestJson = new JSONObject(stringfiedJson);
	            	ETModel etModelData = DataHandler.toETModel(requestJson);
	                if (item.getFieldName().equals("data")) {
	                	etModel.set("data", etModelData);
	                }else if(item.getFieldName().equals("fileMapping")){
	                	etModel.set("fileMapping", requestJson);
	                }
	    	   }else{//Get uploaded files here!
	    		   ETModel fileMap = new ETModel();
	    		   fileMap.set( "fileName", item.getName());
	    		   fileMap.set("fileStream", item.getInputStream());
	    		   fileMap.set("fileSize", item.getSize());
	    		   uplodedFileMap.put(item.getName(), fileMap);
	            }
 			  etModel.set("files", uplodedFileMap);
 			}
		}catch (FileUploadException e) {
			 throw new RuntimeException("FileIOManager throws FileUploadException2:" + e.getCause());
		} catch (IOException e) {
			throw new RuntimeException("FileIOManager throws IOException:" + e.getMessage());
		} catch (JSONException e) {
			throw new RuntimeException("FileIOManager throws JSONException:" + e.getMessage());
		}
		return etModel;
	}
	private static String read(InputStream stream) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}finally {
				reader.close();
		}
		return sb.toString();
	}
	public static List<ETModel> extractZipStream(InputStream inputStream) {
		try {
	        BufferedInputStream bis = new BufferedInputStream(inputStream);
			ZipInputStream zipInputStream = new ZipInputStream(bis);
			List<ETModel> directory = new ArrayList<>();
			ZipEntry entry;
			String prevPath = null;
			try {
				while ((entry = zipInputStream.getNextEntry()) != null) {
					String entryName = entry.getName();
					String[] pathSplitter = entryName.split("/"); 
					for(int i=0; i< pathSplitter.length; i++){
						UUID uID = UUID.randomUUID();
						String name = pathSplitter[i];
						StringBuilder pathBuilder = new StringBuilder();
						String path = "";
						String parentPath = "";
						for(int j=0 ; j <= i; j++){
							if(j == i){
								parentPath = pathBuilder.toString();
							}
							String item = pathSplitter[j];
							if(pathBuilder.length() >0){
								pathBuilder.append("/" + item);
							}else{
								pathBuilder.append(item);
							}
						}
						path = pathBuilder.toString();
						ETModel me = new ETModel();
						boolean isCompleted = false;
						if(prevPath != null && prevPath.length() >0){
							  Pattern pattern = Pattern.compile("^(" +path +")");
						      Matcher matcher = pattern.matcher(prevPath);
						      if(matcher.find()){
						    	  continue;
						      }
						}
						for(ETModel completedEntry: directory){
							if(name.equals(completedEntry.get("name")) && path.equals(completedEntry.get("path"))){
								isCompleted = true;
								break;
							}
						}
						if(isCompleted){
							continue;
						}
						if(i > 0){
							for(ETModel directoryEntry: directory){
								if(directoryEntry.get("path").equals(parentPath)){
									me.set("parentFolder", directoryEntry);
									break;
								}
							}
						}else{
							ETModel parentFolder = new ETModel();
							parentFolder.set("uid", "#");
							me.set("parentFolder", parentFolder);
						}
						String isFolder = "Y";
						if(i == pathSplitter.length -1){
							isFolder = (entry.isDirectory()? "Y" : "N" );
						}
						me.set("path",path);
						me.set("uid", uID.toString());
						me.set("name", name);
						me.set("isFolder", isFolder);
						if("N".equals(isFolder)){
							StringBuilder stringBuilder = new StringBuilder();
							for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
								stringBuilder.append((char)c);
							}
							me.set("fileContent", stringBuilder.toString());
						}
						directory.add(me);
					}
					prevPath = entryName;
				}
				return directory;
			} finally {
				if(inputStream != null) inputStream.close();
				if(bis != null) bis.close();
				if(zipInputStream != null) zipInputStream.close();
			}
		} catch ( IOException e) {
			 throw new RuntimeException("FileIOManager throws FileUploadException:" + e.getMessage());
		}
	}
}