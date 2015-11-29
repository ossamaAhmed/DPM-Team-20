package Game;
/*
 * File: Game.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * Game class is the responsible for initiating the game of catch the flag 
 */

import java.util.ArrayList;

import motorController.DriveController;
import navigationController.Navigator;
import sensorController.FilteredUltrasonicPoller;
import lejos.hardware.Sound;
import FieldMap.*;

/**
 * Game class is the responsible for initiating the game of catch the flag 
 *
 */
public class Game {
	/* Instance Variables*/
	private Field myField;
	private Robot myRobot;
	private DriveController drive;
	private Navigator navigator;
	private FilteredUltrasonicPoller USpoller;
	/** 
	 * Constructor
	 * This method takes care of initializing the game object 
	 * @param myRobot is the robot that will play the game
	 * @param myField is the field that the robot will be navigating in
	 * @param USpoller is the ultrasonic sensor that will be used in detecting the obstacles 
	 * and blocking the tiles
	 */
	public Game(Robot myRobot,Field myField,DriveController drive, Navigator navigator, FilteredUltrasonicPoller USpoller){
		this.myField= myField;
		this.myRobot= myRobot;
		this.navigator=navigator; 
		this.USpoller= USpoller;
	}
	/** 
	 * This method takes care of moving the robot to a specified tile
	 * @param tileX is the x index of the tile the robot should navigate to
	 * @param tileY is the y index of the tile the robot should navigate to  
	 */
	public void moveRobot(int tileX, int tileY){
		this.myField.displayField();
		int robotTileX= (int) ((int) this.myRobot.getPosition().getPositionX()/this.myField.getTileSize());
		int robotTileY= (int) ((int) this.myRobot.getPosition().getPositionY()/this.myField.getTileSize());

		Task[]path= aStarAlgorithm.aStar(myField.getTiles(), robotTileX,robotTileY, tileX,tileY);
		for(int i=0;i< path.length;i++){
			//update the robot's position
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			robotTileX= (int) (this.myRobot.getPosition().getPositionX()/this.myField.getTileSize());
			robotTileY= (int) (this.myRobot.getPosition().getPositionY()/this.myField.getTileSize());
			Task currentTask= path[i];
			System.out.println("Timmy: my Current positionX"+this.myRobot.getPosition().getPositionX()+"tileX is "+ robotTileX+ " and my current positionY is "+this.myRobot.getPosition().getPositionY()+" tileY is "+ robotTileY+ ", I am attempting to preform "+currentTask);
		
			boolean attemptingTask= attemptTask(currentTask);
			if(!attemptingTask){
				Sound.beep();
				blockNextTile(robotTileX,robotTileY,currentTask);
				this.myField.displayField();
				robotTileX= (int) (this.myRobot.getPosition().getPositionX()/this.myField.getTileSize());
				robotTileY= (int) (this.myRobot.getPosition().getPositionY()/this.myField.getTileSize());
				path= aStarAlgorithm.aStar(myField.getTiles(), robotTileX,robotTileY, tileX,tileY);
				i=-1;
			}
		}
		//calculate the shortest path and return a list of movments while shortest path list is not empty
		//while shortest path list is not empty
		//iterate over the list of movments, position the robot to attempt the move check the tile looking at it 
		//if empty, attempt the move and go to the next movment, if not empty: block it
		//and generate a new path out of the new starting point but with the new ending point, change tha array list 
	}
	/** 
	 * This method takes care of blocking the next tile that the robot detected an object in
	 * @param posx is the x index of the tile that the robot is currently in
	 * @param posy is the y index of the tile that the robot is currently in 
	 * @param task is the task that the robot was attempting to perform and got blocked while 
	 * performing it 
	 */
	public void blockNextTile(int posx,int posy,Task task){
		int nextTileX= posx;
		int nextTileY= posy;
		switch(task){
		case MOVEUP: nextTileX= posx;
					 nextTileY= posy+1;
					 break;
		case MOVEDOWN: nextTileX= posx;
					   nextTileY= posy-1;
					   break;
		case MOVERIGHT: nextTileX= posx+1;
						nextTileY= posy;
						break;
		case MOVELEFT:  nextTileX= posx-1;
						nextTileY= posy;
						break;
		}
		this.myField.getTile(nextTileY, nextTileX).setBlock(Block.BLOCKED);
	}
	/** 
	 * This method takes care of actually attempting the task using the hardware of the robot
	 * @param t is the task that the robot should attempt
	 * @return it returns true is the task was attempted successfully and false otehrwise
	 */
	public boolean attemptTask(Task t){
	//update the robot's position
		switch(t){
		case MOVEUP: this.navigator.turnUp();
					 break;
		case MOVEDOWN: this.navigator.turnDown();
					   break;
		case MOVERIGHT: this.navigator.turnRight();
						break;
		case MOVELEFT:  this.navigator.turnLeft();
						break;
		}
		this.navigator.goBackwards(8);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		float usDist= USpoller.getDistance();
		if(usDist<0.25){
			this.navigator.goForward(8);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return false;
		}
		this.navigator.goForwardHalfTile(this.myField.getTileSize());
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		usDist= USpoller.getDistance();
		if(usDist<0.25){
			this.navigator.goBackwards((this.myField.getTileSize()/2)-8);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return false;
		}
		this.navigator.goForwardHalfTile(this.myField.getTileSize());
		this.navigator.goForward(8);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		return true;
		
	}
	/**
	 * This method moves the robot around the outside of the Opponent Zone, scans for objects, and attempts to identify the
	 * opponent flag.
	 * @param suspectedTileList The ArrayList of Tiles corresponding to the Opponent Zone
	 */
	public void searchZone(ArrayList<Tile> suspectedTileList){
		for (Tile suspectedTile : suspectedTileList ){
			Tile[] myNeighbors = this.myField.getNeighbouringTiles(suspectedTile.getTileIndexX(), suspectedTile.getTileIndexY());
			Tile goToTile= null;
			//getting a suitable tile to search the suspected tile,, include condition for blocked tile NOT DONE 
			for(Tile neighbor : myNeighbors){
				if(neighbor!=null &&neighbor.getZoneType()!=Zone.OPPONENT_ZONE){
					goToTile=neighbor;
					break;
				}
			}
			System.out.println("Going To "+goToTile.getTileIndexX()+"   " +goToTile.getTileIndexY());
			moveRobot(goToTile.getTileIndexX(),goToTile.getTileIndexY());
			searchTile(suspectedTile);
		}
		
	}

	/**
	 * This method searches the given tile for objects. First it takes an initial scan, if no object is found it moves the robot
	 * forward and takes another scan. Lastly, it returns the robot to it's position prior to the search.
	 * @param suspectedTile The tile to look for objects in.
	 */
	public void searchTile(Tile suspectedTile){
		//Face the suspected tile
		//
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		navigator.turnToTile(suspectedTile,myRobot,myField);
		delay(300);
		// First check for an object
		//
		float usDist = USpoller.getDistance();
		this.navigator.goBackwards(8);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		if (usDist < 0.25) {
			Sound.beep();
			inspectObject(suspectedTile,robotTileX,robotTileY);
			Sound.beep();
//			this.navigator.travelToBackwards(robotTileX*this.myField.getTileSize()-15,robotTileY*this.myField.getTileSize()-15 );
			navigator.travelBackToTile(robotTileY,robotTileX, myField);
			return;
		}
		//
		//
		Sound.buzz();
		// Move forward half a tile, and check again for an object
		//
		this.navigator.goForwardHalfTile(this.myField.getTileSize());
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),
				this.navigator.getCurrentY()));
		delay(200);
		usDist = USpoller.getDistance();
		if (usDist < 0.25) {
			inspectObject(suspectedTile,robotTileX,robotTileY);
			Sound.beep();
//			this.navigator.travelToBackwards(robotTileX*this.myField.getTileSize()-15,robotTileY*this.myField.getTileSize()-15 );
			navigator.travelBackToTile(robotTileY,robotTileX, myField);
			return;
		}
		//
		//
		Sound.buzz();
		navigator.travelBackToTile(robotTileY,robotTileX, myField);
		//search the tile for any objects 
		//if object detected go search it and if an object was styrofoam just go 
	}
	/**
	 * Moves the robot forward until the ultrasonic sensor detects an object.
	 */
	public void inspectObject(Tile suspectedTile, int initialX,int initialY){
		navigator.goForwardUntilObject(0.05f, 25);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),
				this.navigator.getCurrentY()));
		//poll the object and scan
		
		delay(2000);
	}

	
	/**
	 * Helper method which sleeps the thread for a given time
	 * @param time The time to sleep the thread
	 */
	public void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
