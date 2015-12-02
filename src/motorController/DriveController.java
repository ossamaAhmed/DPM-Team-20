package motorController;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is responsible for providing methods which control the driving motors
 * @author tcv
 *
 */
public class DriveController {
	public final int SLOW = 75;
	public final static int MEDIUM = 100;
	public final static int FAST = 225;
	private final EV3LargeRegulatedMotor[] drivingMotor = { new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A")), new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D")) };

	public DriveController() {
		drivingMotor[0].synchronizeWith(new EV3LargeRegulatedMotor[] { drivingMotor[1] });
		drivingMotor[0].resetTachoCount();
		drivingMotor[1].resetTachoCount();

	}

	/**
	 * This method sets the desired acceleration for both motors
	 * @param acceleration The desired acceleration
	 */
	public void setAcceleration(int acceleration) {
		drivingMotor[0].setAcceleration(acceleration);
		drivingMotor[1].setAcceleration(acceleration);
	}

	/**
	 * This method sets the desired acceleration for the indexed motor
	 * @param acceleration The desired acceleration
	 * @param index The desired motor to set
	 */
	public void setAcceleration(int acceleration, int index) {
		switch (index) {
		case 0: {
			drivingMotor[0].setAcceleration(acceleration);
		}
		case 1: {
			drivingMotor[1].setAcceleration(acceleration);
		}
		}
	}

	/**
	 * This method sets the right and left motors to a desired speed
	 * (forwards and backwards)
	 * @param lSpd
	 * The desired left motor speed
	 * @param rSpd
	 * The desired right motor speed
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		if (drivingMotor[0].getSpeed() == lSpd && drivingMotor[1].getSpeed() == rSpd) return;
		drivingMotor[0].startSynchronization();
		this.drivingMotor[0].setSpeed(lSpd);
		this.drivingMotor[1].setSpeed(rSpd);
		if (lSpd < 0)
			this.drivingMotor[0].backward();
		else
			this.drivingMotor[0].forward();
		if (rSpd < 0)
			this.drivingMotor[1].backward();
		else
			this.drivingMotor[1].forward();
		drivingMotor[0].endSynchronization();
	}

	/**
	 * This method floats both the motors
	 */
	public void setFloat() {
		this.drivingMotor[0].stop();
		this.drivingMotor[1].stop();
		this.drivingMotor[0].flt(true);
		this.drivingMotor[1].flt(true);
	}

	/**
	 * Getter method for the driving motors
	 * @return An array containing the driving motors
	 */
	public EV3LargeRegulatedMotor[] getMotors() {
		return drivingMotor;
	}

}
