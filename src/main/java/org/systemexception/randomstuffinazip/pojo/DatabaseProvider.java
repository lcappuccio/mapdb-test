package org.systemexception.randomstuffinazip.pojo;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.systemexception.logger.api.Logger;
import org.systemexception.logger.impl.LoggerImpl;

import java.io.File;

/**
 * @author leo
 * @date 26/07/15 01:11
 */
public class DatabaseProvider {

	private final static Logger logger = LoggerImpl.getFor(DatabaseProvider.class);
	private final DB database;
	private HTreeMap<String, File> databaseMap;

	public DatabaseProvider() {
		database = DBMaker.fileDB(new File("database.db")).closeOnJvmShutdown().make();
		databaseMap = database.hashMap("matchCollection");
	}

	public void addRecords(String recordName, File file) {
		logger.info("Added record " + recordName);
		databaseMap.put(recordName, file);
		database.commit();
		file.delete();
	}

	public File getRecord(String recordName) {
		logger.info("Fetched record " + recordName);
		return databaseMap.get(recordName);
	}
}
