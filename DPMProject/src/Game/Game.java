package Game;

import FieldMap.*;

public class Game {
	private Field myField;
	private Robot myRobot;
	
	public Game(Robot myRobot,Field myField){
		this.myField= myField;
		this.myRobot= myRobot;
	}
	public void moveRobot(int tileX, int tileY){
		//calculate the shortest path and return a list of movments while shortest path list is not empty
		//while shortest path list is not empty
		//iterate over the list of movments, position the robot to attempt the move check the tile looking at it 
		//if empty, attempt the move and go to the next movment, if not empty: block it
		//and generate a new path out of the new starting point but with the new ending point, change tha array list 
	}

}
