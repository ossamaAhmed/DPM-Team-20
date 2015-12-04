package navigationController;

import motorController.DriveController;
import sensorController.FilteredColorPoller;

/**
 * This class detects grid lines and updates the robot x,y, and heading
 * as appropriate
 *
 */
public class OdometerCorrection {
	//Resources
	private Odometer odo;
	private FilteredColorPoller colorPoller;
	private DriveController drive;
	public volatile boolean run = false;
	//Variables
	private final static double distanceOfSensor = 9; // Distance of sensor from center of robot wheels
	private final float lineIntensity = 0.5f; //0.6
	private final double correctionDistanceThreshold = 10;
	private final int abortAngle = 25; //45
	//
	private double[] lastCorrectionCoordinate = { 900, 900 }; // Last place odometry correction occured 
	private double[] latchedPos = new double[3];
	boolean[] sensorIsOnLine = new boolean[3];

	public OdometerCorrection(Odometer odo, FilteredColorPoller colorPoller, DriveController drive) {
		this.odo = odo;
		this.colorPoller = colorPoller;
		this.drive = drive;
	}

	/**
	 * Checks if correction should be performed
	 * @return True if correction took place, false otherwise
	 */
	public boolean doCorrectionRoutine() {
		if (!run) return false;
		sensorIsOnLine[1] = lineDetected(1);
		sensorIsOnLine[2] = lineDetected(2);
		if ((sensorIsOnLine[1] || sensorIsOnLine[2]) && shouldCorrect()) {
			System.out.println("1. Starting allign robot at " + (int) odo.getX() + " , " + (int) odo.getY() + " , " + (int) odo.getAng());
			if (!doAllignRobot()){
				lastCorrectionCoordinate[0] = odo.getX();
				lastCorrectionCoordinate[1] = odo.getY();
				return false;
			};
			doOdoCorrection();
			lastCorrectionCoordinate[0] = odo.getX();
			lastCorrectionCoordinate[1] = odo.getY();
			System.out.println("3. Corrected odo to " + (int) odo.getX() + " , " + (int) odo.getY() + " , " + (int) odo.getAng());
			return true;

			// Check if either sensor is on the line, afterwards:
			// Stop the navigator, allign the robot, and then correct the odometer

		}
		
		else{
			return false;
		}

	}

	/**
	 * This method explicitly updates the odometer readings to the 
	 * new corrected values
	 */
	public void doOdoCorrection() {
		// This is what the odometer is reading when the line is crossed
		latchedPos[0]=odo.getX();
		latchedPos[1]= odo.getY();
		latchedPos[2]= odo.getAng();
		double currentX = latchedPos[0];
		double currentY = latchedPos[1];
		// This is the robots new x and y;
		double newX = currentX;
		double newY = currentY;
		double newHeading = roundToNearestMultipleOf(latchedPos[2], 90);
		int direction = getDirection();
		System.out.println(direction);
		switch (direction) {
		case 1: {
			newY = roundToNearestMultipleOf((currentY-distanceOfSensor), 30);
			newY += distanceOfSensor;
			break;

		}
		case 2: {
			newY = roundToNearestMultipleOf(currentY+(distanceOfSensor), 30);
			newY -= distanceOfSensor;
			break;
		}
		case 3: {
			newX = roundToNearestMultipleOf((currentX-distanceOfSensor), 30);
			newX += distanceOfSensor;
			break;

		}
		case 4: {
			newX = roundToNearestMultipleOf((currentX+distanceOfSensor), 30);
			newX -= distanceOfSensor;
			break;
		}
		}
			
		double[] updatePos = { newX, newY, newHeading };
		boolean[] updateBoolean = { true, true, true };
		odo.setPosition(updatePos, updateBoolean);

	}

	/**
	 * This method attempts to align the motors of the robot on a line
	 * @return True if the motors were aligned, false otherwise
	 */
	public boolean doAllignRobot() {
		double initAngle = odo.getAng();
		boolean[] sensorFlag = {false,false,false};
		if (sensorIsOnLine[1]) sensorFlag[1] = true;
		if (sensorIsOnLine[2]) sensorFlag[2] = true;
		sensorIsOnLine[1] = lineDetected(1);
		sensorIsOnLine[2] = lineDetected(2);
		
		while (!sensorFlag[1] || !sensorFlag[2]) {
			if (sensorFlag[1])
				drive.setSpeeds(0, drive.SLOW);
			else if (sensorFlag[2])
				drive.setSpeeds(drive.SLOW, 0);
			sensorIsOnLine[1] = lineDetected(1);
			sensorIsOnLine[2] = lineDetected(2);
			if (sensorIsOnLine[1]) sensorFlag[1] = true;
			if (sensorIsOnLine[2]) sensorFlag[2] = true;
			if (Math.abs(odo.minimumAngleFromTo(odo.getAng(),initAngle)) > abortAngle){
				System.out.println("Aborting allignRobot");
				return false;
				
				
			}

		}
		delay(200);
		drive.setSpeeds(0, 0);
		delay(250);
		return true;

		// While either sensor is not on the line, stop the motor which is on the line
		// and wait until both sensors are on the line before returning

	}

	/**
	 * Checks the reading if a given sensor, to see if it is detecting a line
	 * @param sensorNumber The sensor to check
	 * @return True if a line is detected, false otherwise
	 */
	public boolean lineDetected(int sensorNumber) {
		if (colorPoller.getReadingOf(sensorNumber) < lineIntensity) {
			return true;
		}

		else {
			return false;
		}

	}

	/**
	 * This method checks the heading of the robot and determines its direction
	 * @return The direction of the robot (N,S,E,W)
	 */
	public int getDirection() {
		// Null = 0
		// North = 1
		// South = 2
		// East = 3
		// West = 4

		int result = 0;
		double angle = odo.getAng();
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

	/**
	 * This method checks if a sufficient distance has been traveled since the last odometer correction
	 * @return True if a sufficient distance has been traveled since last corection, false otherwise
	 */
	public boolean shouldCorrect() {
		double[] currentCoordinates = { odo.getX(), odo.getY() };
		double distance = odo.getCoordinateDistance(currentCoordinates, lastCorrectionCoordinate);
		if (distance >= correctionDistanceThreshold) {
			return true;
		}

		else
			return false;

	}

	/**
	 * This helper method rounds a given number to the nearest given multiple
	 * @param numberToRound The number to round
	 * @param multipleOf The multiple to round to
	 * @return The rounded number
	 */
	public double roundToNearestMultipleOf(double numberToRound, double multipleOf) {
		double result = Math.round(numberToRound / multipleOf) * multipleOf;
		System.out.println("Number to round:"+numberToRound+  "  Result: " +result);
		return result;
	}
	

	/**
	 * Helper method to sleep the thread
	 * @param time The amount of time to sleep the thread in ms
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
	 * Turns on odometer correction
	 */
	public void run(){
		this.run=true;
	}
	
	/**
	 * Turns off odometer correction
	 * 
	 */
	public void stop(){
		this.run=false;
	}

}