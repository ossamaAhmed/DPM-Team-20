package initializationRoutine;

import navigationController.Odometer;
import sensorController.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 200;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	private FilteredUltrasonicPoller usPoller;
	private FilteredColorPoller colorPoller;



	
	// arrays for displaying data
	private double [] pos;
	
	/**
	 * A class which is responsible for displaying the robot's 
	 * current sensor and odometer readings
	 * @param odo The odometer to display
	 * @param usPoller The ultrasonic reading to display
	 * @param colorPoller The color sensor reading to display
	 */
	public LCDInfo(Odometer odo, FilteredUltrasonicPoller usPoller, FilteredColorPoller colorPoller) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		this.usPoller= usPoller;
		this.colorPoller= colorPoller;
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	/**
	 * This method is called everytime the LCD is updated and is responsible
	 * for updating the current readings on the screen
	 */
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: "+ pos[0], 0, 0);
		LCD.drawString("Y: "+pos[1], 0, 1);
		LCD.drawString("H: "+pos[2], 0, 2);
		LCD.drawString("US1: " + usPoller.getDistance(), 0, 3 );	// print last US reading
		LCD.drawString("CS1: " + colorPoller.getReadingOf(0), 0, 4 );	// print last color reading
		LCD.drawString("CS2: " + colorPoller.getReadingOf(1), 0, 5 );	// print last color reading	
	}
}
