package sensorController;
/*
 * File: MaxValueFilter.java
 * Written by: Rick Wu
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * MaxValueFilter class is responsible for the implementation of the maximum value filter
 * for the ultrasonic readings
 */


import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

/**
 * MaxValueFilter class is responsible for the implementation of the maximum value filter
 * for the ultrasonic readings
 *
 */
public class MaxValueFilter extends AbstractFilter {
	/* Instance Variables*/
	float[] sample;
	private float max;

	/** 
	 * Constructor
	 * This method takes care of initializing the MaxValueFilter object 
	 * @param source is a sample provider to be filtered
	 * @param max is the maximum value where everything above it will be ignored
	 */
	public MaxValueFilter(SampleProvider source, float max) {
		super(source);
		this.max = max;
	}
	/** 
	 * This method takes care of fetching a sample 
	 * @param sample is an array where the samples should be filtered
	 * @param offset is the offset in the sample array
	 */
	public void fetchSample(float sample[], int offset) {
		super.fetchSample(sample, offset);
		for (int i = 0; i < sampleSize(); i++) {
			if (sample[offset + i] > max) {
				sample[offset+i] = max;
			}
		}
	}
}
