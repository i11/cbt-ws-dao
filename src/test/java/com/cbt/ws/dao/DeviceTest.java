package com.cbt.ws.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.cbt.ws.entity.Device;
import com.cbt.ws.exceptions.CbtDaoException;
import com.cbt.ws.jooq.enums.DeviceState;

public class DeviceTest {
	
	private final Logger mLogger = Logger.getLogger(DeviceTest.class);
	
	@Test
	public void testAddNewDevice() {
		DeviceDao dao = new DeviceDao();
		Device device1 = createNewDevice(dao, null);		
		Device device2 = new Device();
		device2.setDeviceUniqueId(device1.getDeviceUniqueId());
		device2.setUserId(device1.getUserId());
		device2.setDeviceOsId(device1.getDeviceOsId());
		
		// Try to add with the same id		
		try {
			createNewDevice(dao, device2);
			fail("Expected exception not thrown");
		} catch(Exception e) {
			mLogger.error(e);
		}
		
		//Clean up		
		deleteDevice(dao, device1);		
	}
	
	@Test
	public void testUpdateDevice() {
		DeviceDao dao = new DeviceDao();
		// Add new device		
		Device device = createNewDevice(dao, null);		
		device.setState(DeviceState.ONLINE);		
		try {
			dao.updateDevice(device);
		} catch (CbtDaoException e) {
			fail("Could not update device");
		}		
		Device updateDevice = dao.getDevice(device.getId());		
		assertEquals(device, updateDevice);		
		deleteDevice(dao, device);		
	}
	
	/**
	 * Create new device and verify
	 * @param dao
	 * @param device
	 * @return
	 */
	private Device createNewDevice(DeviceDao dao, Device device) {		
		if (null == device) {
			device = new Device();
			device.setUserId(1L);
			device.setDeviceTypeId(1L);
			device.setDeviceOsId(1L);
			String uniqueId = UUID.randomUUID().toString();
			device.setDeviceUniqueId(uniqueId);
		}		
		Long deviceId = dao.add(device);
		assertTrue("Failed to add new device:" + device, deviceId > 0);
		device.setId(deviceId);
		assertTrue("Failed to add new device:" + device, verify(dao, deviceId, device));
		return device;
	}
	
	private void deleteDevice(DeviceDao dao, Device device) {
		try {
			dao.deleteDevice(device);
		} catch (CbtDaoException e) {
			mLogger.error("Could not delete device", e);
			fail("Could not delete " + device);
		}
		// TODO: add verify that it was deleted
	}
	
	/**
	 * Fetch device object and compare to one specified
	 * 
	 * @param dao
	 * @param deviceId
	 * @param device2
	 * @return
	 */
	private boolean verify(DeviceDao dao, Long deviceId, Device device2) {
		Device fetchedDevice = dao.getDevice(deviceId);
		return fetchedDevice.equals(device2);		
	}
			
}