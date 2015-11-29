package sensorController;
/*
 * File: FilteredUltrasonicPoller.java
 * Written by: Rick Wu
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * FilteredUltrasonicPoller class is responsible for getting filtered readings from the ultrasonic sensor
 */


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;


/**
 * FilteredUltrasonicPoller class is responsible for getting filtered readings from the ultrasonic sensor
 *
 */
public class FilteredUltrasonicPoller extends Thread {
	
	//Resources
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private SampleProvider usFilteredSource;
	float[] usData;
	
	// Variables
	
	private static final float maxUltrasonicReading = 2.00f; // Max value for us clipping filter
	private static final int usReadingsToMedian = 5;

	public FilteredUltrasonicPoller() {
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		
		
		SampleProvider usReading = usSensor.getMode("Distance");
		// Filter which caps sensor values to n
		SampleProvider usCappedSource = new MaxValueFilter(usReading, maxUltrasonicReading);
		// Stack a filter which takes average readings
		SampleProvider usMedianSource = new MedianFilter(usCappedSource, usReadingsToMedian);
		// The final, filtered data from the us sensor is stored in usFilteredSource
		this.usFilteredSource = usMedianSource;
		// initialize an array of floats for fetching samples
		this.usData = new float[usFilteredSource.sampleSize()];
		this.start();
	}
	/** 
	 * This method takes care of running the filtered color sensor to get filtered readings
	 */
	public void run() {
		while (true) {
			usFilteredSource.fetchSample(usData, 0); // acquire data
			try {
				Thread.sleep(25);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}
	/** 
	 * This method takes care of returning the reading at the ultrasonic sensor
	 * @return it returns the filtered reading taken by the ultrasonic sensor
	 */

	public float getDistance() {
		return this.usData[0];
	}

}