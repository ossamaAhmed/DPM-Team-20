package objectSearchRoutine;

import java.util.ArrayList;
import java.util.List;

import navigationController.*;
import sensorController.*;
import lejos.hardware.Sound;
import motorController.DriveController;

public class OLDObjectSearch {
	private Odometer odo;
	private FilteredUltrasonicPoller usPoller;
	private OLDObjectPoller objectPoller;
	private DriveController drive;
	public static float ROTATION_SPEED = 40;
	public static final float HIGH = 150;
	public static final float MEDIUM = 100;
	public static final float LOW = 50;
	public static final int angleThreshold = 5; // Ignore angle errors smaller than this
	private Navigator nav;
	private int counter;
	private int desiredX;
	private int desiredY;
	List<Double[]> blackList = new ArrayList<Double[]>();

	public OLDObjectSearch(Odometer odo, OLDObjectPoller objectPoller, FilteredUltrasonicPoller usPoller, Navigator nav, DriveController drive) {
		this.odo = odo;
		this.objectPoller = objectPoller;
		this.usPoller = usPoller;
		this.nav = nav;
		this.counter = 0;
		this.drive= drive;
	}

	public void doBoardSearch() {
		double dx = this.desiredX - odo.getX();
		double dy = this.desiredY - odo.getY();
		double distance = Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));
		nav.turnTo(0, true);
		desiredX = 60;
		desiredY = 0;
		move();

	}

	public void move() {
		
		if (usPoller.getDistance() < 0.1) {
			while (usPoller.getDistance() < 0.25) {
				drive.setSpeeds(-1*MEDIUM,-1*MEDIUM);
			}
		}

		if (isBlackListed()) {
			drive.setSpeeds(MEDIUM, HIGH);
		}

		else if (usPoller.getDistance() < 0.20) {
			doFindObjectCenter();
			doIdentifyObject();
			Double[] d = { odo.getX(), odo.getY() };
			blackList.add(d);

		} else if (getMinAngle(getNewHeading()) > angleThreshold) {
			drive.setSpeeds(MEDIUM, HIGH);
		} else if (getMinAngle(getNewHeading()) < angleThreshold) {
			drive.setSpeeds(HIGH, MEDIUM);
		} else {
			drive.setSpeeds(HIGH, HIGH);

		}

	}

	public void doFindObjectCenter() {
		double angleA, angleB;
		double startAngle = odo.getAng();
		drive.setSpeeds(-1 * LOW, LOW);
		while (usPoller.getDistance() < 0.20) { // Rotate clockwise until facing block == false
		}
		// Found one edge, Angle A
		Sound.beep();
		angleA = odo.getAng();
		nav.turnTo(startAngle, false);
		drive.setSpeeds(LOW, -1 * LOW);
		while (usPoller.getDistance() < 0.20) { // Rotate c-clockwise until facing block == false
		}
		// Found other edge, Angle B
		angleB = odo.getAng();
		Sound.beep();
		nav.turnTo(odo.getAng() - 0.5 * getAngleDistance(angleA, angleB), true);
	}

	public void doIdentifyObject() {
		// First get a proper distance, then start
		// rotating 30 deg clockwise than 60 deg c-clockwise
		int dTheta = 0;

		while (usPoller.getDistance() > objectPoller.maxDistanceThreshold) {
			drive.setSpeeds(LOW, LOW);
		}

		while (usPoller.getDistance() < objectPoller.minDistanceThreshold) {
			drive.setSpeeds(-1 * LOW, -1 * LOW);
		}

		drive.setSpeeds(0, 0);
		while (objectPoller.identifyObject() == 0) {
			// If the sensor becomes too close while rotating, back up
			if (usPoller.getDistance() < objectPoller.minDistanceThreshold) {
				drive.setSpeeds(-1 * LOW, -1 * LOW);
			} else {
				drive.setSpeeds(0, 0);
			}

			// Turn by -30, then by 60. If still nothing
			// adjust a little bit and repeat
			if (dTheta > -30 && dTheta <= 0) {
				nav.turnTo(odo.getAng() - 5, false);
				dTheta -= 5;
			}

			else if (dTheta == -30) {
				nav.turnTo(odo.getAng() + 35, false);
				dTheta = 5;
			}

			else if (dTheta >= 5) {
				nav.turnTo(odo.getAng() + 5, false);
			}

			else if (dTheta == 30) {
				nav.turnTo(odo.getAng() - 30, false);
				drive.setSpeeds(-1*LOW, -1*LOW);
				while (usPoller.getDistance() < 0.15){
				}
				nav.turnTo(odo.getAng()+90, true);
				nav.goForward(5);
				nav.turnTo(odo.getAng()-90, true);
				dTheta = 0;
			}
		}

		if (objectPoller.identifyObject() == 1) {
			Sound.beep();
		}

		else if (objectPoller.identifyObject() == 2) {
			Sound.beep();
			Sound.buzz();
		}

	}

	public double getWallUsValue() {
		return 0.40 * (1 + (0.181818 * Math.abs(Math.sin(Math.toRadians(2 * odo.getAng() + 180)))));

	}

	public boolean objectSeen() {
		if (usPoller.getDistance() < getWallUsValue()) {
			Sound.beep();
			return true;
		}
		return false;
	}

	public int getCounter() {
		return counter;
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

	public double getMinAngle(double theta) {

		double minAngle = theta - odo.getAng();
		// Find minimum angle
		if (minAngle >= (-1 * Math.toDegrees(Math.PI)) && minAngle <= (Math.toDegrees(Math.PI))) {
			// Leave d as is
		} else if (minAngle < (-1 * Math.toDegrees(Math.PI))) {
			minAngle = minAngle + (Math.toDegrees(Math.PI) * 2);
		} else if (minAngle > (Math.toDegrees(Math.PI))) {
			minAngle = minAngle - (2 * Math.toDegrees(Math.PI));
		}

		return minAngle;
	}

	public double getNewHeading() {
		double dx = this.desiredX - odo.getX();
		double dy = this.desiredY - odo.getY();
		double theta = 0;
		if (dx == 0 && dy > 0) {
			theta = Math.toDegrees(Math.PI / 2);
		} else if (dx == 0 && dy < 0) {
			theta = -1 * Math.toDegrees(Math.PI / 2);
		}

		else if (dx > 0) {
			theta = Math.toDegrees(Math.atan(dy / dx));

		}

		else if (dx < 0 && dy >= 0) {
			theta = Math.toDegrees(Math.atan(dy / dx)) + (Math.toDegrees(Math.PI));

		}

		else if (dx < 0 && dy < 0) {
			theta = Math.toDegrees(Math.atan(dy / dx)) - (Math.toDegrees(Math.PI));

		}
		return theta;
	}

	public boolean isBlackListed() {
		boolean result = false;
		if (blackList.isEmpty())
			return result;
		for (int i = 0; i < blackList.size(); i++) {
			// Check if the current x and y is within a radius of a blacklisted co-ord
			Double[] pos = blackList.get(i);
			double x = pos[0];
			double y = pos[1];

			double dx = Math.abs(x - odo.getX());
			double dy = Math.abs(y - odo.getY());
			double distance = Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));

			if (distance < 20) {
				result = true;
				return result;
			}
		}
		return result;
	}

}
