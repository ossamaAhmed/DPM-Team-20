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
import sensorController.FilteredUltrasonicPoller;
import lejos.hardware.Sound;
import FieldMap.*;

public class Game {
	/* Instance Variables*/
	private Field myField;
	private Robot myRobot;
	private Navigator navigator;
	private FilteredUltrasonicPoller USpoller;
	private final double distanceUltraSonic= 9;
	private ArmController myArm;
	private  OdometerCorrection odoC;
	/** 
	 * Constructor
	 * This method takes care of initializing the game object 
	 * @param myRobot is the robot that will play the game
	 * @param myField is the field that the robot will be navigating in
	 * @param USpoller is the ultrasonic sensor that will be used in detecting the obstacles 
	 * and blocking the tiles
	 */
	public Game(Robot myRobot,Field myField, Navigator navigator, FilteredUltrasonicPoller USpoller, ArmController arm, OdometerCorrection odoC){
		this.myField= myField;
		this.myRobot= myRobot;
		this.navigator=navigator; 
		this.USpoller= USpoller;
		this.myArm=arm;
		this.odoC= odoC;
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
		if(usDist<0.20){
			
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
		if(usDist<0.25){
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
	public void searchZone(ArrayList<Tile> suspectedTileList){
		for (Tile suspectedTile : suspectedTileList ){
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
			searchTile(suspectedTile);
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Task methodPolicy= getIntersectionPolicy(suspectedTile);
//			if(methodPolicy==Task.MOVERIGHT){
//				searchIntersention(suspectedTile,Task.MOVERIGHT);
//
//			}
//			else if(methodPolicy==Task.MOVELEFT){
//				searchIntersention(suspectedTile,Task.MOVELEFT);
//			}
		}
		
		
	}
	//This method is implemented only for search tile right
	public void searchIntersention(Tile suspectedTile, Task myTask){
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		//travel to intersention
		if(myTask==Task.MOVERIGHT){
			this.navigator.turnDown();
			this.navigator.goForward(10);
			this.navigator.turnRight();
			this.navigator.goForward((this.myField.getTileSize()/2)-distanceUltraSonic);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		}
		else if(myTask==Task.MOVELEFT){
			this.navigator.turnDown();
			this.navigator.goForward(10);
			this.navigator.turnLeft();
			this.navigator.goForward((this.myField.getTileSize()/2)-distanceUltraSonic);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		float usDist = USpoller.getDistance();
		if (usDist < 0.25) {
			Sound.beep();
			inspectObject(suspectedTile,robotTileX,robotTileY);
			Sound.beep();
			//go backwards
			double finalPositionX= this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX();
			double finalPositionY= this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY();
			if(myTask==Task.MOVERIGHT){
				this.navigator.goBackwards(this.navigator.getCurrentX()-finalPositionX);
				this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			}
			else if(myTask==Task.MOVELEFT){
				this.navigator.goBackwards(finalPositionX-this.navigator.getCurrentX());
				this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			}
			this.navigator.turnDown();
			this.navigator.goBackwards(finalPositionY-this.navigator.getCurrentY());
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return;
		}
		double finalPositionX= this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX();
		double finalPositionY= this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY();
		if(myTask==Task.MOVERIGHT){
			this.navigator.goBackwards(this.navigator.getCurrentX()-finalPositionX);		}
		else if(myTask==Task.MOVELEFT){
			this.navigator.goBackwards(finalPositionX-this.navigator.getCurrentX());
		}
		this.navigator.turnDown();
		this.navigator.goBackwards(finalPositionY-this.navigator.getCurrentY());
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		return;
	}
	
	public void searchTile(Tile suspectedTile){
		//face the suspected tile
		int robotTileX = (int) (this.myRobot.getPosition().getPositionX() / this.myField
				.getTileSize());
		int robotTileY = (int) (this.myRobot.getPosition().getPositionY() / this.myField
				.getTileSize());
		turnToTile(suspectedTile);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.navigator.goBackwards(distanceUltraSonic);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		float usDist = USpoller.getDistance();
		if (usDist < 0.25) {
			Sound.beep();
			inspectObject(suspectedTile,robotTileX,robotTileY);
			Sound.beep();
			double[] myInitialPosition= {this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
					this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
			double[] myCurrentPosition= {this.navigator.getCurrentX(),
					this.navigator.getCurrentY()};
			double distanceToTravel= this.navigator.getCoordinateDistance(myCurrentPosition, myInitialPosition);
			this.navigator.goBackwards(distanceToTravel);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return;
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
			inspectObject(suspectedTile,robotTileX,robotTileY);
			double[] myInitialPosition= {this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
					this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
			double[] myCurrentPosition= {this.navigator.getCurrentX(),
					this.navigator.getCurrentY()};
			double distanceToTravel= this.navigator.getCoordinateDistance(myCurrentPosition, myInitialPosition);
			this.navigator.goBackwards(distanceToTravel);
			this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
			return;
		}
		Sound.buzz();
		double[] myInitialPosition= {this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionX(),
				this.myField.getTile(robotTileY, robotTileX).getPosition().getPositionY()};
		double[] myCurrentPosition= {this.navigator.getCurrentX(),
				this.navigator.getCurrentY()};
		double distanceToTravel= this.navigator.getCoordinateDistance(myCurrentPosition, myInitialPosition);
		this.navigator.goBackwards(distanceToTravel);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),this.navigator.getCurrentY()));
		//search the tile for any objects 
		//if object detected go search it and if an object was styrofoam just go 
	}
	public void inspectObject(Tile suspectedTile, int initialX,int initialY){
		float usDist = USpoller.getDistance();
		this.navigator.drive.setSpeeds(50, 50);
		double initialPositionX= this.navigator.getCurrentX();
		double initialPositionY= this.navigator.getCurrentY();
		while(usDist>0.05){
			usDist = USpoller.getDistance();
			double distanceTravelled= Math.sqrt(Math.pow(initialPositionX-this.navigator.getCurrentX(), 2)+Math.pow(initialPositionY-this.navigator.getCurrentX(), 2));
			if(distanceTravelled>25) break;
		}
		this.navigator.drive.setSpeeds(0,0);
		this.myRobot.setPosition(new Position(this.navigator.getCurrentX(),
				this.navigator.getCurrentY()));
		//poll the object and scan
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
	

}