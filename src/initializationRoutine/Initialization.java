package initializationRoutine;

import java.io.IOException;
import java.util.ArrayList;

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
import FieldMap.Block;
import FieldMap.Field;
import FieldMap.Position;
import FieldMap.Robot;
import FieldMap.Tile;
import FieldMap.Zone;
import Game.Game;

/**
 * Performs the initialization routine of the robot
 *
 */
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
		Navigator nav = new Navigator(odo, drive,usPoller,colorPoller);
		OdometerCorrection odoC = new OdometerCorrection(odo, colorPoller);

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
		initWifi();
		LCD.drawString("Ready", 0, 0);
		

		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		{

		}

		// Left Button
		if (buttonChoice == Button.ID_LEFT) {
			arm.raiseArm(0);
			USLocalizer myLoc = new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
			myLoc.doLocalization();
		}

		// Right Button
		else if (buttonChoice == Button.ID_RIGHT) {
			arm.raiseArm(0);
			nav.travelTo(70, 0);
			nav.travelTo(70, 70);
			nav.travelTo(0, 70);
			nav.travelTo(0, 0);

		}

		// Up Button
		else if (buttonChoice == Button.ID_UP) {
			
		}

		// Down Button
		else if (buttonChoice == Button.ID_DOWN) {
			arm.raiseArm(0);
			LCDInfo lcd = new LCDInfo(odo, usPoller, colorPoller);
			//			USLocalizer myLoc= new USLocalizer(odo, usPoller, nav, drive, USLocalizer.LocalizationType.RISING_EDGE);
			//			myLoc.doLocalization();
			//			try {
			//				Thread.sleep(5000);
			//			} catch (InterruptedException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//			nav.travelToDiagonaly(15, 17);
			//			nav.turnTo(0, true);
			//			double[] newpos= {0,0,0};
			//			boolean[] newbol= {true,true,true};
			//			odo.setPosition(newpos, newbol);
			//			try {
			//				Thread.sleep(5000);
			//			} catch (InterruptedException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//			nav.travelToDiagonaly((35/2.0), (35/2.0)+4);
			//			nav.turnTo(0, true);
			//			try {
			//				Thread.sleep(5000);
			//			} catch (InterruptedException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//			odoC.start();
			double[] newpos2 = { (35 / 2.0) + 35, (35 / 2.0) + 35, 90 };
			boolean[] newbol2 = { true, true, true };
			odo.setPosition(newpos2, newbol2);
			Robot myRobot = new Robot(new Position((35 / 2.0) + 35, (35 / 2.0) + 35));
			Field myField = new Field(5, 3, 35);
			Game myGame = new Game(myRobot, myField,drive, nav, usPoller);
			ArrayList<Tile> myOpponentZoneTile = new ArrayList<Tile>();
			myOpponentZoneTile.add(myField.getTile(2, 2));
			myOpponentZoneTile.add(myField.getTile(1, 2));
			myOpponentZoneTile.add(myField.getTile(1, 3));
			myOpponentZoneTile.add(myField.getTile(2, 3));
			for (Tile opponentTile : myOpponentZoneTile) {
				opponentTile.setZoneType(Zone.OPPONENT_ZONE);
				opponentTile.setBlock(Block.BLOCKED);
			}
			myGame.searchZone(myOpponentZoneTile);

		}

	}

	public static void initWifi() {
		if (useWifi) {
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
