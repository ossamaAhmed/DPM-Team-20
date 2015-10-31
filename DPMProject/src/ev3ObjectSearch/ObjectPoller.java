package ev3ObjectSearch;

import ev3Sensors.*;

public class ObjectPoller extends Thread {
	//Initialization Variables
	private FilteredUltrasonicPoller usPoller;
	private FilteredColorPoller colorPoller;
	private int counter=0;
	public int isThereObject=0;
	
	// Adjustable Variables
	public final double maxDistanceThreshold = 0.065; // Require the block to be within this distance
	public  final double minDistanceThreshold = 0.050; // Require the block to be within this distance

	private static final double colorThreshold = 0.5; // Require a value greater than this to identify the block as blue
	private static final double idThreshold = 5; // The required amount of positive readings before confirming an object

	
	public ObjectPoller(FilteredUltrasonicPoller usPoller, FilteredColorPoller colorPoller){
		this.usPoller = usPoller;
		this.colorPoller = colorPoller;
		
	}
	
	public void run() {
		while (true){
		isThereObject = identifyObject();
		
		try {
			Thread.sleep(50);
		} catch (Exception e) {
		}
		
		}
		
	}
	
	public int identifyObject() {
		int result=0; // 0 = No Object, 1 = Blue Block , 2 = Wood Block
		
		// Check for object within
		if (usPoller.getDistance()>= minDistanceThreshold && usPoller.getDistance()<= maxDistanceThreshold &&(colorPoller.blueObject()||colorPoller.whiteObject()) && counter < idThreshold*1.5 )
		{ 
			counter++;
		}
		else if (counter >0) counter--;
		
		if (counter >= idThreshold) {
			if(colorPoller.blueObject()) result=1;
			else result=2;
		}
		
		
		return result;
	}
	
	

}
