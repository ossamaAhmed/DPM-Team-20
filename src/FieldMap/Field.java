package FieldMap;
/*
 * File: Field.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * Field class represents the physical map (NxM) matrix 
 */
/**
 * Field class represents the physical map (NxM) matrix 
 *
 */
public class Field {
	
	/* Instance Variables*/
	private Tile[][] Map;
	private double tileSize;
	private int xTiles;
	private int yTiles;
	public int xHomeZone;
	public int yHomeZone;
	
	/** 
	 * Constructor
	 * This method takes care of initializing the field object 
	 * @param xTiles is the number of tiles in the x coordinate 
	 * @param yTiles is the number of tiles in the y coordinate 
	 * @param tileSize is the size of the physical tile
	 * @author Ossama Ahmed
	 */
	public Field(int xTiles, int yTiles,double tileSize ){
		this.xTiles= xTiles;
		this.yTiles= yTiles;
		this.Map=new Tile[yTiles][xTiles];
		this.tileSize=tileSize;
		for(int i=0;i<yTiles;i++){
			for(int j=0;j<xTiles;j++){
				this.Map[i][j]= new Tile(j,i,tileSize,Block.UNBLOCKED);
			}
		}
	}
	/** 
	 * This method takes care of getting the tile size of a tile in the field 
	 * @return it returns the tile size of the physical tile
	 * @author Ossama Ahmed
	 */
	public double getTileSize(){
		return this.tileSize;
	}
	/** 
	 * This method can be used in debugging the field by displaying the field on the console
	 * @author Ossama Ahmed 
	 */
	public void displayField(){
		for(int i=this.yTiles-1;i>=0;i--){
			for(int j=0;j<this.xTiles;j++){
				if(this.Map[i][j].getBlock()==Block.UNBLOCKED){
					System.out.print("0");
				}
				else{
					System.out.print("1");
				}
			}
			System.out.println("");
		}
	}
	/** 
	 * This method takes care of setting a tile instead of an already existing tile in the x and y location
	 * @param newTile is the tile that you want to insert in the field
	 * @param x is the x index of the tile to be replaced
	 * @param y is the y index of the tile to be replaced
	 * @author Ossama Ahmed
	 */
	public void setTile(Tile newTile,int x,int y){
		this.Map[x][y]=newTile;
	}
	/** 
	 * This method takes care of getting a tile at the x and y indices 
	 * @param x is the x index of the tile you want to get
	 * @param y is the y index of the tile you want to get
	 * @return it returns the tile at the x and y locations
	 * @author Ossama Ahmed
	 */
	public Tile getTile(int y,int x){
		return this.Map[y][x];
	}
	public Tile[] getTile(int y){
		return this.Map[y];
	}
	/** 
	 * This method takes care of getting the list of the neighboring tiles to a specific tile
	 * @param x is the x index of the tile you want to get it's neighbors
	 * @param y is the y index of the tile you want to get it's neighbors
	 * @return it returns a list of tiles of the neighboring tiles (north, south, east, west)
	 * @author Ossama Ahmed
	 */
	public Tile[] getNeighbouringTiles(int x,int y){
		Tile[] neighbouringTiles= new Tile[4];
		//getting the upper tile 
		if(y==this.yTiles-1){
			neighbouringTiles[2]= null;
		}
		else{
			neighbouringTiles[2]= this.Map[y+1][x];
		}
		//getting the lower tile
		if(y==0){
			neighbouringTiles[3]= null;
		}
		else{
			neighbouringTiles[3]= this.Map[y-1][x];
		}
		//getting the right tile
		if(x==this.xTiles-1){
			neighbouringTiles[0]= null;
		}
		else{
			neighbouringTiles[0]= this.Map[y][x+1];
		}
		//getting the left tile 
		if(x==0){
			neighbouringTiles[1]= null;
		}
		else{
			neighbouringTiles[1]= this.Map[y][x-1];
		}
		return neighbouringTiles;
	}
	/** 
	 * This method takes care of getting the tiles 2D array
	 * @author Ossama Ahmed
	 * @return it returns the 2D tile array
	 */
	public Tile[][] getTiles(){
		return this.Map;
	}

}