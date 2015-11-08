package ev3Arm;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class contains methods which give control to all of the arm motors
 * @author rick
 *
 */
public class ArmController {

	// Resources
	private EV3LargeRegulatedMotor[] armMotor;
	//
	
	// Adjustable Variables
	/**
	 * An array of degree angles corresponding to the respective amount of
	 * rotation necessary to drop the arm
	 */
	private static final int armAngle[] = {135,135}; // The required rotation to raise or lower the arm
	//
	
	/**
	 * The constructor of ArmController takes an array of EV3LargeRegulatedMotor's
	 * @param armMotor is an array of EV3LargeRegulatedMotor's controlling the arms 
	 */
	public ArmController(EV3LargeRegulatedMotor[] armMotor){
		this.armMotor = armMotor;
		}
	
	/**
	 * This method raises a given arm to zero degrees
	 * which corresponds to the raised position
	 * @param index The arm that will be raised
	 */
	public void raiseArm(int index){
		armMotor[index].rotateTo(0);
	}
	
	/**
	 * This method lowers a given arm to a predefined angle
	 * corresponding to a lowered position
	 * @param index The arm that will be lowered
	 */
	public void dropArm(int index){
		armMotor[index].rotate(armAngle[index]);
		
	}
	
	/**
	 * This method raises or lowers a given arm to a given angle
	 * @param index The arm that will be raised or lowered
	 * @param angle The angle to raise or lower the arm to
	 */
	public void raiseArmTo(int index, int angle){
		armMotor[index].rotate(angle);
	}

}
