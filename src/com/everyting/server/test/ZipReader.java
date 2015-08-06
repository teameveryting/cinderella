package com.everyting.server.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

import com.everyting.server.model.ETFolderModel;

public class ZipReader {

	public static void main(String[] args) throws IOException {
		InputStream inputStream = new FileInputStream("/home/venkatesh_vasam/ionic/new2.zip");
		List<ETFolderModel> projectStructure = readUploadedZip2(inputStream);
		System.out.println("Entry Count:" + projectStructure.size() );
		for(int i=0; i < projectStructure.size(); i++){
			ETFolderModel item = projectStructure.get(i);
			String fileContent =  "";
			if(item.getFileContent() != null){
				InputStream fileWritter = item.getFileContent();
				try{
					fileContent = fileWritter.toString();
				}finally{
					if(fileWritter != null){
						fileWritter.close();
					}
				}
			}
			System.out.println("Uid:" + item.getUID() +" Name:" + item.getName() +" Parent UID:" + item.getParentFolder().getUID() + " Type:" + (item.isDirectory() ?" Folder":" File") +""+ (fileContent.length() >0 ? " Size:" + fileContent.length():"") ); 
		}
	}	
	public static List<ETFolderModel> readUploadedZip2(InputStream inputStream) throws IOException {
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
						StringWriter fileWriter = new StringWriter();
						IOUtils.copy(zipInputStream, fileWriter, "UTF-8");
					//	me.setFileContent(fileWriter);
					}
					directory.add(me);
				}
				prevPath = entryName;
			}
		} finally {
			zipInputStream.close();
		}
		return directory;
	}
}