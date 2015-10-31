package ev3Localization;

import ev3Drive.Navigator;
import ev3Drive.Odometer;
import ev3Sensors.FilteredUltrasonicPoller;
import lejos.hardware.Sound;

public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	// Variables
	public static float ROTATION_SPEED = 40;
	private int cornerAngle = 235;
	private static final double angleThreshold = 125; // Angle A and B must differ by at least this much
	private static final double wallAngleThreshold = 25; // Used to avoid the localization latching the same angle twice

	private Odometer odo;
	private LocalizationType locType;
	private Navigator nav;
	private FilteredUltrasonicPoller usPoller;

	//

	public USLocalizer(Odometer odo, FilteredUltrasonicPoller usPoller, LocalizationType locType) {
		this.odo = odo;
		this.locType = locType;
		this.nav = new Navigator(odo);
		this.usPoller = usPoller;
		nav.start();
	}

	public void doLocalization() {
		double[] angles = new double[3]; // Angle A = 0, Angle B = 1, New Heading = 2

		if (locType == LocalizationType.FALLING_EDGE) {
			// newHeading = doFallingEdge();
		} else {
			angles = doRisingEdge();
			if (getAngleDistance(angles[0], angles[1]) < angleThreshold) {
				// The robot is latching angles of a block -> turn around 180 to face wall
				// afterwards, redo the rising edge
				Sound.buzz();
				nav.turnTo(odo.getAng()+180, false);
				angles = doRisingEdge();

			}
			odo.setAng(angles[2]);

		}
		nav.turnTo(0, true);
		nav.setSpeeds(0, 0);
	}

	private int facingWall() {
		// 1 = True, 0 = False , 2 = Don't know
		float d = usPoller.getDistance();
		int facingWall = 2;
		if (d < 0.40)
			facingWall = 1;
		else if (d > 0.50)
			facingWall = 0;
		return facingWall;
	}

	private double[] doRisingEdge() {
		double[] angles = new double[3];
		nav.setSpeeds(ROTATION_SPEED, -1 * ROTATION_SPEED);
		while (facingWall() != 1) { // Rotate clockwise until facing wall == true
		}
		while (facingWall() != 0) { // Rotate clockwise until facing wall == false
		}
		angles[0] = odo.getAng(); // First wall detected, this is angle A. Switch directions
		Sound.beep();
		nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
		while (facingWall() != 1 || getAngleDistance(angles[0], odo.getAng()) < wallAngleThreshold) { // Rotate c-clockwise until facing wall == true
		}
		while (facingWall() != 0) { // Rotate c-clockwise until facing wall == false
		}
		angles[1] = odo.getAng(); // Second wall detected, this is angle B.
		Sound.beep();
		angles[2] = (cornerAngle + (getAngleDistance(angles[0], angles[1]) / 2)); // Fix Heading

		return angles;
	}

	private double[] doFallingEdge() {
		double angleA, angleB;
		double[] angles = new double[3];
		nav.setSpeeds(ROTATION_SPEED, -1 * ROTATION_SPEED);
		while (facingWall() != 0) { // read: Rotate clockwise until facing wall == false
		}
		while (facingWall() != 1) { // Rotate clockwise until facing wall == true
		}
		angleA = odo.getAng(); // First wall detected, this is angle A. Switch directions
		Sound.beep();
		nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
		while (facingWall() != 0 || getAngleDistance(angleA, odo.getAng()) < wallAngleThreshold) { // Rotate c-clockwise until facing wall == false
		}
		while (facingWall() != 1) { // Rotate c-clockwise until facing wall == true
		}
		angleB = odo.getAng(); // Second wall detected, this is angle B.
		Sound.beep();
		odo.setAng(cornerAngle - (getAngleDistance(angleA, angleB) / 2)); // Fix heading
		nav.setSpeeds(0, 0);
		return angles;

	}

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
