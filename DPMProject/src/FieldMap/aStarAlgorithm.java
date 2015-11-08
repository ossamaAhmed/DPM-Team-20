package FieldMap;

import java.util.ArrayList;
import java.util.Stack;

public class aStarAlgorithm {
	
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
					if (myCell.getTileIndexY() + 1 <= myMap.length) {
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


	private static Tile getMinCell(ArrayList<Tile> openSet) {
		Tile smallest = openSet.get(0);
		for (int i = 1; i < openSet.size(); i++) {
			if (openSet.get(i).getFScore() < smallest.getFScore())
				smallest = openSet.get(i);
		}

		return smallest;
	}


	private static boolean getObstacle(Tile cell) {
		if (cell.getBlock()==Block.BLOCKED) {
			return true;
		} else  {
			return false;
		}
	}


	private static int manhattan(Tile[][] myMap, int cellPosX, int cellPosY, int endPosX, int endPosY) {
		int dX = Math.abs(cellPosX- endPosX);
		int dY = Math.abs(cellPosY- endPosY);
		return (dY + dX);
	}

}
