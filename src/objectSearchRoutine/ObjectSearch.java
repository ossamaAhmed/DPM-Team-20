package objectSearchRoutine;

import motorController.*;
import navigationController.*;
import sensorController.*;

public class ObjectSearch {
	//Resources
	private Odometer odo;
	private Navigator nav;
	private ObjectIdentifier objectID;
	private FilteredUltrasonicPoller usSensor;
	private ArmController arm;
	private DriveController drive;
	private int blX,blY,trX,trY;
	
	//Variables
	private final float detectionThreshold = 0.3f;
	private final float secondCheckThreshold = 0.15f;
	private final float finalDistanceThreshold = 0.05f;
	
	public  ObjectSearch(Odometer odo, Navigator nav, ObjectIdentifier objectID, FilteredUltrasonicPoller usSensor, ArmController arm, DriveController drive   ){
		this.odo = odo;
		this.nav = nav;
		this.objectID = objectID;
		this.usSensor = usSensor;
		this.arm = arm;
		this.drive = drive;
		
	}
	
	public void doObjectSearch(double startingAngle){
		// Start at the center facing a given angle
		if (startingAngle==0) nav.travelTo(0.5*(blX+trX), 0.5*(blY+trY));
		else nav.travelToBackwards(0.5*(blX+trX), 0.5*(blY+trY));
		nav.turnTo(startingAngle, true);
		// Begin sweeping clockwise until an object is detected
		doSweep();
		// Turn a little more, and then check again for the object
		nav.turnTo(odo.getAng()+30, true);
		checkForObject(0.8f*detectionThreshold);
		// At this point we know there is an object in front of us, go forward 70%
		// of the distance
		float distance = usSensor.getDistance();
		nav.goForward(0.7f*distance);
		// Check once again if there is an object 
		checkForObject(secondCheckThreshold);
		// Perform the final approach
		
		
		
		
	}
	
	public void doSweep(){
		drive.setSpeeds(drive.SLOW, -1*drive.SLOW);
		while (usSensor.getDistance() > detectionThreshold){		
		}
		drive.setSpeeds(0, 0);
		System.out.println("doSweep found an object");
	}
	
	public void doApproach() {
		drive.setSpeeds(drive.SLOW,drive.SLOW);
		while (usSensor.getDistance() > finalDistanceThreshold){		
		}
		
	}
	
	public boolean checkForObject(float distance){
		sleep(500);
		if (usSensor.getDistance() <= distance){
			return true;
			
		}
		return false;
		
		
	}
	
	public void setCoordinates(int blX, int blY, int trX, int trY){
		this.blX=blX;
		this.blY=blY;
		this.trX=trX;
		this.trY=trY;
		
		
		
	}
	
	public void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
