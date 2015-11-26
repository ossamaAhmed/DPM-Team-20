package navigationController;

import motorController.DriveController;
import sensorController.FilteredColorPoller;

public class OdometerCorrection {
	//Resources
	private Odometer odo;
	private FilteredColorPoller colorPoller;
	private DriveController drive;
	public volatile boolean run = false;
	//Variables
	private final static double distanceOfSensor = 4.2; // Distance of sensor from center of robot wheels
	private final float lineIntensity = 0.3f;
	private final double correctionDistanceThreshold = 15;
	//
	private double[] lastCorrectionCoordinate = { 900, 900 }; // Last place odometry correction occured 

	public OdometerCorrection(Odometer odo, FilteredColorPoller colorPoller, DriveController drive) {
		this.odo = odo;
		this.colorPoller = colorPoller;
		this.drive = drive;
	}

	public boolean doCorrectionRoutine() {
		if (!run) return false;
		if ((lineDetected(1) || lineDetected(2)) && shouldCorrect()) {
			System.out.println("1. Starting allign robot at " + (int) odo.getX() + " , " + (int) odo.getY() + " , " + (int) odo.getAng());
			doAllignRobot();
			
			System.out.println("2. Performed  odo correction at " + (int) odo.getX() + " , " + (int) odo.getY() + " , " + (int) odo.getAng());
			doOdoCorrection();
			lastCorrectionCoordinate[0] = odo.getX();
			lastCorrectionCoordinate[1] = odo.getY();
			System.out.println("3. Corrected odo to " + (int) odo.getX() + " , " + (int) odo.getY()+" , " + (int) odo.getAng());
			return true;

			// Check if either sensor is on the line, afterwards:
			// Stop the navigator, allign the robot, and then correct the odometer

		}
		
		else{
			return false;
		}

	}

	public void doOdoCorrection() {
		// This is what the odometer is reading when the line is crossed
		double currentX = odo.getX();
		double currentY = odo.getY();
		// This is the robots new x and y;
		double newX = currentX;
		double newY = currentY;
		double newHeading = roundToNearestMultipleOf(odo.getAng(), 90);
		int direction = getDirection();
		switch (direction) {
		case 1: {
			newY = roundToNearestMultipleOf(currentY, 30);
			newY -= distanceOfSensor;

		}
		case 2: {
			newY = roundToNearestMultipleOf(currentY, 30);
			newY += distanceOfSensor;
		}
		case 3: {
			newX = roundToNearestMultipleOf(currentX, 30);
			newX -= distanceOfSensor;

		}
		case 4: {
			newX = roundToNearestMultipleOf(currentX, 30);
			newX += distanceOfSensor;
		}
		}
		double[] updatePos = { newX, newY, newHeading };
		boolean[] updateBoolean = { true, true, true };
		odo.setPosition(updatePos, updateBoolean);

	}

	public void doAllignRobot() {
		boolean[] sensorIsOnLine = new boolean[3];
		sensorIsOnLine[1] = lineDetected(1);
		sensorIsOnLine[2] = lineDetected(2);
		while (!sensorIsOnLine[1] || !sensorIsOnLine[2]) {
			if (sensorIsOnLine[1])
				drive.setSpeeds(0, drive.SLOW);
			else if (sensorIsOnLine[2])
				drive.setSpeeds(drive.SLOW, 0);
			sensorIsOnLine[1] = lineDetected(1);
			sensorIsOnLine[2] = lineDetected(2);

		}
		drive.setSpeeds(0, 0);
		delay(250);

		// While either sensor is not on the line, stop the motor which is on the line
		// and wait until both sensors are on the line before returning

	}

	public boolean lineDetected(int sensorNumber) {
		if (colorPoller.getReadingOf(sensorNumber) < lineIntensity) {
			return true;
		}

		else {
			return false;
		}

	}

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

	public boolean shouldCorrect() {
		double[] currentCoordinates = { odo.getX(), odo.getY() };
		double distance = odo.getCoordinateDistance(currentCoordinates, lastCorrectionCoordinate);
		if (distance >= correctionDistanceThreshold) {
			return true;
		}

		else
			return false;

	}

	public double roundToNearestMultipleOf(double numberToRound, double multipleOf) {
		double result = Math.round(numberToRound / multipleOf) * multipleOf;
		return result;
	}

	public void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		this.run=true;
	}
	
	public void stop(){
		this.run=false;
	}

}