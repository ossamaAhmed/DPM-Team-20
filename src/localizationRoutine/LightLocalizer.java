package localizationRoutine;

import navigationController.*;
import navigationController.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import motorController.DriveController;

/**
 * This class is responsible completing light localization and correcting
 * the x,y and theta position
 * @author rick
 *
 */
public class LightLocalizer {
	// Resources
	private Odometer odo;
	private SampleProvider colorSource;
	private float[] colorData;
	private Navigator nav;
	private DriveController drive;

	// Variables
	private double d = 9.5; // Distance from centre of rotation to light sensor
	private int angleErrorThreshold = 15; // Lines must be at least this far apart to be considered distinct
	private double lineDetectionThreshold = 0.4; // A color reading below this indicates a line
	private int xStart = 12;
	private int yStart = 12;
	public static float ROTATION_SPEED = 75;
	private double intersectionAngles[] = { 0, 0, 0, 0, 0 };

	/**
	 * @param odo The odometer to be updated and used for localization logic
	 * @param colorSource The color sensor to be polled for data
	 * @param colorData The data from the polled sensor
	 */
	public LightLocalizer(Odometer odo, SampleProvider colorSource, float[] colorData, Navigator nav, DriveController drive) {
		this.odo = odo;
		this.colorSource = colorSource;
		this.colorData = colorData;
		this.nav = nav;
		this.drive = drive;
	}

	/**
	 * This method executes the light localization algorithim
	 * and then updates the x,y, and theta values afterwards
	 */
	public void doLocalization() {
		// Travel to starting position and face south
		//nav.travelTo(xStart, yStart);
		nav.turnTo(230, true);

		// Begin counter-clockwise turn
		drive.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);

		// Record the angle of 4 intersections
		int i = 1;
		while (i < 5) {
			colorSource.fetchSample(colorData, 0);
			// If a line is detected AND the reading is distinct to ensure we don't double count lines
			if (colorData[0] < lineDetectionThreshold && getAngleDistance(intersectionAngles[i - 1], odo.getAng()) > angleErrorThreshold) {
				Sound.beep();
				intersectionAngles[i] = odo.getAng();
				i++;
			}
		}
		double newX = -1 * d * Math.cos(Math.toRadians(getAngleDistance(intersectionAngles[1], intersectionAngles[3])) * 0.5);
		double newY = -1 * d * Math.cos(Math.toRadians(getAngleDistance(intersectionAngles[2], intersectionAngles[4])) * 0.5);
		double newTheta = 180+225- 0.5 * getAngleDistance(intersectionAngles[1], intersectionAngles[4]);
		double newPosition[] = { newX, newY, newTheta };
		boolean update[] = { true, true, true };
		odo.setPosition(newPosition, update);

		// Travel to 0,0,0 and stop
		nav.travelTo(0, 0);
		nav.turnTo(0, true);
		drive.setSpeeds(0, 0);

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

}
