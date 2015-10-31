package ev3Sensors;

import lejos.hardware.*;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;

public class FilteredColorPoller extends Thread {
	// Static Resources
	private SampleProvider colorFilteredSource[] = new SampleProvider[2];
	float[][] colorData = new float[2][];
	private String[] colorMode = { "ColorID", "Red" };

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
	public float getID() {
		return this.colorData[0][0];
	}

	public boolean blueObject() {
		if (this.colorData[0][0] == 6 || this.colorData[0][0] == 7)
			return true;

		return false;
	}

	public boolean whiteObject() {
		if (this.colorData[0][0] == 13 || this.colorData[0][0] == 12)
			return true;

		return false;
	}

	//

	public float getReadingOf(int i) {
		return this.colorData[i][0];
	}

}
