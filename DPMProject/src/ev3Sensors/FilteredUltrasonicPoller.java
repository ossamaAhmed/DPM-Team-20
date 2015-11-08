package ev3Sensors;
/*
 * File: FilteredUltrasonicPoller.java
 * Written by: Rick Wu
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * FilteredUltrasonicPoller class is responsible for getting filtered readings from the ultrasonic sensor
 */
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;


public class FilteredUltrasonicPoller extends Thread {
	
	/* Instance Variables*/
	private SampleProvider usFilteredSource;
	// initialize an array of floats for fetching samples
	float[] usData;

	/** 
	 * Constructor
	 * This method takes care of initializing the FilteredUltrasonicPoller object 
	 * @param usSensor is the sensor mode of the ultrasonic sensor
	 * @param maxValue is the maximum value where the ultrasonic will disregard anything above it
	 * @param ReadingsToMedian is the number of samples taken
	 */
	public FilteredUltrasonicPoller(SensorModes usSensor, float maxValue, int ReadingsToMedian) {
		SampleProvider usReading = usSensor.getMode("Distance");
		// Filter which caps sensor values to n
		SampleProvider usCappedSource = new MaxValueFilter(usReading, maxValue);
		// Stack a filter which takes average readings
		SampleProvider usMedianSource = new MedianFilter(usCappedSource, ReadingsToMedian);
		// The final, filtered data from the us sensor is stored in usFilteredSource
		this.usFilteredSource = usMedianSource;
		// initialize an array of floats for fetching samples
		this.usData = new float[usFilteredSource.sampleSize()];
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