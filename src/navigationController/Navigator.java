package navigationController;

/*
 * File: Navigation.java
 */
import FieldMap.Field;
import FieldMap.Robot;
import FieldMap.Tile;
import sensorController.FilteredColorPoller;
import sensorController.FilteredUltrasonicPoller;
import navigationController.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import motorController.DriveController;

/**
 * This class extends Thread and contains methods which
 * controls navigation and general robot movement
 *
 */
public class Navigator extends Thread {
	final static int FAST = 100;
	final static int SLOW = 100;
	final static int VERY_SLOW = 50;
	final long delayAmount = 200;
	static final int ACCELERATION = 1500;
	final static double DEG_ERR = 2.0, CM_ERR = 3.0;
	private Odometer odo;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private DriveController drive;
	private FilteredColorPoller colorPoller;
	private FilteredUltrasonicPoller usPoller;

	/**
	 * The constructor takes a Odometer and DriveController
	 * 
	 * @param odo Used to retrieve current x,y,theta position for navigation
	 */
	public Navigator(Odometer odo, DriveController drive, FilteredUltrasonicPoller usPoller, FilteredColorPoller colorPoller) {
		this.odo = odo;
		this.drive = drive;
		EV3LargeRegulatedMotor[] motors = this.drive.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.usPoller = usPoller;
		this.colorPoller = colorPoller;
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
		drive.setSpeeds(0, 0);
		delay(delayAmount);

		double minAng;
		minAng = (Math.atan2(y - odo.getY(), x - odo.getX())) * (180.0 / Math.PI);
		if (minAng < 0)
			minAng += 360.0;

		this.turnTo(minAng, true);
		drive.setSpeeds(FAST, FAST);

		double dx = x - odo.getX();
		double dy = y - odo.getY();
		double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		while (distance > CM_ERR) {

			dx = x - odo.getX();
			dy = y - odo.getY();
			distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		}

		drive.setSpeeds(0, 0);
		delay(delayAmount);
	}

	/**
	 * This method turns away from a (x,y) coordinate and then travels backwards in a straight
	 * line until the destination
	 * @param x The desired X coordinate
	 * @param y The desired Y coordinate
	 */
	public void travelToBackwards(double x, double y) {
		drive.setSpeeds(0, 0);
		delay(delayAmount);

		double minAng;
		minAng = (Math.atan2(y - odo.getY(), x - odo.getX())) * (180.0 / Math.PI);
		if (minAng < 0)
			minAng += 360.0;
		minAng += 180;

		this.turnTo(minAng, true);
		drive.setSpeeds(-1 * FAST, -1 * FAST);

		double dx = x - odo.getX();
		double dy = y - odo.getY();
		double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		while (distance > CM_ERR) {
			dx = x - odo.getX();
			dy = y - odo.getY();
			distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		}

		drive.setSpeeds(0, 0);
		delay(delayAmount);
	}
	
	/**
	 * This method makes the robot travels in a straight line backwards depending on the distance to a given tile.
	 * @param robotTileY The Y coordinate of the tile
	 * @param robotTileX The X coordinate of the tile
	 * @param myField The field to which the tile belongs
	 */
	public void travelBackToTile(int robotTileY, int robotTileX, Field myField){
		double[] myInitialPosition= {myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
				myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
		double[] myCurrentPosition= {getCurrentX(),
				getCurrentY()};
		double distanceToTravel= getCoordinateDistance(myCurrentPosition, myInitialPosition);
		goBackwards(distanceToTravel);
	}
	
	/**
	 * This method makes the robot travel forward until the ultrasonic sensor detects and object.
	 * @param detectionDistance The detection threshold distance
	 * @param timeOutDistance The distance to timeout after travelling
	 */
	public void goForwardUntilObject(float detectionDistance, double timeOutDistance){
		drive.setSpeeds(0, 0);
		delay(delayAmount);
		
		double startX = odo.getX();
		double startY = odo.getY();
		drive.setSpeeds(VERY_SLOW, VERY_SLOW);
		double travelledDistance = getDistanceFromRobot(startX,startY);
		while (usPoller.getDistance() >= detectionDistance){
			travelledDistance = getDistanceFromRobot(startX,startY);
			if (travelledDistance > timeOutDistance) break;
			//^ Wait inside this loop until an object is detected, or timeout
		}
		
		drive.setSpeeds(0, 0);
		delay(delayAmount);
		
	}
	


	/**
	 * This method turns the robot towards a given degree angle (where 0 degrees is east)
	 * @param angle The desired degree angle to turn to
	 * @param stop If true, the robot will stop its motors after turning to the desired angle
	 */
	public void turnTo(double angle, boolean stop) {
		drive.setSpeeds(0, 0);
		delay(delayAmount);

		angle = Odometer.fixDegAngle(angle);
		double error = angle - this.odo.getAng();

		while (Math.abs(error) > DEG_ERR) {
			error = angle - this.odo.getAng();
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
	 * Turns the robot towards a given tile
	 * @param suspectedTile The tile to turn to
	 * @param myRobot The robot to turn and position to update
	 * @param myField The field to which the tile belongs
	 */
	public void turnToTile(Tile suspectedTile, Robot myRobot, Field myField){
		int robotTileX = (int) (myRobot.getPosition().getPositionX() / myField
				.getTileSize());
		int robotTileY = (int) (myRobot.getPosition().getPositionY() / myField
				.getTileSize());
		int differenceX= robotTileX-suspectedTile.getTileIndexX();
		int differenceY= robotTileY-suspectedTile.getTileIndexY();
		if(differenceY==-1){
			turnUp();
		}
		else if(differenceY==1){
			turnDown();
		}
		else if(differenceX==-1){
			turnRight();
		}
		else if(differenceX==1){
			turnLeft();
		}
	}
	

	/**
	 * This method moves the robot forward a given distance in cm
	 * @param distance The desired distance to move forward in cm
	 */
	public void goForward(double distance) {
		if (distance >= 0) {
			this.travelTo(odo.getX() + Math.cos(Math.toRadians(this.odo.getAng())) * distance, odo.getY() + Math.sin(Math.toRadians(this.odo.getAng())) * distance);
		}

		else {

		}

	}

	/**
	 * This method moves the robot backwards a given distance in cm
	 * @param distance The desired distance to move forward in cm
	 */
	public void goBackwards(double distance) {
		if (distance >= 0) {
			this.travelToBackwards(odo.getX() - Math.cos(Math.toRadians(this.odo.getAng())) * distance, odo.getY() - Math.sin(Math.toRadians(this.odo.getAng())) * distance);
		}

		else {

		}

	}

	/**
	 * This method moves the robot forward one tile (given a tile size)
	 * @param tileSize The tile size 
	 */
	public void goForwardOneTile(double tileSize) {
		goForward(tileSize);
	}

	/**
	 * This method moves the robot forward half a tile (given a tile size)
	 * @param tileSize The tile size
	 */
	public void goForwardHalfTile(double tileSize) {
		goForward(0.5 * tileSize);

	}

	/**
	 * Turns the robot to face north
	 * 
	 */
	public void turnUp() {
		turnTo(90, true);
	}

	/**
	 * Turns the robot to face east
	 * 
	 */
	public void turnRight() {
		turnTo(0, true);
	}

	/**
	 * Turns the robot to face west
	 */
	public void turnLeft() {
		turnTo(180, true);
	}

	/**
	 * Turns the robot to face south
	 * 
	 */
	public void turnDown() {
		turnTo(270, true);
	}

	/**
	 * The getter method for the robot's x position
	 * @return The odometer x position
	 */
	public double getCurrentX() {
		return odo.getX();
	}

	/**
	 * The getter method for the robot's y position
	 * @return The odometer y position
	 */
	public double getCurrentY() {
		return odo.getY();
	}

	/**
	 * This helper method returns the distance between two coordinates
	 * @param a Array of coordinates , x and y
	 * @param b Array of coordinates , x and y
	 * @return The distance between a(x,y) and b(x,y)
	 */
	public double getCoordinateDistance(double[] a, double[] b) {
		double dx = a[0] - b[0];
		double dy = a[1] - b[1];
		double distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}

	/**
	 * Helper method which sleeps the thread for a given time.
	 * @param time The time to sleep the thread
	 */
	public void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the distance from a coordinate to the robots current position/
	 * @param x The x coordinate to compare to
	 * @param y The y coordinate to compare to
	 * @return The distance from the robot
	 */
	public double getDistanceFromRobot(double x, double y) {
		double dx = x - odo.getX();
		double dy = y - odo.getY();
		double result = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		return result;
	}

	public void start() {
		// Nothing needs to run here

	}

}
