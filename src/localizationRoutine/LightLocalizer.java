package localizationRoutine;


import motorController.DriveController;
import navigationController.Navigator;
import navigationController.Odometer;
import sensorController.FilteredColorPoller;

/**
 * Performs the routine which localizes the robot by detecting grid lines
 * with the color sensors
 *
 */
public class LightLocalizer {
	// Resources
	private Navigator nav;
	private Odometer odo;
	private DriveController drive;
	private FilteredColorPoller colorPoller;
	// Variables
	private static final float lineIntensity = 0.56f;
	private static final double sensorDistance = 9;
	private static final int abortAngle = 30;

	/**
	 * The constructor utilizes the robots sensors, motors and navigation system
	 * @param nav The robot's navigator
	 * @param odo The robot's odometer
	 * @param drive The robot's drive controller
	 * @param colorPoller The robot's color sensor poller
	 */
	public LightLocalizer(Navigator nav, Odometer odo, DriveController drive, FilteredColorPoller colorPoller) {
		this.nav = nav;
		this.odo = odo;
		this.drive = drive;
		this.colorPoller = colorPoller;
	}

	/**
	 * This method is responsible for performing the algorithm which will bring the robot 
	 * to 30,30 with a heading of 90 degrees
	 * 
	 */
	public void doLocalization() {
		drive.setSpeeds(drive.MEDIUM, drive.MEDIUM);
		while (!lineDetected(1) && !lineDetected(2)) {

		}
		odo.setAng(0);
		drive.setSpeeds(0, 0);
		nav.goBackwards(sensorDistance+1);
		nav.turnUp();
		drive.setSpeeds(drive.MEDIUM, drive.MEDIUM);
		while (!lineDetected(1) && !lineDetected(2)) {

		}
		odo.setAng(90);
		nav.goBackwards(sensorDistance+1);
		drive.setSpeeds(0, 0);
		double[] updatePos = { 30, 30, 90 };
		boolean[] updateBoolean = { true, true, true };
		odo.setPosition(updatePos, updateBoolean);
		nav.turnTo(0, true);

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

	

}