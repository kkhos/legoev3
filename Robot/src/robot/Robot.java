package robot;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Robot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LCD.drawString("Hellow World", 0, 4);
		Delay.msDelay(5000);
	}

}
