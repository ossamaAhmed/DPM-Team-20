package navigationController;

import lejos.hardware.Sound;
import sensorController.FilteredColorPoller;

public class OdometerCorrection extends Thread {
	//Resources
	private Odometer odo;
	private FilteredColorPoller colorPoller;
	//Variables
	private final static double distanceOfSensor = 5.5; // Distance of sensor from center of robot wheels
	private final float lineIntensity = 10;
	private final double correctionDistanceThreshold = 10;
	//
	private double[] lastCorrectionCoordinate = { 900, 900 }; // Last place odometry correction occured 

	public OdometerCorrection(Odometer odo, FilteredColorPoller colorPoller) {
		this.odo = odo;
		this.colorPoller = colorPoller;
	}

	public void run() {
		while (true) {
			if (lineDetected() && shouldCorrect()) {
				doCorrection();
				lastCorrectionCoordinate[0] = odo.getX();
				lastCorrectionCoordinate[1] = odo.getY();
				System.out.println("Performed correction at " + (int)lastCorrectionCoordinate[0]+" , "+(int)lastCorrectionCoordinate[1]);
			}

		}

	}

	public void doCorrection() {
		// This is what the odometer is reading when the line is crossed
		double currentX = odo.getX();
		double currentY = odo.getY();
		// This is the robots new x and y;
		double newX = currentX;
		double newY = currentY;
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
		double[] updatePos = {newX,newY,0};
		boolean[] updateBoolean = {true,true,false};
		odo.setPosition(updatePos, updateBoolean);

	}

	public boolean lineDetected() {
		if (colorPoller.getReadingOf(1) > lineIntensity) {
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

}