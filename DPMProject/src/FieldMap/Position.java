package FieldMap;

public class Position {
	//these variables correspond to the exact position on the physical map
	private double x;
	private double y;
	
	public Position(double x,double y){
		this.x= x;
		this.y= y;
	}
	public void setPosition(double x,double y){
		this.x= x;
		this.y= y;
	}
	public double getPositionX (){
		return this.x;
	}
	public double getPositionY (){
		return this.y;
	}
	public String toString(){
		return "Position: ( "+x+" ,  "+y+" )";
	}

}
