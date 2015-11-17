package navigationController;
/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import navigationController.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import motorController.DriveController;

/**
 * This class extends Thread and contains methods which
 * controls navigation and general robot movement
 *
 */
public class Navigator extends Thread {
	final int FAST = 200;
	final int SLOW = 50;
	static final int ACCELERATION = 1500;
	final static double DEG_ERR = 2.0, CM_ERR = 1.0;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	public DriveController drive;

	/**
	 * The constructor takes a Odometer and DriveController
	 * 
	 * @param odo Used to retrieve current x,y,theta position for navigation
	 */
	public Navigator(Odometer odo, DriveController drive) {
		this.odometer = odo;
		this.drive = drive;
		EV3LargeRegulatedMotor[] motors = this.drive.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.drive.setAcceleration(ACCELERATION);
		this.start();
	}





	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	/**
	 * This method turns towards a (x,y) coordinate and then travels in a straight
	 * line until the destination
	 * @param x The desired X coordinate
	 * @param y The desired Y coordinate
	 */
	public void travelTo(double x, double y) {
		double minAng;
		minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
		if (minAng < 0) minAng += 360.0;
		
		this.turnTo(minAng, true);
		drive.setSpeeds(FAST, FAST);
		
		double dx= x - odometer.getX();
		double dy= y - odometer.getY();
		double distance= Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
		while (distance > CM_ERR ) {
			dx=x - odometer.getX();
			dy= y - odometer.getY();
			distance= Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
			}
		
		drive.setSpeeds(0, 0);
	}
	
	public void travelToBackwards(double x, double y){
		
		double minAng;
		minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
		if (minAng < 0) minAng += 360.0;
		minAng+=180;
		
		this.turnTo(minAng, true);
		drive.setSpeeds(-1*FAST, -1*FAST);
		
		double dx= x - odometer.getX();
		double dy= y - odometer.getY();
		double distance= Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
		while (distance > CM_ERR ) {
			dx=x - odometer.getX();
			dy= y - odometer.getY();
			distance= Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
			}
		
		drive.setSpeeds(0, 0);
	}
		

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	/**
	 * This method turns the robot towards a given degree angle (where 0 degrees is east)
	 * @param angle The desired degree angle to turn to
	 * @param stop If true, the robot will stop its motors after turning to the desired angle
	 */
	public void turnTo(double angle, boolean stop) {
		angle = Odometer.fixDegAngle(angle);
		double error = angle - this.odometer.getAng();
		
	

		while (Math.abs(error) > DEG_ERR) {
			error = angle - this.odometer.getAng();
			if (error < -180.0) {
				drive.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				drive.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				drive.setSpeeds(SLOW, -SLOW);
			} else {
				drive.setSpeeds(-SLOW, SLOW);
			}
		}
		if (stop) {
			drive.setSpeeds(0, 0);
		}
	}
	

	/**
	 * This method moves the robot forward a given distance in cm
	 * @param distance The desired distance to move forward in cm
	 */
	public void goForward(double distance) {
		if (distance >= 0){
		this.travelTo(odometer.getX()+ Math.cos(Math.toRadians(this.odometer.getAng())) * distance,odometer.getY()+ Math.sin(Math.toRadians(this.odometer.getAng())) * distance);
		}
		
		else {
			
		}

	}
	
	/**
	 * This method moves the robot forward one tile (given a tile size)
	 * @param tileSize The tile size 
	 */
	public void goForwardOneTile(double tileSize){
		goForward(tileSize);
	}
	
	/**
	 * This method moves the robot forward half a tile (given a tile size)
	 * @param tileSize The tile size
	 */
	public void goForwardHalfTile(double tileSize){
		goForward(0.5*tileSize);
		
	}
	
	/**
	 * Turns the robot to face north
	 * 
	 */
	public void turnUp (){
		turnTo(90,true);
	}
	
	/**
	 * Turns the robot to face east
	 * 
	 */
	public void turnRight() {
		turnTo(0,true);
	}
	
	/**
	 * Turns the robot to face west
	 */
	public void turnLeft() {
		turnTo(180,true);
	}
	
	/**
	 * Turns the robot to face south
	 * 
	 */
	public void turnDown() {
		turnTo(270,true);
	}
	
	/**
	 * The getter method for the robot's x position
	 * @return The odometer x position
	 */
	public double getCurrentX(){
		return odometer.getX();
	}
	
	/**
	 * The getter method for the robot's y position
	 * @return The odometer y position
	 */
	public double getCurrentY(){
		return odometer.getY();
	}
	
	/**
	 * This helper method returns the distance between two coordinates
	 * @param a Array of coordinates , x and y
	 * @param b Array of coordinates , x and y
	 * @return The distance between a(x,y) and b(x,y)
	 */
	public double getCoordinateDistance(double[] a, double[] b){
		double dx = a[0]-b[0];
		double dy = a[1] -b[1];
		double distance=Math.sqrt(dx*dx + dy*dy);
		return distance;
	}
	public void start() {
		// Nothing needs to run here
		
	}
}
