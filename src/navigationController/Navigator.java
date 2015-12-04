package navigationController;


import sensorController.FilteredColorPoller;
import navigationController.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import motorController.DriveController;

/**
 * This class extends Thread and contains methods which
 * controls navigation and general robot movement
 *
 */

public class Navigator{
	final int FAST = 250;
	final int SLOW = 100;
	final long delayAmount = 100;
	static final int ACCELERATION = 1500;
	final static double DEG_ERR = 3.0, CM_ERR = 3.0;
	private Odometer odo;
	public volatile boolean correctionFlag = false;
	public DriveController drive;
	public FilteredColorPoller colorPoller;
	private OdometerCorrection odoC;

	/**
	 * The constructor takes a Odometer and DriveController
	 * 
	 * @param odo Used to retrieve current x,y,theta position for navigation
	 */

	public Navigator(Odometer odo, OdometerCorrection odoC, DriveController drive, FilteredColorPoller colorPoller) {
		this.odo = odo;
		this.drive = drive;
		this.colorPoller = colorPoller;
		this.odoC = odoC;
		this.drive.setAcceleration(ACCELERATION);
	}


	/**
	 * This method turns towards a (x,y) coordinate and then travels in a straight
	 * line until the destination
	 * @param x The desired X coordinate
	 * @param y The desired Y coordinate
	 */
	public void travelTo(double x, double y) {
		drive.setSpeeds(0, 0);
		delay(delayAmount);

		double distance = getDistanceFromRobot(x, y);
		adjustHeading(x, y, false,false);

		while (distance > CM_ERR) {
			if (odoC.doCorrectionRoutine()) {
				// Check if correction needs to be done, if so perform it. Otherwise
				// go do the normal travelTo stuff.
				distance = getDistanceFromRobot(x, y);
			}

			else {
				adjustHeading(x, y, true,false);
				drive.setSpeeds(FAST, FAST);
				distance = getDistanceFromRobot(x, y);

			}
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

		double distance = getDistanceFromRobot(x, y);
		adjustHeading(x, y, false,true);

		while (distance > CM_ERR) {
		
				adjustHeading(x, y, true,true);
				drive.setSpeeds(-1*FAST, -1*FAST);
				distance = getDistanceFromRobot(x, y);

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
	 * This method turns the robot to face a given x, y position.
	 * @param x The given X coordinate
	 * @param y The given Y coordinate
	 * @param checkDegError If true, don't rotate for angles under the DEG_ERROR
	 * @param backwards If true, face the robot's back to the given x,y position
	 */
	public void adjustHeading(double x, double y, boolean checkDegError, boolean backwards) {
		double minAng;
		minAng = (Math.atan2(y - odo.getY(), x - odo.getX())) * (180.0 / Math.PI);
		if (minAng < 0)
			minAng += 360.0;

		if (backwards) minAng += 180;
		// If checkDegError = true, only turn if minAng > DEG_ERR
		if (checkDegError) {
			if (getAngleDistance(minAng, odo.getAng()) > DEG_ERR)
				this.turnTo(minAng, true);
		} else if (!checkDegError) {
			this.turnTo(minAng, true);
		}

	}

	/**
	 * Helper method which returns the shortest distance between angles a and b in degrees
	 * @param a The first angle
	 * @param b The second angle
	 * @return The shortest distance between angle a and b (-180 to 180 degrees)
	 */
	public double getAngleDistance(double a, double b) {
		// Given a and b, find the minimum distance between a and b (in degrees)
		// while accounting for angle wrapping
		double result = 0;
		// Find the difference
		result = Math.abs(a - b);
		// Account for wrapping
		if (result > 180) {
			result = -1 * (result - 360);
		}

		return result;
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


}