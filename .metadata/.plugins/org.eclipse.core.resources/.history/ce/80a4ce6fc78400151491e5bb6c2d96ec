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
		int[]path= aStarAlgorithm.aStar(myField.getTiles(), 1,2, 6,1);
		for(int i=0;i<path.length;i++){
			System.out.print(path[i]);
		}
		System.out.println("");
	}

}
