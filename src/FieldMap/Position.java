package FieldMap;
/*
 * File: Position.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * Position class represents the (x,y) coordinates on the map
 */

public class Position {
	
	/* Instance Variables*/
	private double x;
	private double y;
	
	/** 
	 * Constructor
	 * This method takes care of initializing the Position coordinates 
	 * @param x is the x coordinate
	 * @param y is the y coordinate
	 */
	public Position(double x,double y){
		this.x= x;
		this.y= y;
	}
	/** 
	 * This method takes care of setting the Position coordinates 
	 * @param x is the x coordinate
	 * @param y is the y coordinate
	 */
	public void setPosition(double x,double y){
		this.x= x;
		this.y= y;
	}
	/** 
	 * This method takes care of getting the x coordinate of the position
	 * @return it returns the x coordinate of the position 
	 */
	public double getPositionX (){
		return this.x;
	}
	/** 
	 * This method takes care of getting the y coordinate of the position
	 * @return it returns the y coordinate of the position 
	 */
	public double getPositionY (){
		return this.y;
	}
	/** 
	 * This method takes care of getting the string representation of the position
	 * @return it returns the string representation of the position
	 */
	public String toString(){
		return "Position: ( "+x+" ,  "+y+" )";
	}

}
