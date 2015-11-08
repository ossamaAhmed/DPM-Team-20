/*
 * File: TestField.java
 * Written by: Ossama Ahmed
 * ECSE 211 - Design Principles and Methods
 * Fall 2015
 * TestField class is a temporary class used to test the logic of the shortest path algorithm 
 * and the obstacle avoidance algorithm
 */
package FieldMap;

public class TestField {
	
	public static void main(String[] args){
		Field myField= new Field(7,6,30);
		myField.getTile(4, 3).setBlock(Block.BLOCKED);
		myField.getTile(3, 3).setBlock(Block.BLOCKED);
		myField.getTile(2, 3).setBlock(Block.BLOCKED);
		myField.getTile(1, 3).setBlock(Block.BLOCKED);
		myField.getTile(1, 1).setBlock(Block.BLOCKED);

		myField.displayField();
		Task[]path= aStarAlgorithm.aStar(myField.getTiles(), 1,2, 6,1);
		for(int i=0;i<path.length;i++){
			System.out.print(path[i]);
		}
		System.out.println("");
	}

}
