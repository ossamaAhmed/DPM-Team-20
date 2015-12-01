package localizationRoutine;

import motorController.DriveController;
import navigationController.Navigator;
import navigationController.Odometer;
import sensorController.FilteredColorPoller;

public class LightLocalizer {
	// Resources
	private Navigator nav;
	private Odometer odo;
	private DriveController drive;
	private FilteredColorPoller colorPoller;
	// Variables
	private static final float lineIntensity = 0.65f;
	private static final double sensorDistance = 9;
	private static final int abortAngle = 30;

	public LightLocalizer(Navigator nav,Odometer odo, DriveController drive, FilteredColorPoller colorPoller) {
		this.nav = nav;
		this.odo = odo;
		this.drive = drive;
		this.colorPoller = colorPoller;
	}

	public void doLocalization() {
		drive.setSpeeds(drive.FAST, drive.FAST);
		while (!lineDetected(1) && !lineDetected(2)) {

		}
		drive.setSpeeds(0, 0);
		nav.goBackwards(sensorDistance);
		nav.turnUp();
		drive.setSpeeds(drive.FAST, drive.FAST);
		while (!lineDetected(1) && !lineDetected(2)) {

		}
		nav.goBackwards(sensorDistance);
		drive.setSpeeds(0, 0);
		double[] updatePos = { 30,30,90};
		boolean[] updateBoolean = { true, true, true };
		odo.setPosition(updatePos, updateBoolean);

	}

	public boolean lineDetected(int sensorNumber) {
		if (colorPoller.getReadingOf(sensorNumber) < lineIntensity) {
			return true;
		}

		else {
			return false;
		}

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
		return true;

		// While either sensor is not on the line, stop the motor which is on the line
		// and wait until both sensors are on the line before returning

	}

}
