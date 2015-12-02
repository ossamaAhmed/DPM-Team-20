package initializationRoutine;

import java.io.IOException;

import navigationController.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import localizationRoutine.LightLocalizer;
import localizationRoutine.USLocalizer;
import motorController.ArmController;
import motorController.DriveController;
import sensorController.FilteredColorPoller;
import sensorController.FilteredUltrasonicPoller;
import wifi.StartCorner;
import wifi.Transmission;
import wifi.WifiConnection;
import FieldMap.Block;
import FieldMap.Field;
import FieldMap.Position;
import FieldMap.Robot;
import FieldMap.Zone;
import Game.Game;


public class Initializationn {
	
//	private static final String SERVER_IP = "192.168.10.200";
	private static final String SERVER_IP = "192.168.43.193";
	private static final int TEAM_NUMBER = 20;
	private final static boolean useWifi = true;
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
		USLocalizer usLoc= new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
		LightLocalizer lightLoc = new LightLocalizer(nav, odo, drive, colorPoller); 
		

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
		Sound.beep();
		arm.raiseArm(0);
		initWifi();
		
		USLocalizer myLoc= new USLocalizer(odo,usPoller,nav,drive,USLocalizer.LocalizationType.RISING_EDGE);
		myLoc.doLocalization();
		LightLocalizer myLightLoc= new LightLocalizer(nav, odo, drive, colorPoller);
		myLightLoc.doLocalization();
		nav.travelTo(45, 45);
		Robot myRobot= new Robot(new Position((30/2.0)+30,(30/2.0)+30));
		nav.turnTo(0, true);
		setStartingPositionOfRobot( myRobot, t.startingCorner.getId(), odo);
		odoC.run = false;
		//Assumes robot begins at center of 2nd diagonal tile
		Field myField= new Field(12,12,30);
		Game myGame= new Game(myRobot,myField, nav, usPoller,arm,odoC);
		setOpponentZone( myField,t.opponentHomeZoneBL_X+1,t.opponentHomeZoneBL_Y+1,t.opponentHomeZoneTR_X,t.opponentHomeZoneTR_Y);
		myGame.moveRobot(t.opponentHomeZoneBL_X, t.opponentHomeZoneBL_Y+1);
		
		
		
		// If no command line arguements, resume old init with buttons
		
		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		{
			
		}
		
		// Left Button
		if (buttonChoice == Button.ID_LEFT) {
			drive.setFloat();
			while (true){
			System.out.println("Sensor 1: " + (int)(100*colorPoller.getReadingOf(1)));
			System.out.println("Sensor 2: " + (int)(100*colorPoller.getReadingOf(2)));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}

		// Right Button
		else if (buttonChoice == Button.ID_RIGHT) {
			
			arm.raiseArm(0);
//			USLocalizer myLoc= new USLocalizer(odo,usPoller,nav,drive,USLocalizer.LocalizationType.RISING_EDGE);
//			myLoc.doLocalization();
//			LightLocalizer myLightLoc= new LightLocalizer(nav, odo, drive, colorPoller);
//			myLightLoc.doLocalization();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			nav.travelTo(15, 17);
//			nav.turnTo(0, true);
//			double[] newpos={0,0,0};
//			boolean[] newbol={true,true,true};
//			odo.setPosition(newpos, newbol);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			nav.travelTo((35/2.0), (35/2.0+4));
//			nav.turnTo(0, true);
//			double[] newpos2={45,45,0};
//			boolean[] newbol2={true,true,true};
//			odo.setPosition(newpos2, newbol2); 
//			nav.travelTo(45, 45);
//			nav.turnTo(0, true);
//			odoC.run = false;
//			//Assumes robot begins at center of 2nd diagonal tile
//			Robot myRobot= new Robot(new Position((30/2.0)+30,(30/2.0)+30));
//			Field myField= new Field(12,12,30);
//			Game myGame= new Game(myRobot,myField, nav, usPoller,arm,odoC);
//			myGame.moveRobot(5, 6);
			



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
			odoC.run = true;
			for (int i =0;i<10;i++){
			nav.travelTo(60, 0);
			nav.travelTo(60, 60);
			nav.travelTo(0, 60);
			nav.travelTo(0, 0);
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
	public static void setStartingPositionOfRobot(Robot myRobot, int id, Odometer myOdo){
		switch(id){
		case 1: myRobot.setPosition(new Position(45,45));
				double[] newpos1={45,45,0};
				boolean[] newbol1={true,true,true};
				myOdo.setPosition(newpos1, newbol1);
			break;
		case 2: myRobot.setPosition(new Position(315,45));
				double[] newpos2={315,45,90};
				boolean[] newbol2={true,true,true};
				myOdo.setPosition(newpos2, newbol2);
			break;
		case 3: myRobot.setPosition(new Position(315,315));
				double[] newpos3={315,315,180};
				boolean[] newbol3={true,true,true};
				myOdo.setPosition(newpos3, newbol3);
			break;
		case 4:
			myRobot.setPosition(new Position(45,315));
			double[] newpos4={45,315,270};
			boolean[] newbol4={true,true,true};
			myOdo.setPosition(newpos4, newbol4);
			break;
		}
	}
	public static void setOpponentZone(Field myField, int BLX,int BLY,int TRX,int TRY){
		for(int j=BLY;j<TRY+1;j++){
			for (int i=BLX;i<TRX+1;i++){
				if(i>=BLX && i<=TRX && j>=BLY&& j<=TRY)
				myField.getTile(j, i).setZoneType(Zone.OPPONENT_ZONE);
				myField.getTile(j, i).setBlock(Block.BLOCKED);
			}
		}
	}
}
