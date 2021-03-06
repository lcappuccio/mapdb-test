package org.systemexception.randomstuffinazip.pojo;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.systemexception.logger.api.Logger;
import org.systemexception.logger.impl.LoggerImpl;

import java.io.File;
import java.util.ArrayList;

/**
 * @author leo
 * @date 26/07/15 01:11
 */
public class DatabaseProvider {

	private final static Logger logger = LoggerImpl.getFor(DatabaseProvider.class);
	private final DB database;
	private HTreeMap<String, byte[]> databaseMap;

	public DatabaseProvider(String fileName) {
		database = DBMaker.fileDB(new File(fileName)).closeOnJvmShutdown().make();
		databaseMap = database.hashMap("matchCollection");
	}

	/**
	 * Add a record to the database
	 *
	 * @param recordName the record name/id
	 * @param fileData the byte[] with all file data
	 * @param file the original file, to be deleted
	 */
	public void addRecords(String recordName, byte[] fileData, File file) {
		databaseMap.put(recordName, fileData);
		logger.info("Added record " + recordName);
		database.commit();
		file.delete();
	}

	/**
	 * Fetch a file record from the database
	 *
	 * @param recordName is the record name/id
	 * @return the byte array of this content
	 */
	public byte[] getRecord(String recordName) {
		logger.info("Fetched record " + recordName);
		return databaseMap.get(recordName);
	}

	/**
	 * Get all recordIds stored in dabase
	 *
	 * @return lists all record ids
	 */
	public ArrayList<String> getAllStoredRecordIds() {
		ArrayList<String> records = new ArrayList<>();
		for (String recordId : databaseMap.keySet()) {
			records.add(recordId);
		}
		return records;
	}

	/**
	 * Compact database manually
	 */
	public void closeDatabase() {
		database.commit();
		database.compact();
		database.close();
		logger.info("Database compacted");
	}

	/**
	 * Count all items on database
	 *
	 * @return returns the total items count
	 */
	public int countItems() {
		logger.info("Database item count: " + databaseMap.size());
		return databaseMap.size();
	}
}
