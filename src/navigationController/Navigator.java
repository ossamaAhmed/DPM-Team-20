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
import sensorController.FilteredColorPoller;
import navigationController.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import motorController.DriveController;

/**
 * This class extends Thread and contains methods which
 * controls navigation and general robot movement
 *
 */
public class Navigator extends Thread {
	final int FAST = 100;
	final int SLOW = 100;
	final long delayAmount = 200;
	static final int ACCELERATION = 1500;
	final static double DEG_ERR = 2.0, CM_ERR = 3.0;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	public DriveController drive;
	public FilteredColorPoller colorPoller;

	/**
	 * The constructor takes a Odometer and DriveController
	 * 
	 * @param odo Used to retrieve current x,y,theta position for navigation
	 */
	public Navigator(Odometer odo, DriveController drive,FilteredColorPoller colorPoller) {
		this.odometer = odo;
		this.drive = drive;
		EV3LargeRegulatedMotor[] motors = this.drive.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.colorPoller=colorPoller;
		// set acceleration
		this.drive.setAcceleration(ACCELERATION);
		this.start();
	}

	public void travelToDiagonaly(double x, double y) {
		drive.setSpeeds(0, 0);
		delay(delayAmount);
		
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
		delay(delayAmount);
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
		drive.setSpeeds(0, 0);
		delay(delayAmount);
		
		double minAng;
		minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
		if (minAng < 0) minAng += 360.0;
		
		this.turnTo(minAng, true);
		drive.setSpeeds(FAST, FAST);
		
		double dx= x - odometer.getX();
		double dy= y - odometer.getY();
		double distance= Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
		while (distance > CM_ERR ) {
//			if(colorPoller.isColorSensorReadingBlackLine(1)&&colorPoller.isColorSensorReadingBlackLine(2)){
//				drive.setSpeeds(FAST, FAST);
////				double correctedY= odometer.getY();
////				double correctedX= odometer.getX();
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
////				if( getDirection()==1){
////					 correctedY= roundToNearestMultipleOf(odometer.getY(), 30-5.5);
////					 correctedX= odometer.getX();
////				}
////				else if(getDirection()==2){
////					 correctedY= roundToNearestMultipleOf(odometer.getY(), 30+5.5);
////					 correctedX= odometer.getX();
////				}
////				else if(getDirection()==3){
////					 correctedX= roundToNearestMultipleOf(odometer.getX(), 30-5.5);
////					 correctedY= odometer.getY();
////
////
////				}
////				else if( getDirection()==4){
////					 correctedX= roundToNearestMultipleOf(odometer.getX(), 30+5.5);
////					 correctedY= odometer.getY();
////				}
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
////				double[] newpos= {correctedX,correctedY,0};
////				boolean[] newbol= {true,true,false};
////				odometer.setPosition(newpos, newbol);
//
//			}
//			else if(colorPoller.isColorSensorReadingBlackLine(1)&&!colorPoller.isColorSensorReadingBlackLine(2)){
//				double correctedY= odometer.getY();
//				double correctedX= odometer.getX();
//				drive.setSpeeds(0, FAST);
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
////				if( getDirection()==1 ||  getDirection()==2){
////					 correctedY= roundToNearestMultipleOf(odometer.getY(), 30-5.5);
////					 correctedX= odometer.getX();
////				}
////				else if(getDirection()==2){
////					 correctedY= roundToNearestMultipleOf(odometer.getY(), 30+5.5);
////					 correctedX= odometer.getX();
////				}
////				else if(getDirection()==3){
////					 correctedX= roundToNearestMultipleOf(odometer.getX(), 30-5.5);
////					 correctedY= odometer.getY();
////
////
////				}
////				else if( getDirection()==4){
////					 correctedX= roundToNearestMultipleOf(odometer.getX(), 30+5.5);
////					 correctedY= odometer.getY();
////				}
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
////				double[] newpos= {correctedX,correctedY,0};
////				boolean[] newbol= {true,true,false};
////				odometer.setPosition(newpos, newbol);
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
//			
//
//			}
//			else if(!colorPoller.isColorSensorReadingBlackLine(1)&&colorPoller.isColorSensorReadingBlackLine(2)){
//				double correctedY= odometer.getY();
//				double correctedX= odometer.getX();
//				drive.setSpeeds(FAST, 0);
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
////				if( getDirection()==1 ||  getDirection()==2){
////					 correctedY= roundToNearestMultipleOf(odometer.getY(), 30-5.5);
////					 correctedX= odometer.getX();
////				}
////				else if(getDirection()==2){
////					 correctedY= roundToNearestMultipleOf(odometer.getY(), 30+5.5);
////					 correctedX= odometer.getX();
////				}
////				else if(getDirection()==3){
////					 correctedX= roundToNearestMultipleOf(odometer.getX(), 30-5.5);
////					 correctedY= odometer.getY();
////
////
////				}
////				else if( getDirection()==4){
////					 correctedX= roundToNearestMultipleOf(odometer.getX(), 30+5.5);
////					 correctedY= odometer.getY();
////				}
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
////				double[] newpos= {correctedX,correctedY,0};
////				boolean[] newbol= {true,true,false};
////				odometer.setPosition(newpos, newbol);
////				System.out.println("correcting "+odometer.getX()+"  y:" + odometer.getY());
//				
//			}
//			else{
//				
//				drive.setSpeeds(FAST, FAST);
//			}
			dx=x - odometer.getX();
			dy= y - odometer.getY();
			distance= Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
			}
		
		drive.setSpeeds(0, 0);
		delay(delayAmount);
	}
	
	public void travelToBackwards(double x, double y){
		drive.setSpeeds(0, 0);
		delay(delayAmount);
		
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
		delay(delayAmount);
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
		drive.setSpeeds(0, 0);
		delay(delayAmount);
		
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
			delay(delayAmount);
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
	public void goBackwards(double distance) {
		if (distance >= 0){
		this.travelToBackwards(odometer.getX()- Math.cos(Math.toRadians(this.odometer.getAng())) * distance,odometer.getY()- Math.sin(Math.toRadians(this.odometer.getAng())) * distance);
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
	
	public void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() {
		// Nothing needs to run here
		
	}
	
	public int getDirection() {
		// Null = 0
		// North = 1
		// South = 2
		// East = 3
		// West = 4

		int result = 0;
		double angle = odometer.getAng();
		//East
		if (angle <= 45 && angle >= 0 || angle >= 315 && angle <= 360) {
			result = 3;
		}
		// North
		else if (angle > 45 && angle < 135) {
			result = 1;
		}
		// West
		else if (angle >= 135 && angle <= 235) {
			result = 4;
		}
		// South
		else if (angle > 235 && angle < 315) {
			result = 2;

		}

		return result;

	}
	public double roundToNearestMultipleOf(double numberToRound, double multipleOf) {
		double result = Math.round(numberToRound / multipleOf) * multipleOf;
		return result;
	}
}
