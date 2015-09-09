package com.everyting.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETFolderModel;
import com.everyting.server.model.ETModel;
public class FileIOManager {
	
	// upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 5;  // 5MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 120; // 120MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 150; // 150MB

    public static void writeFileStreamToResponse(ETModel blobData, HttpServletResponse response, boolean isInline){
	try {	
			if(blobData != null){
				String fileName = (String) blobData.get("fileName");
				String contentType = (String) blobData.get("contentType");
				contentType = (contentType == null || !(contentType.length() >0 )) ? "application/octet-stream": contentType;
				String content = (String) blobData.get("content");
				byte[] fileStream = content.getBytes();
				response.setContentType(contentType);
				String downloadType = "attachment";
				if(isInline) downloadType = "inline";
				response.setHeader("Content-Disposition", downloadType + "; filename=\"" + fileName + "\"");
		         OutputStream outputStream = response.getOutputStream();
		         outputStream.write(fileStream);
		         outputStream.flush();
		         outputStream.close();
		         return;
			}
		} catch (IOException e) {
			throw new ETException("IOException", "FileIOManager throws IOException while writeFileStreamToResponse", e.getMessage());
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
	public static ETModel getUploadedFileFormData(HttpServletRequest request){
		ETModel etModel = new ETModel();
		Map<String, InputStream> uplodedFileMap = new HashMap<>();
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
	                if (item.getFieldName().equals("data")) {
	                	etModel.set("data", requestJson);
	                }else if(item.getFieldName().equals("fileMapping")){
	                	etModel.set("fileMapping", requestJson);
	                }
	    	   }else{//Get uploaded files here!
	    		   uplodedFileMap.put(item.getName(), item.getInputStream());
	            }
 			  etModel.set("files", uplodedFileMap);
 			}
		}
        catch (FileUploadException e) {
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
		}  finally {
				reader.close();
		}
		return sb.toString();
	}
	
	public static List<ETFolderModel> readUploadedZip(InputStream inputStream, HttpServletRequest request) {
		try {
	        BufferedInputStream bis = new BufferedInputStream(inputStream);
			final ZipInputStream zipInputStream = new ZipInputStream(bis);
			List<ETFolderModel> directory = new ArrayList<>();
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
						ETFolderModel me = new ETFolderModel();
						boolean isCompleted = false;
						if(prevPath != null && prevPath.length() >0){
							  Pattern pattern = Pattern.compile("^(" +path +")");
						      Matcher matcher = pattern.matcher(prevPath);
						      if(matcher.find()){
						    	  continue;
						      }
						}
						for(ETFolderModel completedEntry: directory){
							if(name.equals(completedEntry.getName()) && path.equals(completedEntry.getPath())){
								isCompleted = true;
								break;
							}
						}
						if(isCompleted){
							continue;
						}
						if(i > 0){
							for(ETFolderModel directoryEntry: directory){
								if(directoryEntry.getPath().equals(parentPath)){
									me.setParentFolder(directoryEntry);
									break;
								}
							}
						}else{
							ETFolderModel parentFolder = new ETFolderModel();
							parentFolder.setUID("#");
							me.setParentFolder(parentFolder);
						}
						boolean isFolder = true;
						if(i == pathSplitter.length -1){
							isFolder = entry.isDirectory();
						}
						me.setPath(path);
						me.setUID(uID.toString());
						me.setName(name);
						me.setDirectory(isFolder);
						if(!isFolder){
							//StringWriter fileWriter = new StringWriter();
							//IOUtils.copy(zipInputStream, fileWriter, "UTF-8");
							me.setFileContent(zipInputStream);
						}
						directory.add(me);
					}
					prevPath = entryName;
				}
				return directory;
			} finally {
				//zipInputStream.close();
			}
		} catch ( IOException e) {
			 throw new RuntimeException("FileIOManager throws FileUploadException:" + e.getMessage());
		}
	}
}