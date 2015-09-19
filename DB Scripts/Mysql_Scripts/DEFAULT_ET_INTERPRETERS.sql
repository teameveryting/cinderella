DELETE  FROM ET_INTERPRETERS WHERE ID > 0;

INSERT INTO ET_INTERPRETERS(NAME, LANG, IS_ACTIVE,ICON, CONTENT) 
values('ETDSInfoResolve', 'Java', 'Y', 'icon-compass',
'import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;
import com.everyting.server.vendor.VendorManager;

requestData.set("skipAPI", true);
String dbVendor = VendorManager.getDBVendor();
String rawQueryETDS = "";
String rawQueryETDSAttrs = "";
if("mysql".equalsIgnoreCase(dbVendor)){
  rawQueryETDS = "SELECT * FROM ET_DS";
  rawQueryETDSAttrs = "SELECT * FROM ET_DS_ATTRS";
}
if(!(rawQueryETDS.length() > 0 && rawQueryETDSAttrs.length() > 0)) return;
List<ETModel> etDSResponseList = DBExecutor.rawExecuteQuery(rawQueryETDS, new Object[]{});
List<ETModel> etDSAttrsResponseList = DBExecutor.rawExecuteQuery(rawQueryETDSAttrs, new Object[]{});
List<ETModel> dsInfoList = new ArrayList();

/*Response Data--- [ {dsInfo:{}, attrsInfo:[{},{},...]},{dsInfo:{}, attrsInfo:[{},{},...]}, ....  ]*/
if(etDSResponseList != null && etDSResponseList.size() > 0){
  for(int i=0; i < etDSResponseList.size(); i++){
	ETModel dsModel = new ETModel();
	List<ETModel> attrsInfoList = new ArrayList();
	ETModel etDSResponse = etDSResponseList.get(i);
	String tableName = (String) etDSResponse.get("tableName");
	if(etDSAttrsResponseList != null && etDSAttrsResponseList.size() > 0){
	  for(int j=0; j < etDSAttrsResponseList.size(); j++){
		ETModel etDSAttrsResponse = etDSAttrsResponseList.get(j);
		String dsName = (String) etDSAttrsResponse.get("tableName");
		if(dsName.equals(tableName)){
		  attrsInfoList.add(etDSAttrsResponse);
		  etDSAttrsResponseList.remove(j);
		}
	  }
	}
	dsModel.set("dsInfo",etDSResponse );
	dsModel.set("attrsInfo", attrsInfoList);
	dsInfoList.add(dsModel);
  }
}
responseData.set("data", dsInfoList);');

INSERT INTO ET_INTERPRETERS(NAME, LANG, IS_ACTIVE,ICON, CONTENT) 
values('ETPlatformPageQuery', 'Java', 'Y', 'icon-eyeglass', 'import java.util.ArrayList;
import java.util.List;
import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;

List<ETModel> queryResultList = DBExecutor.executeQuery(requestData);
if(queryResultList == null || !(queryResultList.size() > 0)) return;
ETModel queryResult = queryResultList.get(0);
int htmlBlobId = (int)queryResult.get("htmlBlobId");
		int jsBlobId = (int) queryResult.get("jsBlobId");
		if(htmlBlobId > -1){
			List<ETModel> htmlList = DBExecutor.rawExecuteQuery("SELECT CONTENT FROM ET_BLOBS WHERE ID = ?", new Object[]{htmlBlobId});
			String html = (String) htmlList.get(0).get("content");
			queryResult.set("html", html);
		}
		if(jsBlobId > -1){
			List<ETModel> jsList =  DBExecutor.rawExecuteQuery("SELECT CONTENT FROM ET_BLOBS WHERE ID = ?", new Object[]{jsBlobId});
			String js = (String) jsList.get(0).get("content");
			queryResult.set("js", js);
		}
		responseData.set("data", queryResult);
');

INSERT INTO ET_INTERPRETERS(NAME, LANG, IS_ACTIVE,ICON, CONTENT) 
values('ETPlatformPageSave', 'Java', 'Y', 'icon-fire', 
'import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.everyting.server.DBExecutor;
import com.everyting.server.model.ETModel;
import com.everyting.server.vendor.VendorManager;

		/*For Insertion*/
		if(requestData.get("valueMap") != null){
			ETModel valueMapDetails = (ETModel) requestData.get("valueMap");
			String html = (String) valueMapDetails.get("html");
			String js = (String) valueMapDetails.get("js");
			String insertSQL = "INSERT INTO ET_BLOBS(FILE_NAME, CONTENT_TYPE, CONTENT) VALUES(?,?,?)";
			int htmlBlobId = DBExecutor.rawExecuteUpdate(insertSQL, new Object[]{(String)valueMapDetails.get("pageName") +"_index.html", "text/html", html});
			int jsBlobId =   DBExecutor.rawExecuteUpdate(insertSQL, new Object[]{(String)valueMapDetails.get("pageName") +"_js.js", "application/javascript", js});
				if(htmlBlobId > -1 && jsBlobId > -1){
					valueMapDetails.set("htmlBlobId", htmlBlobId);
					valueMapDetails.set("jsBlobId", jsBlobId);
					List<ETModel> responseList=  DBExecutor.executeUpdate("insert", requestData);
				    responseData.set("data", responseList);
				}
		}
		/*For Update*/
		else if(requestData.get("setMap") != null){
			ETModel setMapDetails = (ETModel) requestData.get("setMap");
			String html = (String) setMapDetails.get("html");
			String js = (String) setMapDetails.get("js");
			if(html != null){
				String updateSQL = "UPDATE ET_BLOBS SET CONTENT = ?  WHERE ID = ?";
				DBExecutor.rawExecuteUpdate(updateSQL, new Object[]{html, setMapDetails.get("htmlBlobId")});
			}
			if(js != null){
				String updateSQL = "UPDATE ET_BLOBS SET CONTENT = ? WHERE ID = ?";
				DBExecutor.rawExecuteUpdate(updateSQL, new Object[]{js, setMapDetails.get("jsBlobId")});
			}
			List<ETModel> updateList  = DBExecutor.executeUpdate("update", requestData);
		    responseData.set("data", updateList);
		}
');

INSERT INTO ET_INTERPRETERS(NAME, LANG, IS_ACTIVE,ICON, CONTENT) 
values('ETTemplatesSave', 'Java', 'Y', 'icon-music-tone-alt', 'import java.util.Map;
import com.everyting.server.ServiceManager;
import com.everyting.server.model.ETModel;
import com.everyting.server.DBExecutor;
import java.io.InputStream;
import java.util.List;
import com.everyting.server.model.ETModel;
import com.everyting.server.util.FileIOManager;

		ETModel formData = (ETModel) requestData.get("formData");
		ETModel fileMapping = (ETModel) requestData.get("fileMapping");
		Map filesData = (Map) requestData.get("files");
		String thumbnailImageFileName = (String) fileMapping.get("thumbNail");
		int thumbnailFileId = ServiceManager.uploadETFileStream(filesData.get(thumbnailImageFileName));
		int templateId = DBExecutor.rawExecuteUpdate("INSERT INTO ET_TEMPLATES(TEMPLATE_NAME, THUMBNAIL_ID, THEME, VERSION) 				VALUES(?,?,?,?)",new Object[]{ formData.get("templateName"), 
					thumbnailFileId,formData.get("theme"),formData.get("version")});
		String projectZipFileName = (String) fileMapping.get("project");
		ETModel projectData = filesData.get(projectZipFileName);
		List<ETModel> extractedProjectModel = FileIOManager.extractZipStream( (InputStream) projectData.get("fileStream"));
       for(ETModel entry : extractedProjectModel ){
			entry.set("templateId", templateId);
            entry.set("fileContent", entry.get("fileContent"));
			entry.set("parentUid", ((ETModel)entry.get("parentFolder")).get("uid"));
			entry.remove("parentFolder");
		}
		ETModel projectBatch = new ETModel();
		projectBatch.set("dataSource", "etTemplateStructure");
		projectBatch.set("valueMapList", extractedProjectModel);
		DBExecutor.batchExecuteUpdate("insert", projectBatch);
');
