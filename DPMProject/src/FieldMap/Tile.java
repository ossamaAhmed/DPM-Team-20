package FieldMap;

public class Tile {
	private Position topLeftPosition;
	private Position topRightPosition;
	private Position bottomLeftPosition;
	private Position bottomRightPosition;
	private double tileSize;
	private int xIndex;
	private int yIndex;
	private boolean isDiscovered;
	private Block block;
	//used for aStar Algorithm
	private int fScore;
	private int gScore;
	private Tile parent;
	
	public Tile(int tileNumberX,int tileNumberY, double tileSize, Block block){
		this.parent=null;
		this.xIndex=tileNumberX;
		this.yIndex=tileNumberY;
		this.isDiscovered=false;
		this.tileSize=tileSize;
		this.block=block;
		this.topLeftPosition= new Position((tileSize*tileNumberX),((tileNumberY+1)*tileSize)); //tileNumberX starts with zero
		this.topRightPosition=new Position((tileSize*(tileNumberX+1)),((tileNumberY+1)*tileSize));
		this.bottomLeftPosition=new Position((tileSize*tileNumberX),((tileNumberY)*tileSize));
		this.bottomRightPosition=new Position((tileSize*(tileNumberX+1)),((tileNumberY)*tileSize));
	}
	public void setPosition(Position midpoint){
		this.topLeftPosition= new Position((midpoint.getPositionX()-(this.tileSize/2.0)),(midpoint.getPositionY()+(this.tileSize/2.0))); //tileNumberX starts with zero
		this.topRightPosition= new Position((midpoint.getPositionX()+(this.tileSize/2.0)),(midpoint.getPositionY()+(this.tileSize/2.0))); //tileNumberX starts with zero
		this.bottomLeftPosition= new Position((midpoint.getPositionX()-(this.tileSize/2.0)),(midpoint.getPositionY()-(this.tileSize/2.0))); //tileNumberX starts with zero
		this.bottomRightPosition= new Position((midpoint.getPositionX()+(this.tileSize/2.0)),(midpoint.getPositionY()-(this.tileSize/2.0))); //tileNumberX starts with zero
	}
	public void setTileSize(double tileSize){
		this.tileSize=tileSize;
	}
	public void setDiscovery(boolean isDiscovered){
		this.isDiscovered=isDiscovered;
	}
	public void setTileIndexX(int x){
		this.xIndex=x;
	}
	public void setTileIndexY(int y){
		this.yIndex=y;
	}
	public void setBlock(Block block){
		this.block= block;
	}
	public boolean isSeen(){
		return this.isDiscovered;
	}
	public Position getPosition(){
		Position midPointPosition= new Position(this.topLeftPosition.getPositionX()+(this.tileSize/2.0),this.topLeftPosition.getPositionY()-(this.tileSize/2.0));
		return midPointPosition;
	}
	public int getTileIndexX(){
		return this.xIndex;
	}
	public int getTileIndexY(){
		return this.yIndex;
	}
	public Block getBlock(){
		return this.block;
	}
	public Position getTopLeftPosition(){
		return this.topLeftPosition;
	}
	public Position getTopRightPosition(){
		return this.topRightPosition;
	}
	public Position getBottomRightPosition(){
		return this.bottomRightPosition;
	}
	public Position getBottomLeftPosition(){
		return this.bottomLeftPosition;
	}
	public double getTileSize(){
		return this.tileSize;
	}
	//astar algorithm methods
	public void setParent(Tile parent){
		this.parent= parent;
	}
	public void setFScore(int fscore){
		this.fScore= fscore;
	}
	public void setGScore(int gscore){
		this.gScore= gscore;
	}
	public Tile getParent(){
		return this.parent;
	}
	public int getFScore(){
		return this.fScore;
	}
	public int getGScore(){
		return this.gScore;
	}



}
