package ev3Sensors;

import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;


public class FilteredUltrasonicPoller extends Thread {
	private SampleProvider usFilteredSource;
	// initialize an array of floats for fetching samples
	float[] usData;

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

	public void run() {
		while (true) {
			usFilteredSource.fetchSample(usData, 0); // acquire data
			try {
				Thread.sleep(25);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}

	public float getDistance() {
		return this.usData[0];
	}

}