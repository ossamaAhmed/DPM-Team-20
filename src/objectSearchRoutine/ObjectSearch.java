package objectSearchRoutine;

import java.util.ArrayList;
import java.util.List;

import motorController.*;
import navigationController.*;
import sensorController.*;

public class ObjectSearch {
	//Resources
	private Odometer odo;
	private Navigator nav;
	private ObjectIdentifier objectID;
	private FilteredUltrasonicPoller usSensor;
	private FilteredColorPoller colorPoller;
	private ArmController arm;
	private DriveController drive;
	private int blX, blY, trX, trY;

	//Variables
	private final float detectionThreshold = 0.3f;
	private final float secondCheckThreshold = 0.15f;
	private final float finalDistanceThreshold = 0.05f;
	private List<Float> flagColorList = new ArrayList<>();

	public ObjectSearch(Odometer odo, Navigator nav, ObjectIdentifier objectID, FilteredUltrasonicPoller usSensor, FilteredColorPoller colorPoller, ArmController arm, DriveController drive) {
		this.odo = odo;
		this.nav = nav;
		this.objectID = objectID;
		this.usSensor = usSensor;
		this.colorPoller = colorPoller;
		this.arm = arm;
		this.drive = drive;

	}

	public void doObjectSearch() {
		double startingAngle = 0;
		double startingAngleChange = 30;
		int flagFound = searchAtAngle(startingAngle);
		while (flagFound == 0){
			startingAngle += startingAngleChange;
			flagFound= searchAtAngle(startingAngle);
		}
		arm.captureObject();

	}

	public int searchAtAngle(double startingAngle) {
		// Return 0 if: not flag
		// Return 1 if : flag found
		boolean foundObject = false;

		// Start at the center facing a given angle
		if (startingAngle == 0)
			nav.travelTo(0.5 * (blX + trX), 0.5 * (blY + trY));
		else
			nav.travelToBackwards(0.5 * (blX + trX), 0.5 * (blY + trY));
		nav.turnTo(startingAngle, true);
		// Begin sweeping clockwise until an object is detected
		doSweep();
		// Turn a little more, and then check again for the object
		nav.turnTo(odo.getAng() + 30, true);
		foundObject = checkForObject(0.8f * detectionThreshold);
		if (foundObject == false) return 0;
		// At this point we know there is an object in front of us, go forward 50%
		// of the distance
		float distance = usSensor.getDistance();
		nav.goForward(0.5f * distance);
		// Check once again if there is an object 
		checkForObject(secondCheckThreshold);
		if (foundObject == false) return 0;
		// Perform the final approach, timeout if robot travels too far
		doApproach(0.35 * distance);
		//Check the object color
		if (identifyObject()){
			return 1;
		}
		else {
			return 0;
		}

	}

	public void doSweep() {
		drive.setSpeeds(drive.SLOW, -1 * drive.SLOW);
		while (usSensor.getDistance() > detectionThreshold) {
		}
		drive.setSpeeds(0, 0);
		System.out.println("doSweep found an object");
	}

	public void doApproach(double distanceTimeout) {
		double[] startCoord = { odo.getX(), odo.getY() };
		double[] currentCoord = new double[2];
		double distanceTravelled = 0;

		drive.setSpeeds(drive.SLOW, drive.SLOW);
		while (usSensor.getDistance() > finalDistanceThreshold && distanceTravelled < distanceTimeout) {
			currentCoord[0] = odo.getX();
			currentCoord[1] = odo.getY();
			distanceTravelled = odo.getCoordinateDistance(startCoord, currentCoord);
		}
		drive.setSpeeds(0, 0);

	}

	public boolean checkForObject(float distance) {
		sleep(500);
		if (usSensor.getDistance() <= distance) {
			return true;

		}
		return false;

	}

	public boolean identifyObject() {
		for (int i = 0; i < 5; i++) {
			sleep(100);
			if (flagColorList.contains(colorPoller.getReadingOf(0))) return true;
		}
		return false;
	}

	public void setCoordinates(int blX, int blY, int trX, int trY) {
		this.blX = blX;
		this.blY = blY;
		this.trX = trX;
		this.trY = trY;

	}
	
	public void addFlagColor(float color){
		flagColorList.add(color);
	}

	public void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
