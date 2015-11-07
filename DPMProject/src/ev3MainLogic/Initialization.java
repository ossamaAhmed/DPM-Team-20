package ev3MainLogic;

import ev3Arm.ArmController;
import ev3Drive.Navigator;
import ev3Drive.Odometer;
import ev3Localization.USLocalizer;
import ev3ObjectSearch.ObjectPoller;
import ev3ObjectSearch.ObjectSearch;
import ev3Sensors.FilteredColorPoller;
import ev3Sensors.FilteredUltrasonicPoller;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import FieldMap.TestField;


public class Initialization {

	// Static Resources:
	// TODO
	private static final EV3LargeRegulatedMotor[] drivingMotor = { new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A")), new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B")) };
	private static final EV3LargeRegulatedMotor[] armMotor = { new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C")), new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D")) };
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final Port[] colorPort = { LocalEV3.get().getPort("S3"), LocalEV3.get().getPort("S4") };
	// TODO

	// Adjustable Variables
	private static final float maxUltrasonicReading = 2.00f; // Max value for us clipping filter
	private static final int usReadingsToMedian = 5;
	private static final int[] colorReadingsToMedian = { 5, 5 };

	//

	public static void main(String[] args) {

		int buttonChoice = 0;
		// Ultrasonic Sensor Initialization
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);

		FilteredUltrasonicPoller usPoller = new FilteredUltrasonicPoller(usSensor, maxUltrasonicReading, usReadingsToMedian);
		//

		// Color Sensor Initialization
		SensorModes colorSensor[] = new SensorModes[2];
		for (int i = 0; i < 2; i++) {
			colorSensor[i] = new EV3ColorSensor(colorPort[i]);
		}
		FilteredColorPoller colorPoller = new FilteredColorPoller(colorSensor, colorReadingsToMedian);
		//

		// Object Search Initialization
		ObjectPoller objectPoller = new ObjectPoller(usPoller, colorPoller);

		// Odometer and Display
		Odometer odo = new Odometer(drivingMotor[0], drivingMotor[1], 30, true);

		// User Interface
		(new Thread() {
			public void run() {
				while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
				}
				System.exit(0);
			}
		}).start();

		TextLCD LCD = LocalEV3.get().getTextLCD();
		LCD.drawString("Ready", 0, 0);

		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		{
			
		}

		// Left Button
		if (buttonChoice == Button.ID_LEFT) {
			//TODO
			// perform the ultrasonic localization with falling edge
			// usPoller.start();
			// colorPoller.start();
			// objectPoller.start();
			// USLocalizer usl = new USLocalizer(odo, usPoller, USLocalizer.LocalizationType.FALLING_EDGE);
			// ObjectSearch objectSearch = new ObjectSearch(odo,objectPoller,usPoller);
			//
			// LCDInfo lcd = new LCDInfo(odo, usPoller,colorPoller,objectPoller , objectSearch);
			// usl.doLocalization();

		}

		// Right Button
		else if (buttonChoice == Button.ID_RIGHT) {
			usPoller.start();
			colorPoller.start();
			TestField test = new TestField();
			test.testSampleField(0);
			

		}

		// Up Button
		else if (buttonChoice == Button.ID_UP) {
			// Float motors and start sensors for debugging
			usPoller.start();
			colorPoller.start();
			objectPoller.start();
			ObjectSearch objectSearch = new ObjectSearch(odo, objectPoller, usPoller);
			LCDInfo lcd = new LCDInfo(odo, usPoller, colorPoller, objectPoller, objectSearch);
			ArmController arm = new ArmController(armMotor);
			for (int i = 0; i < 2; i++) {
				drivingMotor[i].forward();
				drivingMotor[i].flt();
			}
			// Press enter to set both arms to zero, press up to set both arms to armAngle
			while (buttonChoice != Button.ID_ENTER) {

			}
			// Raise both arm 0 and 1 to 0 degrees
			arm.raiseArm(0);
			arm.raiseArm(1);
			while (buttonChoice != Button.ID_UP) {

			}
			// Drop both arm 0 and 1 to armAngle
			arm.dropArm(0);
			arm.dropArm(1);
		}

		// Down Button
		else if (buttonChoice == Button.ID_DOWN) {
			// board search
			// usPoller.start();
			// colorPoller.start();
			// objectPoller.start();
			// ObjectSearch objectSearch = new ObjectSearch(odo,objectPoller,usPoller);
			// LCDInfo lcd = new LCDInfo(odo, usPoller,colorPoller,objectPoller, objectSearch);
			// objectSearch.doBoardSearch();
			usPoller.start();
			colorPoller.start();
			objectPoller.start();
			ObjectSearch objectSearch = new ObjectSearch(odo, objectPoller, usPoller);
			LCDInfo lcd = new LCDInfo(odo, usPoller, colorPoller, objectPoller, objectSearch);
			Navigator nav = new Navigator(odo);
			// Press enter to do square drive
			while (buttonChoice != Button.ID_ENTER) {
				nav.travelTo(60, 0);
				nav.travelTo(60,60);
				nav.travelTo(0, 60);
				nav.travelTo(0, 0);

			}

		}

	}
}
