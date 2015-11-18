package motorController;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;



/**
 * This class contains methods which give control to all of the arm motors
 * @author rick
 *
 */
public class ArmController {
	

	// Resources
	private static final EV3LargeRegulatedMotor[] armMotor = { new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C")), new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B")) };
	//
	
	// Adjustable Variables
	private static final int armAngle[] = {135,135}; // The required rotation to raise or lower the arm
	private static final int captureAngle[] = {135,135}; // The required rotation to raise or lower the arm
	//
	

	public ArmController(){
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
	
	public void captureObject(){
		raiseArmTo(0,captureAngle[0]);
		raiseArmTo(1,captureAngle[1]);

	}
}
