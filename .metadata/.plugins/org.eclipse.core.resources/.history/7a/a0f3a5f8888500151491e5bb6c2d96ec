package FieldMap;

public class Field {
	private Tile[][] Map;
	private double tileSize;
	private int xTiles;
	private int yTiles;
	
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
	
	public void setTile(Tile newTile,int x,int y){
		this.Map[x][y]=newTile;
	}
	public Tile getTile(int x,int y){
		return this.Map[x][y];
	}
	public Tile[] getNeighbouringTiles(int x,int y){
		Tile[] neighbouringTiles= new Tile[4];
		//getting the upper tile 
		if(y==this.yTiles-1){
			neighbouringTiles[0]= null;
		}
		else{
			neighbouringTiles[0]= this.Map[x][y+1];
		}
		//getting the lower tile
		if(y==0){
			neighbouringTiles[1]= null;
		}
		else{
			neighbouringTiles[1]= this.Map[x][y-1];
		}
		//getting the right tile
		if(x==this.xTiles-1){
			neighbouringTiles[2]= null;
		}
		else{
			neighbouringTiles[2]= this.Map[x+1][y];
		}
		//getting the left tile 
		if(x==0){
			neighbouringTiles[3]= null;
		}
		else{
			neighbouringTiles[3]= this.Map[x-1][y];
		}
		return neighbouringTiles;
	}
	public Tile[][] getTiles(){
		return this.Map;
	}

}
