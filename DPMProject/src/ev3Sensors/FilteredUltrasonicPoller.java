package ev3Sensors;

import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;

public class FilteredUltrasonicPoller extends Thread {
	// Adjustable Variables
	private final static int delay = 50;
	// Static Resources
	private SampleProvider usFilteredSource[] = new SampleProvider[2];
	float[][] usData = new float[2][];

	public FilteredUltrasonicPoller(SensorModes[] usSensor, float[] maxValue, int[] ReadingsToMedian) {
		SampleProvider usReading[] = new SampleProvider[2];
		SampleProvider usCappedSource[] = new SampleProvider[2];
		SampleProvider usMedianSource[] = new SampleProvider[2];
		for (int i = 0; i < 2; i++) {
			usReading[i] = (usSensor[i]).getMode("Distance");
			// Filter which caps sensor values to n
			usCappedSource[i] = new MaxValueFilter(usReading[i], maxValue[i]);
			// Stack a filter which takes average readings
			usMedianSource[i] = new MedianFilter(usCappedSource[i], ReadingsToMedian[i]);
			// The final, filtered data from the us sensor is stored in usFilteredSource
			this.usFilteredSource[i] = usMedianSource[i];
			// initialize an array of floats for fetching samples
			this.usData[i] = new float[usFilteredSource[i].sampleSize()];

		}

	}

	public void run() {
		while (true) {
			// US SENSOR ONE
			usFilteredSource[0].fetchSample(usData[0], 0); // acquire data
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}

			// US SENSOR TWO
			usFilteredSource[1].fetchSample(usData[1], 0); // acquire data
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
		}
	}

	//TODO
	// Legacy method
	// Currently just returns the reading of the first sensor until every other class is adjusted
	public float getDistance() {
		return this.usData[0][0];
	}
	
	
	public float getDistanceOf(int i){
		return this.usData[i][0];
	}

}
