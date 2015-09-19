package com.everyting.server.test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.everyting.server.DBExecutor;
import com.everyting.server.ServiceManager;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.FileIOManager;

public class ETTemplatesSave {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ETModel requestData = new ETModel();
		
		ETModel formData = (ETModel) requestData.get("formData");
		ETModel fileMapping = (ETModel) requestData.get("fileMapping");
		Map<String, ETModel> filesData = (Map<String, ETModel>) requestData.get("files");
		
		String thumbnailImageFileName = (String) fileMapping.get("thumbNail");
		int thumbnailFileId = ServiceManager.uploadETFileStream(filesData.get(thumbnailImageFileName));
		int templateId = DBExecutor.rawExecuteUpdate("INSERT INTO ET_TEMPLATES(TEMPLATE_NAME, THUMBNAIL_ID, THEME, VERSION) VALUES(?,?,?,?)", 
				 new Object[]{ formData.get("templateName"), thumbnailFileId, formData.get("theme"),formData.get("version")});
		String projectZipFileName = (String) fileMapping.get("project");
		ETModel projectData = filesData.get(projectZipFileName);
		List<ETModel> extractedProjectModel = FileIOManager.extractZipStream( (InputStream) projectData.get("fileStream"));
		for(ETModel entry : extractedProjectModel ){
			entry.set("templateId", templateId);
			entry.set("parentUid", ((ETModel)entry.get("parentFolder")).get("uid"));
			entry.remove("parentFolder");
		/*System.out.println(entry.get("uid") +"\t" +entry.get("name") +"\t" + entry.get("path") +"\t" + entry.get("isFolder") + "\t" +
					(entry.get("fileContent") != null ? ((String)entry.get("fileContents")).substring(0, 15) :"") +"\t"+
					((ETModel)entry.get("parentFolder")).get("uid"));*/
		}
		ETModel projectBatch = new ETModel();
		projectBatch.set("dataSource", "etTemplateStructure");
		projectBatch.set("valueMapList", extractedProjectModel);
		int[] affectedRows = DBExecutor.batchExecuteUpdate("insert", projectBatch);
		System.out.println(affectedRows);
	}
}
