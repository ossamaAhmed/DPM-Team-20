package initializationRoutine;

import java.io.IOException;

import navigationController.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import localizationRoutine.USLocalizer;
import motorController.ArmController;
import motorController.DriveController;
import sensorController.FilteredColorPoller;
import sensorController.FilteredUltrasonicPoller;
import wifi.StartCorner;
import wifi.Transmission;
import wifi.WifiConnection;
import FieldMap.Field;
import FieldMap.Position;
import FieldMap.Robot;
import Game.Game;


public class Initialization {
	
//	private static final String SERVER_IP = "192.168.10.200";
	private static final String SERVER_IP = "192.168.43.83";
	private static final int TEAM_NUMBER = 20;

	public static void main(String[] args) {

		int buttonChoice = 0;
		FilteredUltrasonicPoller usPoller = new FilteredUltrasonicPoller();
		FilteredColorPoller colorPoller = new FilteredColorPoller();
		ArmController arm = new ArmController();
		DriveController drive = new DriveController();
		Odometer odo = new Odometer(drive);
		Navigator nav = new Navigator(odo,drive,colorPoller);
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
		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			System.out.println("Connection failed");
		}
		Transmission t = conn.getTransmission();
		int homeZoneBL_X = 0;
		int homeZoneBL_Y = 0;
		int opponentHomeZoneBL_X = 0;
		int opponentHomeZoneBL_Y =0;
		int dropZone_X =0;
		int dropZone_Y = 0;
		int flagType =0;
		int	opponentFlagType = t.opponentFlagType;
		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);

		} else {
			StartCorner corner = t.startingCorner;
			 homeZoneBL_X = t.homeZoneBL_X;
			 homeZoneBL_Y = t.homeZoneBL_Y;
			 opponentHomeZoneBL_X = t.opponentHomeZoneBL_X;
			 opponentHomeZoneBL_Y = t.opponentHomeZoneBL_Y;
			 dropZone_X = t.dropZone_X;
			 dropZone_Y = t.dropZone_Y;
			 flagType = t.flagType;
			 opponentFlagType = t.opponentFlagType;
		
			// print out the transmission information
			conn.printTransmission();
		}
		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		{
			
		}
		//wifi setup 
		
		// Left Button
		if (buttonChoice == Button.ID_LEFT) {
			arm.raiseArm(0);
			USLocalizer myLoc= new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
			myLoc.doLocalization();
		}

		// Right Button
		else if (buttonChoice == Button.ID_RIGHT) {
			arm.raiseArm(0);
			nav.travelTo(70, 0);
			nav.travelTo(70, 70);
			nav.travelTo(0,70);
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
			arm.raiseArm(0);
			LCDInfo lcd = new LCDInfo(odo,usPoller,colorPoller);
			USLocalizer myLoc= new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
			myLoc.doLocalization();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nav.travelToDiagonaly(15, 17);
			nav.turnTo(0, true);
			double[] newpos= {0,0,0};
			boolean[] newbol= {true,true,true};
			odo.setPosition(newpos, newbol);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nav.travelToDiagonaly((35/2.0), (35/2.0)+4);
			nav.turnTo(0, true);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			odoC.start();
			double[] newpos2= {(35/2.0)+35,(35/2.0)+35,0};
			boolean[] newbol2= {true,true,true};
			odo.setPosition(newpos2, newbol2);
			Robot myRobot= new Robot(new Position((35/2.0)+35,(35/2.0)+35));
			Field myField= new Field(8,8,35);
			Game myGame= new Game(myRobot,myField, nav, usPoller);
			myGame.moveRobot(opponentHomeZoneBL_X, opponentHomeZoneBL_Y);

		}

	}
}
