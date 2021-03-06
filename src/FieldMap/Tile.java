/*
 * File: Tile.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * Tile class represents the physical tile in the field that's the robot is 
 * navigating in; a tile can be blocked by a block or unblocked. 
 * this class also is used to calculate the shortest path between two tiles
 */

package FieldMap;
/**
 * Tile class represents the physical tile in the field that's the robot is 
 * navigating in; a tile can be blocked by a block or unblocked. 
 * this class also is used to calculate the shortest path between two tiles
 *
 */
public class Tile {
	
	/* Instance Variables*/
/*	these coordinates corresponds to the real coordinates on the actual map with one corner 
**	corresponding to (0,0)
*/
	private Position topLeftPosition; 
	private Position topRightPosition;
	private Position bottomLeftPosition;
	private Position bottomRightPosition;
	private double tileSize;
	private int xIndex;
	private int yIndex;
	private boolean isDiscovered;
	private Block block;
	private Zone myZone;
	
	//variables used for the aStar Algorithm
	private int fScore;
	private int gScore;
	private Tile parent;
	
	/** 
	 * Constructor
	 * This method takes care of initializing the tile 
	 * @param tileNumberX this is the tile index in the x direction
	 * @param tileNumberY this is the tile index in the y direction
	 * @param tileSize this is the tile size of the physical tile on the map
	 * @param block this is given as blocked if the tile is blocked
	 */
	public Tile(int tileNumberX,int tileNumberY, double tileSize, Block block){
		this.parent=null;
		this.xIndex=tileNumberX;
		this.yIndex=tileNumberY;
		this.isDiscovered=false;
		this.tileSize=tileSize;
		this.myZone= Zone.NOT_ZONE;
		this.block=block;
		this.topLeftPosition= new Position((tileSize*tileNumberX),((tileNumberY+1)*tileSize)); //tileNumberX starts with zero
		this.topRightPosition=new Position((tileSize*(tileNumberX+1)),((tileNumberY+1)*tileSize));
		this.bottomLeftPosition=new Position((tileSize*tileNumberX),((tileNumberY)*tileSize));
		this.bottomRightPosition=new Position((tileSize*(tileNumberX+1)),((tileNumberY)*tileSize));
	}
	/** 
	 * This method takes care of setting the position of the tile (x,y) coordinates
	 * @param midpoint: is a position (x,y) coordinate that represents the midpoint of the tile
	 */
	public void setPosition(Position midpoint){
		this.topLeftPosition= new Position((midpoint.getPositionX()-(this.tileSize/2.0)),(midpoint.getPositionY()+(this.tileSize/2.0))); //tileNumberX starts with zero
		this.topRightPosition= new Position((midpoint.getPositionX()+(this.tileSize/2.0)),(midpoint.getPositionY()+(this.tileSize/2.0))); //tileNumberX starts with zero
		this.bottomLeftPosition= new Position((midpoint.getPositionX()-(this.tileSize/2.0)),(midpoint.getPositionY()-(this.tileSize/2.0))); //tileNumberX starts with zero
		this.bottomRightPosition= new Position((midpoint.getPositionX()+(this.tileSize/2.0)),(midpoint.getPositionY()-(this.tileSize/2.0))); //tileNumberX starts with zero
	}
	/** 
	 * This method takes care of setting the tile size of the physical tile of the map
	 * @param tileSize: is the tile size of the physical tile of the map
	 */
	public void setTileSize(double tileSize){
		this.tileSize=tileSize;
	}
	/** 
	 * This method takes care of setting the tile to be discovered by the robot
	 * @param isDiscovered is a boolean that is set true if the robot dicovers the tile
	 */
	public void setDiscovery(boolean isDiscovered){
		this.isDiscovered=isDiscovered;
	}
	/** 
	 * This method takes care of setting the tile index in the x direction
	 * @param x is the tile index in the x direction
	 */
	public void setTileIndexX(int x){
		this.xIndex=x;
	}
	/** 
	 * This method takes care of setting the tile index in the y direction
	 * @param y: is the tile index in the y direction
	 */
	public void setTileIndexY(int y){
		this.yIndex=y;
	}
	/** 
	 * This method takes care of setting the tile to be blocked by a block or free to move in
	 * @param block: is an enum that expects BLOCKED or UNBLOCKED represent a blocked tile
	 * and unblocked tile respectively.
	 */
	public void setBlock(Block block){
		this.block= block;
	}
	/** 
	 * This method returns the status of the block, if dicovered or not discovered by the robot
	 * @return returns true if the block is dicovered and false otherwise
	 */
	public boolean isSeen(){
		return this.isDiscovered;
	}
	/** 
	 * This method takes care of getting the midpoint position of the tile in the (x,y) coordinates
	 * @return it returns the midpoint position in the (x,y) coordinates of the tile 
	 */
	public Position getPosition(){
		Position midPointPosition= new Position(this.topLeftPosition.getPositionX()+(this.tileSize/2.0),this.topLeftPosition.getPositionY()-(this.tileSize/2.0));
		return midPointPosition;
	}
	/** 
	 * This method takes care of getting the tile index in the x direction
	 * @return it returns the tile index in the x direction
	 */
	public int getTileIndexX(){
		return this.xIndex;
	}
	/** 
	 * This method takes care of getting the tile index in the y direction
	 * @return it returns the tile index in the y direction
	 */
	public int getTileIndexY(){
		return this.yIndex;
	}
	/** 
	 * This method takes care of getting the status of the tile if blocked or not blocked
	 * @return it returns BLOCKED if the tile is blocked or UNBLOCKED if the tile is free to move in
	 */
	public Block getBlock(){
		return this.block;
	}
	/** 
	 * This method takes care of getting the tile top left position in the (x,y) coordinates
	 * @return it returns the tile top left position in the (x,y) coordinates
	 */
	public Position getTopLeftPosition(){
		return this.topLeftPosition;
	}
	/** 
	 * This method takes care of getting the tile top right position in the (x,y) coordinates
	 * @return it returns the tile top right position in the (x,y) coordinates
	 */
	public Position getTopRightPosition(){
		return this.topRightPosition;
	}
	/** 
	 * This method takes care of getting the tile bottom right position in the (x,y) coordinates
	 * @return it returns the tile bottom right position in the (x,y) coordinates
	 */
	public Position getBottomRightPosition(){
		return this.bottomRightPosition;
	}
	/** 
	 * This method takes care of getting the tile bottom left position in the (x,y) coordinates
	 * @return it returns the tile bottom left position in the (x,y) coordinates
	 */
	public Position getBottomLeftPosition(){
		return this.bottomLeftPosition;
	}
	/** 
	 * This method takes care of getting the tile size of the physical tile in the map
	 * @return it returns the tile size of the physical tile in the map
	 */
	public double getTileSize(){
		return this.tileSize;
	}
	public Zone getZoneType(){
		return this.myZone;
	}
	public void setZoneType(Zone myZone){
		this.myZone=myZone;
	}
	/*Methods used in the astar algorithm*/
	
	/** 
	 * This method takes care of setting the parent of the tile to get the shortest path using 
	 * the aStar Algorithm
	 * @param parent it takes the parent tile to set it as the tile's parent
	 */
	public void setParent(Tile parent){
		this.parent= parent;
	}
	/** 
	 * This method takes care of setting the f score of the tile to get the shortest path using 
	 * the aStar Algorithm
	 * @param fscore it takes the fscore of the tile
	 */
	public void setFScore(int fscore){
		this.fScore= fscore;
	}
	/** 
	 * This method takes care of setting the g score of the tile to get the shortest path using 
	 * the aStar Algorithm
	 * @param gscore it takes the g score of the tile
	 */
	public void setGScore(int gscore){
		this.gScore= gscore;
	}
	/** 
	 * This method takes care of getting the parent of the tile to get the shortest path using 
	 * the aStar Algorithm
	 * @return it returns the parent of the tile
	 */
	public Tile getParent(){
		return this.parent;
	}
	/** 
	 * This method takes care of getting the f score of the tile to get the shortest path using 
	 * the aStar Algorithm
	 * @return it returns the fscore of the tile
	 */
	public int getFScore(){
		return this.fScore;
	}
	/** 
	 * This method takes care of getting the g score of the tile to get the shortest path using 
	 * the aStar Algorithm
	 * @return it returns the gscore of the tile
	 */
	public int getGScore(){
		return this.gScore;
	}



}
