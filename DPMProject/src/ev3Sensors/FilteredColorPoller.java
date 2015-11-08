package ev3Sensors;
/*
 * File: FilteredColorPoller.java
 * Written by: Rick Wu
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * FilteredColorPoller class is responsible for getting filtered readings from the color sensor
 */
import lejos.hardware.*;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;

public class FilteredColorPoller extends Thread {
	
	/* Instance Variables*/
	// Static Resources
	private SampleProvider colorFilteredSource[] = new SampleProvider[2];
	float[][] colorData = new float[2][];
	private String[] colorMode = { "ColorID", "Red" };
	
	/** 
	 * Constructor
	 * This method takes care of initializing the FilteredColorPoller object 
	 * @param colorSensor is an array of the sensor modes of the color sensor
	 * @param ReadingsToMedian is an array that will have the readings stored in
	 */
	public FilteredColorPoller(SensorModes[] colorSensor, int[] ReadingsToMedian) {
		SampleProvider[] colorReading = new SampleProvider[2];
		SampleProvider[] colorMedianSource = new SampleProvider[2];
		for (int i = 0; i < 2; i++) {
			colorReading[i] = colorSensor[i].getMode(colorMode[i]);
			// Stack a filter which takes average readings
			colorMedianSource[i] = new MedianFilter(colorReading[i], ReadingsToMedian[i]);
			// The final, filtered data from the us sensor is stored in colorFilteredSource
			this.colorFilteredSource[i] = colorMedianSource[i];
			// initialize an array of floats for fetching samples
			this.colorData[i] = new float[colorFilteredSource[i].sampleSize()];
		}
	}
	/** 
	 * This method takes care of running the filtered color sensor to get filtered readings
	 */
	public void run() {
		while (true) {
			for (int i=0;i<2;i++){
				colorFilteredSource[i].fetchSample(colorData[i], 0);
			}
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
		}
	}

	// TODO
	// Legacy methods
	/** 
	 * This method takes care of getting the color readings of the color sensor
	 * @return it returns the color id read by the sensor
	 */
	public float getID() {
		return this.colorData[0][0];
	}
	/** 
	 * This method takes care of identifying is the color sensor detects a blue object
	 * @return it returns true if the color sensor detects a blue object and false otherwise
	 */
	public boolean blueObject() {
		if (this.colorData[0][0] == 6 || this.colorData[0][0] == 7)
			return true;

		return false;
	}
	/** 
	 * This method takes care of identifying is the color sensor detects a white object
	 * @return it returns true if the color sensor detects a white object and false otherwise
	 */
	public boolean whiteObject() {
		if (this.colorData[0][0] == 13 || this.colorData[0][0] == 12)
			return true;

		return false;
	}

	//
	/** 
	 * This method takes care of returning the reading at the ith scan
	 * @i is the ith scan
	 * @return it returns the readings taken by the color sensor at the ith scan
	 */
	public float getReadingOf(int i) {
		return this.colorData[i][0];
	}

}
