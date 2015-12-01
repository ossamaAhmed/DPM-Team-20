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
	private final static double distanceOfSensor = 9; // Distance of sensor from center of robot wheels
	private final float lineIntensity = 0.7f;
	private final double correctionDistanceThreshold = 10;
	private final int abortAngle = 45;
	//
	private double[] lastCorrectionCoordinate = { 900, 900 }; // Last place odometry correction occured 
	private double[] latchedPos = new double[3];

	public OdometerCorrection(Odometer odo, FilteredColorPoller colorPoller, DriveController drive) {
		this.odo = odo;
		this.colorPoller = colorPoller;
		this.drive = drive;
	}

	public boolean doCorrectionRoutine() {
		if (!run) return false;
		if ((lineDetected(1) || lineDetected(2)) && shouldCorrect()) {
			System.out.println("1. Starting allign robot at " + (int) odo.getX() + " , " + (int) odo.getY() + " , " + (int) odo.getAng());
			latchedPos[0]=odo.getX();
			latchedPos[1]= odo.getY();
			latchedPos[2]= odo.getAng();
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

	public void doOdoCorrection() {
		// This is what the odometer is reading when the line is crossed
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
//		if (newX == latchedPos[0]) {newX = odo.getX();}
//		if (newY == latchedPos[1]) {newX = odo.getY();}
			
		double[] updatePos = { newX, newY, newHeading };
		boolean[] updateBoolean = { true, true, true };
		odo.setPosition(updatePos, updateBoolean);

	}

	public boolean doAllignRobot() {
		double initAngle = odo.getAng();
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
			if (Math.abs(odo.minimumAngleFromTo(odo.getAng(),initAngle)) > abortAngle){
				System.out.println("Aborting allignRobot");
				return false;
				
				
			}

		}
		drive.setSpeeds(0, 0);
		delay(250);
		return true;

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
		System.out.println("Number to round:"+numberToRound+  "  Result: " +result);
		return result;
	}
	
	public boolean checkIfGap()
	{
		double newX = 0;
		double newY = 0;
		double currentX = odo.getX();
		double currentY = odo.getY();
		
		double roundedX = roundToNearestMultipleOf(latchedPos[0],30);
		
		int direction = getDirection();
		switch (direction) {
		case 1: {
			newY = roundToNearestMultipleOf((currentY-distanceOfSensor), 30);
			break;

		}
		case 2: {
			newY = roundToNearestMultipleOf(currentY+(distanceOfSensor), 30);
			break;
		}
		case 3: {
			newX = roundToNearestMultipleOf((currentX-distanceOfSensor), 30);
			break;
			

		}
		case 4: {
			newX = roundToNearestMultipleOf((currentX+distanceOfSensor), 30);
			break;
		}
		}
		
		if ((newX % 120) == 0 || (newY % 120 == 0) ){
			return true;
			
		}
		
		return false;
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