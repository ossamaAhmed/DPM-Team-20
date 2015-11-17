package initializationRoutine;

import navigationController.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import motorController.ArmController;
import motorController.DriveController;
import sensorController.FilteredColorPoller;
import sensorController.FilteredUltrasonicPoller;
import FieldMap.Field;
import FieldMap.Position;
import FieldMap.Robot;
import Game.Game;


public class Initialization {


	public static void main(String[] args) {

		int buttonChoice = 0;
		FilteredUltrasonicPoller usPoller = new FilteredUltrasonicPoller();
		FilteredColorPoller colorPoller = new FilteredColorPoller();
		ArmController arm = new ArmController();
		DriveController drive = new DriveController();
		Odometer odo = new Odometer(drive);
		Navigator nav = new Navigator(odo,drive);
		OdometerCorrection odoC = new OdometerCorrection(odo,colorPoller);

		// User Interface
		(new Thread() {
			@Override
			public void run() {
				while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
				}
				System.exit(0);
			}
		}).start();

		TextLCD LCD = LocalEV3.get().getTextLCD();
		LCD.drawString("Ready", 0, 0);

		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		{
			
		}

		// Left Button
		if (buttonChoice == Button.ID_LEFT) {

		}

		// Right Button
		else if (buttonChoice == Button.ID_RIGHT) {
			nav.travelTo(60, 0);
			nav.travelTo(60, 60);
			nav.travelTo(0,60);
			nav.travelTo(0,0);
			

		}

		// Up Button
		else if (buttonChoice == Button.ID_UP) {
			LCDInfo lcd = new LCDInfo(odo, usPoller, colorPoller);
			drive.setFloat();
			odoC.start();
			// Press enter to set both arms to zero, press up to set both arms to armAngle
			while (buttonChoice != Button.ID_ENTER) {

			}
			// Raise both arm 0 and 1 to 0 degrees
//			arm.raiseArm(0);
//			arm.raiseArm(1);
//			while (buttonChoice != Button.ID_UP) {
//
//			}
//			// Drop both arm 0 and 1 to armAngle
//			arm.dropArm(0);
//			arm.dropArm(1);
		}

		// Down Button
		else if (buttonChoice == Button.ID_DOWN) {
			LCDInfo lcd = new LCDInfo(odo,usPoller,colorPoller);
			double[] newpos= {15,15,0};
			boolean[] newbol= {true,true,true};
			odo.setPosition(newpos, newbol);
			Robot myRobot= new Robot(new Position(15,15));
			Field myField= new Field(3,12,30);
			Game myGame= new Game(myRobot,myField, nav, usPoller);
			myGame.moveRobot(2, 5);

		}

	}
}
