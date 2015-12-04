package motorController;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;



/**
 * This class contains methods which give control to all of the arm motors
 * @author rick
 *
 */
public class ArmController {
	

	// Resources
	private static final EV3LargeRegulatedMotor[] armMotor = { new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B")), new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C")) };
	//
	
	// Adjustable Variables
	private static final int armAngleL[] = {110,0}; // The required rotation to raise or lower the arm
	private static final int captureAngle[] = {110,-90}; // The required rotation to raise or lower the arm
//	private static final int close[] = {-110,0};
	//
	

	public ArmController(){


		
		}
	
	/**
	 * This method raises a given arm to zero degrees
	 * which corresponds to the raised position
	 * @param index The arm that will be raised
	 */
	public void raiseArm(int index){
		armMotor[index].setAcceleration(350);
		armMotor[index].rotateTo(0);
	}
	
	/**
	 * This method lowers a given arm to a predefined angle
	 * corresponding to a lowered position
	 * @param index The arm that will be lowered
	 */
	public void dropArm(int index){
		armMotor[index].setAcceleration(500);
		armMotor[index].rotateTo(armAngleL[index]);
		
	}
	
	/**
	 * This method raises or lowers a given arm to a given angle
	 * @param index The arm that will be raised or lowered
	 * @param angle The angle to raise or lower the arm to
	 */
	public void raiseArmTo(int index, int angle){
		armMotor[index].setAcceleration(250);
		armMotor[index].rotateTo(angle);
	}
	
	/**
	 * This method changes the arms to predefined angles to capture an object
	 */
	public void captureObject(){
		raiseArmTo(1,captureAngle[1]);
		Sound.beep();
		raiseArmTo(0,captureAngle[0]);
		Sound.beep();
		raiseArmTo(1, 100);
		Sound.beep();
		raiseArm(0);
		Sound.beep();


	}
}