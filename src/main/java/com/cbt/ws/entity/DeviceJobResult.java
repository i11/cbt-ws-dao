package com.cbt.ws.entity;

import com.cbt.ws.jooq.tables.records.DeviceJobResultRecord;
import com.cbt.ws.utils.Utils;

/**
 * Device job result entity
 * 
 * @author SauliusAlisauskas 2013-04-07 Initial version
 *
 */
public class DeviceJobResult extends CbtEntity {
	private String output;
	private Integer testsRun;
	private Integer testsFailed;
	private Integer testsErrors;
	private Long devicejobId;

	/**
	 * Constructor taking JooQ record as an argument
	 * 
	 * @param r
	 * @return
	 */
	public static DeviceJobResult fromJooqRecord(DeviceJobResultRecord r) {
		DeviceJobResult job = new DeviceJobResult();
		job.setId(r.getDevicejobresultId());
		job.setCreated(r.getCreated());
		job.setDevicejobId(r.getDevicejobid());
		job.setOutput(r.getOutput());
		job.setTestsErrors(r.getTestserrors());
		job.setTestsFailed(r.getTestsfailed());
		job.setTestsRun(r.getTestsrun());
		job.setState(State.valueOf(r.getState().toString()));
		return job;
	}

	public Long getDevicejobId() {
		return devicejobId;
	}

	public void setDevicejobId(Long devicejobId) {
		this.devicejobId = devicejobId;
	}

	private State state;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public enum JunitTestSummary {
		TESTSRUN, FAILURES, ERRORS
	}

	public enum State {
		PASSED, FAILED
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Integer getTestsRun() {
		return testsRun;
	}

	public void setTestsRun(Integer testsRun) {
		this.testsRun = testsRun;
	}

	public Integer getTestsFailed() {
		return testsFailed;
	}

	public void setTestsFailed(Integer testsFailed) {
		this.testsFailed = testsFailed;
	}

	public Integer getTestsErrors() {
		return testsErrors;
	}

	public void setTestsErrors(Integer testsErrors) {
		this.testsErrors = testsErrors;
	}

	@Override
	public String toString() {
		return Utils.toString("DeviceJobResult", "id", getId(), "deviceJobId", devicejobId, "testsRun", testsRun, "state",
				state, "failures" + testsFailed, "errors", testsErrors);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceJobResult) {
			DeviceJobResult other = (DeviceJobResult) obj;
			// TODO: improve comparison
			if (this.getId().equals(other.getId()) && this.getDevicejobId().equals(other.getDevicejobId())) {
				return true;
			}
		}
		return false;
	}
}
