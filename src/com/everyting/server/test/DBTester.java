package com.everyting.server.test;

import java.util.Iterator;
import java.util.List;

import com.everyting.server.DBExecutor;
import com.everyting.server.exception.ETException;
import com.everyting.server.model.ETModel;

public class DBTester {
	
	public static void main(String[] args) {
		try {
			main();
		} catch (ETException e) {
			System.out.println(e.getErrorType());
			System.out.println(e.getMessage());
		}
	}
	public static void main(){
		ETModel batchModel = new ETModel();
		batchModel.set("dataSource", "ET_DUMMY");
		
		ETModel batch1 = new ETModel();
		batch1.set("NAME", "venkat");
		ETModel batch2 = new ETModel();
		batch2.set("NAME", "tom");
		ETModel batch3 = new ETModel();
		batch3.set("NAME", "bob");
		
		
		batchModel.set("valueMapList", new Object[]{batch1, batch2, batch3});
		
		List<ETModel> batchResponse = DBExecutor.batchExecuteUpdate("batchInsert", batchModel);
		
		for(int i=0; i< batchResponse.size(); i++){
			ETModel batchdata = batchResponse.get(i);
			Iterator<String> iterator = batchdata.getKeyIterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				System.out.print(batchdata.get(key) +"\t");
			}
			System.out.println();
		}
		//String rawSQL = "INSERT INTO ET_DUMMY(NAME) VALUES(?)";		
				/*ETModel responseModel = serviceExecutor.executeUpdate("DELETE", updateModel);
		//ETModel responseModel = serviceExecutor.executeUpdate(rawSQL,"ET_DUMMY",  new Object[]{"TOM"});
		Iterator<String> iterator = responseModel.getKeyIterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			System.out.println(key +":" + responseModel.get(key));
		}*/
	}
}
