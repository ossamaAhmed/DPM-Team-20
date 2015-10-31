package ev3MainLogic;
import ev3Drive.Odometer;
import ev3ObjectSearch.ObjectPoller;
import ev3ObjectSearch.ObjectSearch;
import ev3Sensors.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 200;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	private FilteredUltrasonicPoller usPoller;
	private FilteredColorPoller colorPoller;
	private ObjectPoller objectPoller;
	private ObjectSearch objectSearch;


	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo(Odometer odo, FilteredUltrasonicPoller usPoller, FilteredColorPoller colorPoller, ObjectPoller objectPoller, ObjectSearch objectSearch ) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		this.usPoller= usPoller;
		this.colorPoller= colorPoller;
		this.objectPoller= objectPoller;
		this.objectSearch= objectSearch;
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: "+ pos[0], 0, 0);
		LCD.drawString("Y: "+pos[1], 0, 1);
		LCD.drawString("H: "+pos[2], 0, 2);
		LCD.drawString("US1 Distance: " + usPoller.getDistanceOf(0), 0, 3 );	// print last US reading
		LCD.drawString("US2 Distance: " + usPoller.getDistanceOf(1), 0, 4 );	// print last US reading
		LCD.drawString("CS1 Reading: " + colorPoller.getReadingOf(0), 0, 5 );	// print last color reading
		LCD.drawString("CS2 Reading: " + colorPoller.getReadingOf(1), 0, 6 );	// print last color reading
		
//		if (objectPoller.isThereObject == 0) {
//			LCD.drawString("No Object Detected", 0, 5 );
//		}
//		
//		if (objectPoller.isThereObject == 1) {
//			LCD.drawString("Object Detected", 0, 5 );
//			LCD.drawString("Blue Block", 0, 6 );
//		}
//		else if (objectPoller.isThereObject == 2) {
//			LCD.drawString("Object Detected", 0, 5 );
//			LCD.drawString("Not Blue Block", 0, 6 );
//		}
		//LCD.drawString("Objects: " + objectSearch.getCounter(), 0, 7 );
		
	
	}
}
