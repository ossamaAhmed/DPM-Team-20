package ev3Arm;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class ArmController {

	// Resources
	private EV3LargeRegulatedMotor[] armMotor;
	//
	
	// Adjustable Variables
	private static final int armAngle[] = {135,135}; // The required rotation to raise or lower the arm
	//
	
	public ArmController(EV3LargeRegulatedMotor[] armMotor){
		this.armMotor = armMotor;
		}
	
	public void raiseArm(int index){
		armMotor[index].rotateTo(0);
	}
	
	public void dropArm(int index){
		armMotor[index].rotate(armAngle[index]);
		
	}
	
	public void raiseArmTo(int index, int angle){
		armMotor[index].rotate(angle);
	}

}
