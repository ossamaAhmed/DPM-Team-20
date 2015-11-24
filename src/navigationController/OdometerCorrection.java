package navigationController;

import lejos.hardware.Sound;
import motorController.DriveController;
import sensorController.FilteredColorPoller;

public class OdometerCorrection extends Thread {
	//Resources
	private Odometer odo;
	private FilteredColorPoller colorPoller;
	private DriveController drive;
	private Navigator nav;
	//Variables
	private final static double distanceOfSensor = 5.5; // Distance of sensor from center of robot wheels
	private final float lineIntensity = 10;
	private final double correctionDistanceThreshold = 10;
	//
	private double[] lastCorrectionCoordinate = { 900, 900 }; // Last place odometry correction occured 

	public OdometerCorrection(Odometer odo, FilteredColorPoller colorPoller, DriveController drive,Navigator nav) {
		this.odo = odo;
		this.colorPoller = colorPoller;
		this.drive = drive;
		this.nav = nav;
	}

	public void run() {
		while (true) {
			if ((lineDetected(1) || lineDetected(2)) && shouldCorrect()) {
				nav.correctionInterrupt = true;
				doAllignRobot();	
				System.out.println("Performed correction at " + (int)odo.getX()+" , "+(int)odo.getY());
				doOdoCorrection();
				lastCorrectionCoordinate[0] = odo.getX();
				lastCorrectionCoordinate[1] = odo.getY();
				System.out.println("Corrected odo to " + (int)odo.getX()+" , "+(int)odo.getY());
				nav.correctionInterrupt = false;
				
				// Check if either sensor is on the line, afterwards:
				// Stop the navigator, allign the robot, and then correct the odometer
			}
			
			delay(50);

		}

	}

	public void doOdoCorrection() {
		// This is what the odometer is reading when the line is crossed
		double currentX = odo.getX();
		double currentY = odo.getY();
		// This is the robots new x and y;
		double newX = currentX;
		double newY = currentY;
		double newHeading = roundToNearestMultipleOf(odo.getAng(),90);
		int direction = getDirection();
		switch (direction) {
		case 1: {
			newY = roundToNearestMultipleOf(currentY, 15);
			newY -= distanceOfSensor;

		}
		case 2: {
			newY = roundToNearestMultipleOf(currentY, 15);
			newY += distanceOfSensor;
		}
		case 3:{
			newX = roundToNearestMultipleOf(currentX, 15);
			newX -= distanceOfSensor;
			
		}
		case 4: {
			newX = roundToNearestMultipleOf(currentX, 15);
			newX += distanceOfSensor;
		}
		}
		double[] updatePos = {newX,newY,newHeading};
		boolean[] updateBoolean = {true,true,true};
		odo.setPosition(updatePos, updateBoolean);

	}
	
	public void doAllignRobot(){
		boolean[] sensorIsOnLine = new boolean[3];
		sensorIsOnLine[1] = lineDetected(1);
		sensorIsOnLine[2] = lineDetected(2);
		while ( !sensorIsOnLine[1] || !sensorIsOnLine[2]){
			if (sensorIsOnLine[1]) drive.setSpeeds(0, drive.SLOW);
			if (sensorIsOnLine[2]) drive.setSpeeds(drive.SLOW, 0);
			sensorIsOnLine[1] = lineDetected(1);
			sensorIsOnLine[2] = lineDetected(2);
			
		}
		drive.setSpeeds(0, 0);
		delay(100);
		
		// While either sensor is not on the line, stop the motor which is on the line
		// and wait until both sensors are on the line before returning
		
	}

	public boolean lineDetected(int sensorNumber) {
		if (colorPoller.getReadingOf(sensorNumber) > lineIntensity) {
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

}