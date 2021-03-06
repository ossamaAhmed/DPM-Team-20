package FieldMap;
/*
 * File: aStarAlgorithm.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * aStarAlgorithm class provides the methods used in the path finding and navigating of the robot
 */


import java.util.ArrayList;
import java.util.Stack;

/**
aStarAlgorithm class provides the methods used in the path finding and navigating of the robot
 *
 */
public class aStarAlgorithm {
	
	/**
	 * Path finding algorithm for the robot, using a* search. It does this
	 * by evaluating each frontier cell and picking the best candidate. Each
	 * frontier cell gets placed into ArrayList openSet, while searched
	 * tiles are put into ArrayList closedSet. Cells with obstacles are
	 * put into ArrayList obstacles. Repeats these two steps until it
	 * finds the destination tile.
	 * @param myMap is the 2D tile map that the robot navigates in
	 * @param xStart is the x index where the robot is 
	 * @param yStart is the y index where the robot is 
	 * @param xEnd is the x index of the destination tile
	 * @param yEnd is the y index of the destination tile 
	 * @return it returns a task list to the robot to reach it's destination
	 */
	public static Task[] aStar(Tile[][] myMap, int xStart,int yStart, int xEnd, int yEnd) {
		
		ArrayList<Tile> openSet = new ArrayList<Tile>();
		ArrayList<Tile> closedSet = new ArrayList<Tile>();
		ArrayList<Tile> obstacles = new ArrayList<Tile>();
		Tile myCell = myMap[yStart][xStart];
		myCell.setParent(null);
		openSet.add(myCell);
		Tile successor = null;
		myCell.setGScore(0);
		myCell.setFScore(manhattan(myMap,xStart,yStart,xEnd,yEnd));
		while (openSet.size() != 0) {
			myCell = getMinCell(openSet);
			openSet.remove(myCell);
			closedSet.add(myCell);

			for (int i = 0; i < 4; i++) {
				int oldScore=0;
				int calculatedGScore=0;
				int calculatedFScore=0;
				successor = null;
				if (i == 0) {
					//check right cell
					if (myCell.getTileIndexX() + 1 <= myMap[0].length-1) {
						successor = myMap[myCell.getTileIndexY()][myCell.getTileIndexX() + 1];
						if(successor.getParent()!=null){
							oldScore=successor.getFScore();
						}
						calculatedGScore=myCell.getGScore() + 1;
						calculatedFScore= calculatedGScore+ manhattan(myMap,myCell.getTileIndexX()+1,myCell.getTileIndexY(),xEnd,yEnd);
					}

				}
				if (i == 1) {
					//check left cell
					if (myCell.getTileIndexX() - 1 >= 0) {
						successor = myMap[myCell.getTileIndexY()][myCell.getTileIndexX() - 1];
						if(successor.getParent()!=null){
							oldScore=successor.getFScore();
						}
						calculatedGScore=myCell.getGScore() + 1;
						calculatedFScore= calculatedGScore+  manhattan(myMap,myCell.getTileIndexX()-1,myCell.getTileIndexY(),xEnd,yEnd);
					}
				}
				if (i == 2) {
					//check upper cell
					if (myCell.getTileIndexY() + 1 <= myMap.length-1) {
						successor = myMap[myCell.getTileIndexY()+1][myCell.getTileIndexX()];
						if(successor.getParent()!=null){
							oldScore=successor.getFScore();
						}
						calculatedGScore=myCell.getGScore() + 1;
						calculatedFScore= calculatedGScore+  manhattan(myMap,myCell.getTileIndexX(),myCell.getTileIndexY()+1,xEnd,yEnd);
					}

				}
				if (i == 3) {
					//check bottom cell
					if (myCell.getTileIndexY() - 1 >= 0) {
						successor = myMap[myCell.getTileIndexY()-1][myCell.getTileIndexX()];
						if(successor.getParent()!=null){
							oldScore=successor.getFScore();
						}
						calculatedGScore=myCell.getGScore() + 1;
						calculatedFScore=  calculatedGScore+ manhattan(myMap,myCell.getTileIndexX(),myCell.getTileIndexY()-1,xEnd,yEnd);
					}
				}

				if (successor != null) {
					if (getObstacle(successor) == true
							&& obstacles.contains(successor) == false) {
						obstacles.add(successor);
					}

					else if (obstacles.contains(successor) == false
							&& openSet.contains(successor) == false
							&& closedSet.contains(successor) == false) {
						successor.setParent(myCell);
						successor.setFScore(calculatedFScore);
						successor.setGScore(calculatedGScore);
						openSet.add(successor);
					}
					else if (obstacles.contains(successor) == false
							&& openSet.contains(successor) == true
							&& closedSet.contains(successor) == false) {
						if(calculatedFScore<oldScore){
							successor.setParent(myCell);
							successor.setFScore(calculatedFScore);
							successor.setGScore(calculatedGScore);
							openSet.add(successor);
						}
						
					}
					//reached the final position
					if (successor.getTileIndexX() == xEnd && successor.getTileIndexY() == yEnd) {
						
						successor.setParent(myCell);
						System.out.println("The F score is of the final path: "+successor.getFScore());
						return returnRoute(successor);
					}
				}

			}

		}
		return null;
	}
	/**
	 * Method which returns the route of the path leading to the destination tile. Does
	 * this by taking the last cell, where the destination tile is located and repeatedly
	 * calling the parent cell until it reached the original robot's position.
	 * While calling the parent cell it notes which direction it need to travel
	 * from parent to child cell, storing it into a stack and finally
	 * transferring it into an Task array.
	 * @param cell is the destination tile
	 * @return it returns a task list to the robot to reach it's destination
	 */
	private static Task[] returnRoute(Tile cell) {
		Tile currentCell = cell;
		Tile parent = currentCell.getParent();
		Stack<Task> temp = new Stack<Task>();
		while (parent != null) {
			int dX = currentCell.getTileIndexX() - parent.getTileIndexX();
			int dY = currentCell.getTileIndexY() - parent.getTileIndexY();

			if (dX == 1)
				temp.push(Task.MOVERIGHT); //go right
			if (dX == -1)
				temp.push(Task.MOVELEFT); //go left
			if (dY == 1)
				temp.push(Task.MOVEUP); //go down
			if (dY == -1)
				temp.push(Task.MOVEDOWN); //go up

			currentCell = parent;
			parent = currentCell.getParent();
		}
		Task route[] = new Task[temp.size()];
		for (int i = 0; i < route.length; i++) {

			route[i] = temp.pop();
		}
		System.out.print("The path generated is ");
		for(int i=0;i<route.length;i++){
			System.out.print(route[i]);
		}
		System.out.println("");
		return route;
	}

	/**
	 * Searches through all the tiles in the open set of tiles, and get the tile
	 * with the lowest search algorithm score possible.
	 * @param openSet the tiles that haven't been searched through yet in the path finding algorithm
	 * @return the tile with the best score to reach the destination tile.
	 */
	private static Tile getMinCell(ArrayList<Tile> openSet) {
		Tile smallest = openSet.get(0);
		for (int i = 1; i < openSet.size(); i++) {
			if (openSet.get(i).getFScore() < smallest.getFScore())
				smallest = openSet.get(i);
		}

		return smallest;
	}
	/**
	 * Checks whether or not the tile in question has an obstacle
	 * @param cell current tile being searched in the path finding algorithm
	 * @return boolean whether or not there is an obstacle in the tile
	 */

	private static boolean getObstacle(Tile cell) {
		if (cell.getBlock()==Block.BLOCKED) {
			return true;
		} else  {
			return false;
		}
	}
	/**
	 * Takes future x and y tile indices of the robot and returns the Manhattan
	 * distance to the destination tile.
	 * @param cellPosX the robot's tile x index
	 * @param cellPosY the robot's tile y index
	 * @param endPosX the destination's tile x index
	 * @param endPosY the destination's tile y index
	 * @return Manhattan distance used in path finding algorithm.
	 */
	public static int manhattan(Tile[][] myMap, int cellPosX, int cellPosY, int endPosX, int endPosY) {
		int dX = Math.abs(cellPosX- endPosX);
		int dY = Math.abs(cellPosY- endPosY);
		return (dY + dX);
	}

}
