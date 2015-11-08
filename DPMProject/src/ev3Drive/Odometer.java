package ev3Drive;
/*
 * File: Odometer.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Class which controls the odometer for the robot
 * 
 * Odometer defines cooridinate system as such...
 * 
 * 					90Deg:pos y-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 180Deg:neg x-axis------------------0Deg:pos x-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 					270Deg:neg y-axis
 * 
 * The odometer is initalized to 90 degrees, assuming the robot is facing up the positive y-axis
 * 
 */

import lejos.utility.Timer;
import lejos.utility.TimerListener;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is responsible for updating the robot's
 * current x,y and theta position
 *
 */
/**
 * @author rick
 *
 */
/**
 * @author rick
 *
 */
public class Odometer implements TimerListener {

	private Timer timer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final int DEFAULT_TIMEOUT_PERIOD = 20;
	private double leftRadius, rightRadius, width;
	private double x, y, theta;
	private double[] oldDH, dDH;
	
	// constructor
	/**
	 * @param leftMotor The left driving motor
	 * @param rightMotor The right driving motor
	 * @param INTERVAL The time between each update of the odometer
	 * @param autostart If true, the odometer will start when initialized
	 */
	public Odometer (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int INTERVAL, boolean autostart) {
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		// default values, modify for your robot
		this.rightRadius = 2.1;
		this.leftRadius = 2.1;
		this.width = 11.0;
		
		this.x = 0.0;
		this.y = 0.0;
		this.theta =0;
		this.oldDH = new double[2];
		this.dDH = new double[2];

		if (autostart) {
			// if the timeout interval is given as <= 0, default to 20ms timeout 
			this.timer = new Timer((INTERVAL <= 0) ? INTERVAL : DEFAULT_TIMEOUT_PERIOD, this);
			this.timer.start();
		} else
			this.timer = null;
	}
	
	// functions to start/stop the timerlistener
	/**
	 * This method stops the odometer polling
	 * 
	 */
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}
	/**
	 * This method starts the odometer polling
	 */
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}
	
	/*
	 * Calculates displacement and heading as title suggests
	 */
	/**This method calculates the change in displacement and heading
	 * between the last tachometer polling
	 * @param data The change in x,y, and theta are returned in this array
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
		data[1] = (rightTacho * rightRadius - leftTacho * leftRadius) / width;
	}
	
	/*
	 * Recompute the odometer values using the displacement and heading changes
	 */
	/**
	 * This method is called every poll of the odometer and is responsible for updating
	 * the displacemet and heading
	 */
	public void timedOut() {
		this.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (this) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.cos(Math.toRadians(theta));
			y += dDH[0] * Math.sin(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	// return X value
	/**
	 * This is the getter method for the X coordinate
	 * @return The current odometer X value
	 */
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	// return Y value
	/**
	 * This is the getter method for the Y coordinate
	 * @return The current odometer Y value
	 */
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	// return theta value
	/**
	 * This is the getter method for the heading in degrees
	 * @return The current heading
	 */
	public double getAng() {
		synchronized (this) {
			return theta;
		}
	}

	// set x,y,theta
	/**
	 * The setter method for x,y, and theta
	 * @param position An array corresponding to the new x,y, and theta values
	 * @param update An array of booleans corresponding to if the respective
	 * positions should be updated
	 */
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = fixDegAngle(position[2]);
		}
	}

	// return x,y,theta
	/**
	 * Getter method for the current robot position
	 * @param position An array corresponding to the x,y,and theta (degrees) values
	 * respectively
	 */
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}
	
	/**
	 * Getter method for the current robot position
	 * @param position An array corresponding to the x,y,and theta (degrees) values
	 * respectively
	 */

	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}
	
	
	/**
	 * Setter method for the robot heading
	 * @param newAngle The desired new heading in degrees
	 */
	public void setAng(double newAngle){
		this.theta = fixDegAngle(newAngle);
	}
	
	// accessors to motors
	/**
	 * Getter method for both motors
	 * @return The motors in an array
	 */
	public EV3LargeRegulatedMotor [] getMotors() {
		return new EV3LargeRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}
	/**
	 * Getter method for the left motor
	 * @return The left motor
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	/**
	 * Getter method for the right motor
	 * @return The right motor
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	// static 'helper' methods
	/**
	 * Helper method which wraps the angle from 0 to 360 degrees
	 * @param angle The angle to correct
	 * @return The corrected angle
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	/**
	 * Helper method to get the shortest distance between two angles
	 * @param a The first angle
	 * @param b The second angle
	 * @return The shortest rotation to travel from the first angle
	 * to the second angle (-180 to 180 degrees)
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
