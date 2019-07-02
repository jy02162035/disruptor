package com.bfxy.disruptor.carsys.event;

/**
 * Event类：汽车信息
 * @author wupengshun
 *
 */
public class MyInParkingDataEvent {

	private String carLicense; // 车牌号

	public String getCarLicense() {
		return carLicense;
	}

	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}

}
