package Game;
/*
 * File: Game.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * Game class is the responsible for initiating the game of catch the flag 
 */
import java.util.ArrayList;

import motorController.ArmController;
import navigationController.Navigator;
import navigationController.OdometerCorrection;
import sensorController.FilteredColorPoller;
import sensorController.FilteredUltrasonicPoller;
import lejos.hardware.Sound;
import FieldMap.*;

/**
 * Game class is responsible for performing all the tasks
 * relavent to the game of Capture the Flag
 *
 */
public class Game {
	/* Instance Variables*/
	private Field myField;
	private Robot myRobot;
	private Navigator navigator;
	private FilteredUltrasonicPoller USpoller;
	private FilteredColorPoller ColorPoller;

	private final double distanceUltraSonic= 9;
	private ArmController myArm;
	private int flagColor;
	private  OdometerCorrection odoC;
	private static final int armAngleL[] = {110,0}; // The required rotation to raise or lower the arm
	private static final int captureAngle[] = {110,-110}; // The required rotation to raise or lower the arm
	/** 

	 * Takes as parameters, the robots sensors, motors and navigation system
	 * @param myRobot is the robot that will play the game
	 * @param myField is the field that the robot will be navigating in
	 * @param navigator The robot's navigation system
	 * @param USpoller is the ultrasonic sensor that will be used in detecting the obstacles 
	 * and blocking the tiles
	 * @param arm The arm used in capturing objects
	 * @param odoC The odometer correction routine
	 * @param colorPoller The robot's color sensor poller
	 * @param flagColor The flag color of the opponents flag
	 */
	public Game(Robot myRobot,Field myField, Navigator navigator, FilteredUltrasonicPoller USpoller, ArmController arm, OdometerCorrection odoC,FilteredColorPoller colorPoller, int flagColor){
		this.myField= myField;
		this.myRobot= myRobot;
		this.navigator=navigator; 
		this.USpoller= USpoller;
		this.myArm=arm;
		this.odoC= odoC;
		this.flagColor=flagColor;
		this.ColorPoller=colorPoller;
		this.odoC.run=false;
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
		ArrayList<Tile> tilesToBeUnBlocked= new ArrayList<Tile>();
		path = pathGenerator(path, robotTileX,robotTileY, tileX,tileY,tilesToBeUnBlocked,3);
		if(path==null) return;
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
				path = pathGenerator(path, robotTileX,robotTileY, tileX,tileY,tilesToBeUnBlocked,3);
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
		this.odoC.run= false;
		//center the robot if it's not centered
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		this.navigator.travelTo(this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
								this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY());
	//update the robot's position
		this.odoC.run=true;
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
//		this.navigator.goBackwards(distanceUltraSonic);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		float usDist= USpoller.getDistance();
		if(usDist<0.28){
			
//			this.navigator.goForward(distanceUltraSonic);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return false;
		}
		switch(t){
		case MOVEUP:this.navigator.travelTo(this.navigator.getCurrentX(), this.navigator.getCurrentY()+(this.myField.getTileSize()/2));
					 break;
		case MOVEDOWN: this.navigator.travelTo(this.navigator.getCurrentX(), this.navigator.getCurrentY()-(this.myField.getTileSize()/2));
					   break;
		case MOVERIGHT: this.navigator.travelTo(this.navigator.getCurrentX()+(this.myField.getTileSize()/2), this.navigator.getCurrentY());
						break;
		case MOVELEFT:  this.navigator.travelTo(this.navigator.getCurrentX()-(this.myField.getTileSize()/2), this.navigator.getCurrentY());
						break;
		}
		
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		usDist= USpoller.getDistance();
		if(usDist<0.28){
			this.navigator.goBackwards((this.myField.getTileSize()/2));
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return false;
		}
		switch(t){
		case MOVEUP:this.navigator.travelTo(this.navigator.getCurrentX(), this.navigator.getCurrentY()+(this.myField.getTileSize()/2));
					 break;
		case MOVEDOWN: this.navigator.travelTo(this.navigator.getCurrentX(), this.navigator.getCurrentY()-(this.myField.getTileSize()/2));
					   break;
		case MOVERIGHT: this.navigator.travelTo(this.navigator.getCurrentX()+(this.myField.getTileSize()/2), this.navigator.getCurrentY());
						break;
		case MOVELEFT:  this.navigator.travelTo(this.navigator.getCurrentX()-(this.myField.getTileSize()/2), this.navigator.getCurrentY());
						break;
		}
//		this.navigator.goForward(distanceUltraSonic);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		return true;
		
	}
	/**
	 * This method moves the robot around the outside of the Opponent Zone, scans for objects, and attempts to identify the
	 * opponent flag.
	 * @param suspectedTileList The ArrayList of Tiles corresponding to the Opponent Zone
	 */
	public void searchZone(ArrayList<Tile> suspectedTileList){
		while(suspectedTileList.size()>0){
			Tile suspectedTile= chooseSuspectedTile(suspectedTileList);
			System.out.println("Suspected Tile is "+suspectedTile.getTileIndexX() + "    "+ suspectedTile.getTileIndexY() );
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
			if(searchTile(suspectedTile)){
				moveRobot(this.myField.xHomeZone,this.myField.yHomeZone);
				navigator.goBackwards(10);
				myArm.raiseArmTo(0, captureAngle[0]);
				myArm.raiseArmTo(1, captureAngle[1]);
				myArm.raiseArm(0);
				Sound.beepSequenceUp();
				break;
			}
			else{
				suspectedTileList.remove(suspectedTile);
			}

		}
	}
		
		
	/**
	 * This method searches the given tile for objects. First it takes an initial scan, if no object is found it moves the robot
	 * forward and takes another scan. Lastly, it returns the robot to it's position prior to the search.
	 * @param suspectedTile The tile to look for objects in.
	 */
	public boolean searchTile(Tile suspectedTile){
		//face the suspected tile
		boolean returnValue=false;
		this.odoC.run= false;
		//center the robot if it's not centered
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		this.navigator.travelTo(this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
								this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY());
	//update the robot's position
		this.odoC.run=true;
		turnToTile(suspectedTile);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		float usDist = USpoller.getDistance();
		if (usDist < 0.20) {
			Sound.beep();
			returnValue= inspectObject(suspectedTile,robotTileX,robotTileY);
			Sound.beep();
			double[] myInitialPosition= {this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
					this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
			double[] myCurrentPosition= {this.navigator.getCurrentX(),
					this.navigator.getCurrentY()};
			double distanceToTravel= this.navigator.getCoordinateDistance(myCurrentPosition, myInitialPosition);
			this.navigator.goBackwards(distanceToTravel);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return returnValue;
		}
		Sound.buzz();
		this.navigator.goForwardHalfTile(this.myField.getTileSize());
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),
				this.navigator.getCurrentY()));
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		usDist = USpoller.getDistance();
		if (usDist < 0.25) {
			Sound.beep();
			returnValue= inspectObject(suspectedTile,robotTileX,robotTileY);
			double[] myInitialPosition= {this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
					this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
			double[] myCurrentPosition= {this.navigator.getCurrentX(),
					this.navigator.getCurrentY()};
			double distanceToTravel= this.navigator.getCoordinateDistance(myCurrentPosition, myInitialPosition);
			this.navigator.goBackwards(distanceToTravel);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return returnValue;
		}
		Sound.buzz();
		double[] myInitialPosition= {this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
				this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
		double[] myCurrentPosition= {this.navigator.getCurrentX(),
				this.navigator.getCurrentY()};
		double distanceToTravel= this.navigator.getCoordinateDistance(myCurrentPosition, myInitialPosition);
		this.navigator.goBackwards(distanceToTravel);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		return returnValue;
		//search the tile for any objects 
		//if object detected go search it and if an object was styrofoam just go 
	}
	

	/**
	 * Moves the robot forward until the ultrasonic sensor detects an object.
	 */

	public boolean inspectObject(Tile suspectedTile, int initialX,int initialY){
		boolean returnValue=true;
		float usDist = USpoller.getDistance();
		myArm.raiseArmTo(1,captureAngle[1]);
		Sound.beep();
		myArm.raiseArmTo(0,captureAngle[0]);
		Sound.beep();
		this.navigator.goForward((usDist*100)-5);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),
				this.navigator.getCurrentY()));
		myArm.raiseArmTo(1, 170);
		myArm.raiseArmTo(1,captureAngle[1]);
		myArm.raiseArmTo(1, 170);
		// start add stuff
		myArm.raiseArm(0);
		this.navigator.goBackwards(8);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),
		this.navigator.getCurrentY()));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;

	}
	/**
	 * Turn the robot to face a given tile
	 * @param suspectedTile The tile to face
	 */
	public void turnToTile(Tile suspectedTile){
		Task myPolicy= getIntersectionPolicy(suspectedTile);
		if(myPolicy==Task.MOVELEFT){
			this.navigator.turnLeft();
		}
		else if(myPolicy==Task.MOVERIGHT){
			this.navigator.turnRight();
			
		}
		else if(myPolicy==Task.MOVEUP){
			this.navigator.turnUp();
		}
		else{
			this.navigator.turnDown();
		}
	}
	/**
	 * Helper method which returns a Task depending on where the robot is relative to the given tile
	 * @param suspectedTile The given tile  to move into
	 * @return The Task which the robot needs to perform to move to the given tile
	 */
	public Task getIntersectionPolicy(Tile suspectedTile){
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		int differenceX= robotTileX-suspectedTile.getTileIndexX();
		int differenceY= robotTileY-suspectedTile.getTileIndexY();
		if(differenceY==-1){
			return Task.MOVEUP;
		}
		else if(differenceY==1){
			return Task.MOVEDOWN;
		}
		else if(differenceX==-1){
			return Task.MOVERIGHT;
		}
		else {
			return Task.MOVELEFT;
		}
	} 
	/**
	 * Recursive method which takes an unmodified path from the A* algorithm and forces it to have a maximum amount of
	 * movements in the same direction
	 * @param currentPath The original path
	 * @param startX The start X position of the robot
	 * @param startY The start Y position of the robot
	 * @param endX The end X position of the robot
	 * @param endY The end Y position of the robot
	 * @param tilesToBeUnBlocked An array list storing temporarily blocked tiles
	 * @param restriction The maximum amount of movement Tasks in the same direction
	 * @return The new modified path
	 */
	public Task[] pathGenerator(Task[] currentPath, int startX,int startY,int endX,int endY,ArrayList<Tile> tilesToBeUnBlocked, int restriction){
		if(currentPath==null){
			return currentPath;
		}
		Task theRepeatedTask= currentPath[0];
		int counter =0;
		int i= startX;
		int j= startY;
		for(Task myTask: currentPath){
			switch(myTask){
			case MOVELEFT: i--;
							break;
			case MOVERIGHT: i++;
							break;
			case MOVEUP:	j++;
							break;
			case MOVEDOWN:	j--;
							break;

			}
			if(theRepeatedTask==myTask){
				counter++;
			}
			else{
				theRepeatedTask=myTask;
				counter=1;
			}
			if(counter==restriction&&!(i==endX&&j==endY)){
				this.myField.getTile(j, i).setBlock(Block.BLOCKED);
				tilesToBeUnBlocked.add(this.myField.getTile(j, i));
				Task[] newPath= aStarAlgorithm.aStar(myField.getTiles(), startX,startY,endX,endY);
				if(newPath==null){
					for(Tile myTile: tilesToBeUnBlocked ){
						myTile.setBlock(Block.UNBLOCKED);
					}
					newPath= aStarAlgorithm.aStar(myField.getTiles(), startX,startY,endX,endY);
					return pathGenerator( newPath,  startX, startY,endX,endY,tilesToBeUnBlocked,restriction+1);
				}
				return  pathGenerator( newPath,  startX, startY,endX,endY,tilesToBeUnBlocked,restriction);
			}
			
		}
		for(Tile myTile: tilesToBeUnBlocked ){
			myTile.setBlock(Block.UNBLOCKED);
		}
		return currentPath;
	}
	/**
	 * Given a list of suspected tiles, choose the one which has the smallest manhattan distance
	 * @param suspectedTileList The given list of tiles
	 * @return The closest tile in the given list
	 */
	public Tile chooseSuspectedTile(ArrayList<Tile> suspectedTileList){
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		double manDistance=aStarAlgorithm.manhattan(this.myField.getTiles(), robotTileX, robotTileY,  suspectedTileList.get(0).getTileIndexX(),  suspectedTileList.get(0).getTileIndexY());
		double currentDiffDistance=manDistance;
		Tile currentTile= this.myField.getTile(suspectedTileList.get(0).getTileIndexY(),  suspectedTileList.get(0).getTileIndexX());
		for(int i=1;i<suspectedTileList.size();i++){
			manDistance=aStarAlgorithm.manhattan(this.myField.getTiles(), robotTileX, robotTileY,  suspectedTileList.get(i).getTileIndexX(),  suspectedTileList.get(i).getTileIndexY());
			if(manDistance<currentDiffDistance){
				currentDiffDistance= manDistance;
				currentTile= this.myField.getTile(suspectedTileList.get(i).getTileIndexY(),  suspectedTileList.get(i).getTileIndexX());
			}
		}
		return currentTile;
	}
	


}