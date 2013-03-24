package com.cbt.ws.dao;

import static com.cbt.ws.jooq.tables.Testscript.TESTSCRIPT;
import static com.cbt.ws.jooq.tables.Testtarget.TESTTARGET;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.Executor;

import com.cbt.ws.annotations.TestFileStorePath;
import com.cbt.ws.entity.TestTarget;
import com.cbt.ws.jooq.tables.records.TesttargetRecord;
import com.cbt.ws.mysql.Db;
import com.cbt.ws.utils.Utils;

/**
 * Test target(aplication) DAO
 * 
 * @author SauliusALisauskas 2013-03-03 Initial version
 * 
 */
public class TestTargetDao {

	private String mFileStorePath;

	private final Logger mLogger = Logger.getLogger(TestTargetDao.class);

	@Inject
	public TestTargetDao(@TestFileStorePath String testFileStorePath) {
		mFileStorePath = testFileStorePath;
	}

	/**
	 * Create new dummy test target in database
	 * 
	 * @param userid
	 * @return
	 */
	private Long createNewTestPackageRecord(Long userid) {
		Executor sqexec = new Executor(Db.getConnection(), SQLDialect.MYSQL);
		TesttargetRecord result = sqexec.insertInto(TESTTARGET, TESTTARGET.USER_ID).values(userid)
				.returning(TESTTARGET.TESTTARGET_ID).fetchOne();
		return result.getTesttargetId();
	}

	/**
	 * Create appropriate folder structure for holding test target. e.g. /userid/testtargetid/
	 * 
	 * @param targetId
	 * @param userId
	 * @return - path pointing to reated file
	 */
	private String createTestTargetFolder(Long targetId, Long userId) {
		// create user folder if not existing
		String path = mFileStorePath + userId + "//tt-" + targetId;
		if (new File(path).mkdirs()) {
			mLogger.info("New folder created:" + path);
			return path;
		}
		return null;
	}

	/**
	 * Get all test targets
	 * 
	 * @return
	 */
	public TestTarget[] getAll() {
		List<TestTarget> applications = new ArrayList<TestTarget>();
		Executor sqexec = new Executor(Db.getConnection(), SQLDialect.MYSQL);
		Result<Record> result = sqexec.select().from(TESTTARGET).fetch();
		for (Record r : result) {
			TestTarget tp = new TestTarget();
			tp.setId(r.getValue(TESTTARGET.TESTTARGET_ID));
			tp.setFilePath(r.getValue(TESTTARGET.PATH));
			tp.setFileName(r.getValue(TESTSCRIPT.FILENAME));
			tp.setMetadata(r.getValue(TESTTARGET.METADATA));
			applications.add(tp);
			mLogger.debug("ID: " + tp.getId() + " path: " + tp.getFilePath() + " metadata: " + tp.getMetadata());
		}
		return applications.toArray(new TestTarget[applications.size()]);
	}

	/**
	 * Save test target
	 * 
	 * @param testTarget
	 * @param uploadedInputStream
	 * @throws IOException
	 */
	public void storeTestTarget(TestTarget testTarget, InputStream uploadedInputStream) throws IOException {
		// Create new test package record in db -> get it's id
		Long newTestPackageId = createNewTestPackageRecord(testTarget.getUserId());
		mLogger.debug("Generated new id for test package:" + newTestPackageId);

		// Create appropriate folder structure to store the file
		testTarget.setId(newTestPackageId);
		String testPackagePath = createTestTargetFolder(newTestPackageId, testTarget.getUserId());

		// Store the file
		// TODO: manage file names better
		String fileName = "app-" + testTarget.getId() + ".apk";
		String filePath = testPackagePath + "//" + fileName;
		Utils.writeToFile(uploadedInputStream, filePath);

		// Update path and other info
		testTarget.setFilePath(filePath);
		testTarget.setFileName(fileName);
		updateTestTarget(testTarget);
	}

	/**
	 * Update test target information
	 * 
	 * @param testTarget
	 */
	private void updateTestTarget(TestTarget testTarget) {
		Executor sqexec = new Executor(Db.getConnection(), SQLDialect.MYSQL);

		if (sqexec.update(TESTTARGET).set(TESTTARGET.PATH, testTarget.getFilePath()).set(TESTTARGET.FILENAME, testTarget.getFileName())
				.where(TESTTARGET.TESTTARGET_ID.eq(testTarget.getId())).execute() != 1) {
			mLogger.error("Failed to update package:" + testTarget);
		} else {
			mLogger.debug("Test package updated:" + testTarget);
		}
	}
}
