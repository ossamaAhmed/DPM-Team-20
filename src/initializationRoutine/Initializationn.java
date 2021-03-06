package initializationRoutine;

import java.io.IOException;
import java.util.ArrayList;

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
import FieldMap.Tile;
import FieldMap.Zone;
import Game.Game;


/**
 * The main execution class, which initializes all the routines and controllers
 * in addition to constructing emulated game field and starting the robot.
 *
 */
public class Initializationn {
	
	private static final String SERVER_IP = "192.168.10.200";
	//private static final String SERVER_IP = "192.168.10.42";
	private static final int TEAM_NUMBER = 20;
	private final static boolean useWifi = true;
	private static WifiConnection conn = null;
	private static Transmission t = null;

	/**
	 * Main execution thread, initializes all the routines and controllers and then
	 * awaits the transmission before starting the robot.
	 */
	public static void main(String[] args) {

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
		myField.xHomeZone= t.dropZone_X+1;
		myField.yHomeZone= t.dropZone_Y+1;
		Game myGame= new Game(myRobot,myField, nav, usPoller,arm,odoC,colorPoller, t.flagType);
		ArrayList<Tile> mySuspectedTiles= setOpponentZone( myField,t.opponentHomeZoneBL_X+1,t.opponentHomeZoneBL_Y+1,t.opponentHomeZoneTR_X,t.opponentHomeZoneTR_Y);
		setHomeZone( myField, t.homeZoneBL_X+1, t.homeZoneBL_Y+1, t.homeZoneTR_X,t.homeZoneTR_Y);
		myGame.searchZone(mySuspectedTiles);
		}


	
	
	/**
	 * This method waits until a transmission from the server is received
	 * and then stores the data.
	 * 
	 */
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
	/**
	 * Set the the position of the robot depending on which starting corner it received.
	 * @param myRobot The robot to update
	 * @param id The starting corner ID
	 * @param myOdo The odometer to update
	 */
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
	/**
	 * This method blocks all the tiles contained in the opponent zone
	 * @param myField The field to block the tiles in
	 * @param BLX The bottom left X coordinate
	 * @param BLY The bottom left Y coordinate
	 * @param TRX The top right X coordinate
	 * @param TRY The top right Y coordinate
	 * @return The array list of opponent zone tiles
	 */
	public static ArrayList<Tile> setOpponentZone(Field myField, int BLX,int BLY,int TRX,int TRY){
		ArrayList<Tile> suspectedTiles= new ArrayList<Tile>();
		for(int j=BLY;j<TRY+1;j++){
			for (int i=BLX;i<TRX+1;i++){
				if(i>=BLX && i<=TRX && j>=BLY&& j<=TRY){
				myField.getTile(j, i).setZoneType(Zone.OPPONENT_ZONE);
				myField.getTile(j, i).setBlock(Block.BLOCKED);
				suspectedTiles.add(myField.getTile(j, i));
				}
			}
		}
		return suspectedTiles;
	}
	/**
	 * This method blocks all the tiles contained in the home zone
	 * @param myField The field to block the tiles in
	 * @param BLX The bottom left X coordinate
	 * @param BLY The bottom left Y coordinate
	 * @param TRX The top right X coordinate
	 * @param TRY The top right Y coordinate
	 * @return The array list of home zone tiles
	 */
	public static ArrayList<Tile> setHomeZone(Field myField, int BLX,int BLY,int TRX,int TRY){
		ArrayList<Tile> myHomeZoneTiles= new ArrayList<Tile>();
		for(int j=BLY;j<TRY+1;j++){
			for (int i=BLX;i<TRX+1;i++){
				if(i>=BLX && i<=TRX && j>=BLY&& j<=TRY){
				myField.getTile(j, i).setZoneType(Zone.OPPONENT_ZONE);
				myField.getTile(j, i).setBlock(Block.BLOCKED);
				myHomeZoneTiles.add(myField.getTile(j, i));
				}
			}
		}
		return myHomeZoneTiles;
	}
}