package FieldMap;
/*
 * File: Robot.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * Robot class represents the robot used on the field 
 */


/**
 * Robot class represents the robot used on the field 
 *
 */
public class Robot {
	
	/* Instance Variables*/
	
	private Position myPosition;
	
	/** 
	 * Constructor
	 * This method takes care of initializing the Robot 
	 * @param myPosition this represents the (x,y) coordinates of the center of the robot
	 */
	public Robot(Position myPosition){
		this.myPosition= myPosition;
	}
	/** 
	 * This method takes care of setting the position of the robot (x,y) coordinates of the
	 * center of the robot
	 * @param myPosition this represents the (x,y) coordinates of the center of the robot
	 */
	public void setPosition(Position myPosition){
		this.myPosition= myPosition;
	}
	/** 
	 * This method takes care of getting the position of the robot (x,y) coordinates of the
	 * center of the robot
	 * @return it returns the position of the center of the robot (x,y) coordinates 
	 */
	public Position getPosition(){
		return this.myPosition;
	}

}
