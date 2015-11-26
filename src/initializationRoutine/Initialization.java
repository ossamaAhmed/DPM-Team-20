package initializationRoutine;

import java.io.IOException;

import navigationController.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
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
	private final static boolean useWifi = false;
	private static WifiConnection conn = null;
	private static Transmission t = null;

	public static void main(String[] args) {

		int buttonChoice = 0;
		FilteredUltrasonicPoller usPoller = new FilteredUltrasonicPoller();
		FilteredColorPoller colorPoller = new FilteredColorPoller();
		ArmController arm = new ArmController();
		DriveController drive = new DriveController();
		Odometer odo = new Odometer(drive);
		OdometerCorrection odoC = new OdometerCorrection(odo,colorPoller,drive);
		Navigator nav = new Navigator(odo,odoC,drive,colorPoller);
		

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
		initWifi();
		
		// New Command Line Initialization from console
		// Performs all given commands in order
		if (args.length != 0) {
			  for (String s: args) {
				  switch (s){
					case "squareDrive":{
						arm.raiseArm(0);
						nav.travelTo(70, 0);
						nav.travelTo(70, 70);
						nav.travelTo(0,70);
						nav.travelTo(0,0);
						
					}
					case "localize": {
						arm.raiseArm(0);
						USLocalizer myLoc= new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
						myLoc.doLocalization();			
					}
					
					case "navToZone": {
						//Assumes robot begins at center of 2nd diagonal tile
						double[] newpos2= {(35/2.0)+35,(35/2.0)+35,0};
						boolean[] newbol2= {true,true,true};
						odo.setPosition(newpos2, newbol2);
						Robot myRobot= new Robot(new Position((35/2.0)+35,(35/2.0)+35));
						Field myField= new Field(8,8,35);
						Game myGame= new Game(myRobot,myField, nav, usPoller);
						myGame.moveRobot(t.opponentHomeZoneBL_X, t.opponentHomeZoneBL_Y);
						
					}
					
					case "travelTo": {
						arm.raiseArm(0);
						double x = 17.5;
						double y = 17.5;
						nav.travelTo(x, y);
					}
					
					case "odoC": {
						odoC.run=true;
					}
					
				}
		        }
		}
		
		
		// If no command line arguements, resume old init with buttons
		else {
		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		{
			
		}
		
		// Left Button
		if (buttonChoice == Button.ID_LEFT) {
			arm.raiseArm(0);
			USLocalizer myLoc= new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
			myLoc.doLocalization();
		}

		// Right Button
		else if (buttonChoice == Button.ID_RIGHT) {
			arm.raiseArm(0);
			odoC.run = true;
			nav.travelTo(70, 0);
			nav.travelTo(70, 70);
			nav.travelTo(0,70);
			nav.travelTo(0,0);
			

		}

		// Up Button
		else if (buttonChoice == Button.ID_UP) {
			LCDInfo lcd = new LCDInfo(odo, usPoller, colorPoller);
			drive.setFloat();
			odoC.run=true;
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
			nav.travelTo(15, 17);
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
			nav.travelTo((35/2.0), (35/2.0)+4);
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
			myGame.moveRobot(t.opponentHomeZoneBL_X, t.opponentHomeZoneBL_Y);

		}
		}

	}
	
	public static void initWifi()	{
		if (useWifi){
			conn = null;
			try {
				conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
			} catch (IOException e) {
				System.out.println("Connection failed");
			}
			t = conn.getTransmission();
			if (t == null) {
				System.out.println("Failed to receive transmission");
				Sound.buzz();
				System.exit(0);

			} 
			}
		
	}
}
