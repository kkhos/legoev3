package robot;


import java.io.IOException;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Key;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.PublishFilter;
import lejos.utility.Delay;

public class Robot {
	static EV3ColorSensor color;
	static EV3GyroSensor gyro;
	static EV3UltrasonicSensor ir;
	static RegulatedMotor leftMotor = Motor.B;
    static RegulatedMotor rightMotor = Motor.C;
    static RegulatedMotor servoMotor = Motor.D;
    static boolean golfFind = false;
    static boolean golfCatch = false;
    static int control = 0;
    static float distance = 255;
    static int angle = 0;
    static int angle_post = 0;
    static int lineColor = 100;
    static int count = 0;
    
    public static void ircheck(SampleProvider distanceMode, float [] sample3){
    	distanceMode.fetchSample(sample3, 0);
        distance = sample3[0]*100;
        System.out.print("D: " + distance);
        System.out.println();
    }

	public static void main(String[] args) throws IOException {
		float frequency = 1;
		Brick brick = BrickFinder.getDefault();
		
		// color sensor
	    Port s4 = brick.getPort("S4");
	    color = new EV3ColorSensor(s4);
	    
	    SampleProvider colorMode = color.getColorIDMode();
	    int sampleSize = colorMode.sampleSize();   
	    float[] sample = new float[sampleSize];
	    
	    // gyro sensor
	    Port s2 = brick.getPort("S2");
	    gyro = new EV3GyroSensor(s2);
	   
	    SampleProvider angleMode = gyro.getAngleMode();
	    int sampleSize2 = angleMode.sampleSize();
	    float[] sample2 = new float[sampleSize2];    
	    
	    // IR sensor
	    Port s1 = brick.getPort("S1");
	    ir = new EV3UltrasonicSensor(s1);
	    SampleProvider distanceMode = new PublishFilter(ir.getDistanceMode(), "Ultrasonic readings", frequency);
	    float [] sample3 = new float[distanceMode.sampleSize()];
        
	
	    // left right motor
	    leftMotor.resetTachoCount();
        rightMotor.resetTachoCount();
	    leftMotor.rotateTo(0);
	    rightMotor.rotateTo(0);
	    leftMotor.setSpeed(400);
	    rightMotor.setSpeed(400);
	    leftMotor.setAcceleration(800);
	    rightMotor.setAcceleration(800);
	    	    
	    //escape
	    Key escape = brick.getKey("Escape");
	    while (!escape.isDown()) {    	
	    	
	    	
	   ircheck(distanceMode, sample3); 	 
       if (distance > 2 && distance <=70 && !golfCatch)
       {
       	golfFind = true;
       	while(golfFind)
       	{
       		
   	        if (distance > 30 && distance <=70)
   	        {
	          leftMotor.setSpeed(150);
		      rightMotor.setSpeed(150);
		  	  leftMotor.forward();
		   	  rightMotor.forward();
   	        }
   	        else if (distance > 7 && distance <=30){
 	 	          leftMotor.setSpeed(70);
			      rightMotor.setSpeed(70);
			  	  leftMotor.forward();
			   	  rightMotor.forward();
   	        }
			else if(distance > 70 || distance <= 2){
				  leftMotor.stop(true);
		          rightMotor.stop(true);  
		          golfFind = false;
			      		 
			}else if (distance > 2 && distance <= 7)
			{
				  leftMotor.stop(true);
		          rightMotor.stop(true); 
			    if (!golfCatch)
			    {
			    	servoMotor.rotate(120);
			    	System.out.println("catch");
			    	golfCatch = true;
			    	golfFind = false;
			    }

			}
	        	
		   	Delay.msDelay(1000);
		   	 ircheck(distanceMode, sample3);		       
       	}  

        golfFind = false; 
       }             
	   
	   if (golfCatch)
	   {
		   angleMode.fetchSample(sample2, 0);
		   angle = Math.abs((int)sample2[0]);
		   while(angle < 170 ||  angle>190)
		   {
			   System.out.println("ang : " + angle);
			   if (angle < 170)
			   {
			   leftMotor.rotate(40);
		       rightMotor.rotate(-40);
			   }
			   else if (angle > 190)
			   {
				   leftMotor.rotate(-40);
			       rightMotor.rotate(40);
				   
			   }
		       angleMode.fetchSample(sample2, 0);
		       angle = Math.abs((int)sample2[0]);
		   }	
		   
		   
		   while(lineColor!=0){
			   colorMode.fetchSample(sample, 0);
			  lineColor = (int)sample[0];
		   leftMotor.setSpeed(200);
		      rightMotor.setSpeed(200);
		  	  leftMotor.forward();
		   	  rightMotor.forward();
		   	Delay.msDelay(200);
		   	System.out.println("color : " + lineColor);
		   }
			  leftMotor.stop(true);
	          rightMotor.stop(true); 
		   servoMotor.rotate(-120);
		   
		   lineColor = 100;
		   golfCatch=false;
		   
		   Delay.msDelay(1000);
		   leftMotor.setSpeed(200);
		      rightMotor.setSpeed(200);
		  	  leftMotor.backward();
		   	  rightMotor.backward();
		   	Delay.msDelay(2000);
			  leftMotor.stop(true);
	          rightMotor.stop(true); 
		   	System.out.println("angle : " + angle);

		   angleMode.fetchSample(sample2, 0);
		   angle = Math.abs((int)sample2[0]);
		   while(angle < 350 && angle>10)
		   {
			   System.out.println("ang : " + angle);
			   if (angle >= 180 && angle < 350)
			   {
			   leftMotor.rotate(40);
		       rightMotor.rotate(-40);
			   }
			   else if (angle < 180 && angle > 10)
			   {
				   leftMotor.rotate(-40);
			       rightMotor.rotate(40);
				   
			   }
		       angleMode.fetchSample(sample2, 0);
		       angle = Math.abs((int)sample2[0]);		   
		   }
	   }
	   
	   
	   ircheck(distanceMode, sample3); 	
	   
	   angleMode.fetchSample(sample2, 0);
       angle_post = angle = Math.abs((int)sample2[0]);
	   while (count < 20 && (distance >=70 || distance <= 3))		   
	   {
		  if (count == 0)
		  {
		   leftMotor.setSpeed(200);
		      rightMotor.setSpeed(200);
		  	  leftMotor.forward();
		   	  rightMotor.forward();
		   	Delay.msDelay(1500);
		    leftMotor.stop(true);
	          rightMotor.stop(true); 
	          
	       
		       leftMotor.rotate(-90);
		       rightMotor.rotate(90);		   
		  }
		  else if (count > 0 && count <= 12)
		  {
			  leftMotor.rotate(25);
			  rightMotor.rotate(-25);			  
		  }
		  else 
		  {
			  leftMotor.rotate(-25);
			  rightMotor.rotate(25);		  
		  }
		  Delay.msDelay(1000);
		  angleMode.fetchSample(sample2, 0);
	      angle = Math.abs((int)sample2[0]);
		  ircheck(distanceMode, sample3);
		  System.out.println(count + " dis " + distance + " ang " + angle);
		   count++;		   
	   }
	   count = 0;

	   angleMode.fetchSample(sample2, 0);
	   angle = Math.abs((int)sample2[0]);
	   while(angle < 350 && angle>10 && distance >= 70)
	   {
		   System.out.println("ang : " + angle);
		   if (angle >= 180 && angle < 350)
		   {
		   leftMotor.rotate(40);
	       rightMotor.rotate(-40);
		   }
		   else if (angle < 180 && angle > 10)
		   {
			   leftMotor.rotate(-40);
		       rightMotor.rotate(40);
			   
		   }
	       angleMode.fetchSample(sample2, 0);
	       angle = Math.abs((int)sample2[0]);		   
	   }
			

	   /* else
	    {
	    
	    	servoMotor.rotate(-120);
	    	System.out.println("release");
	    	golfCatch = false;
	    }*/
	    /*  colorMode.fetchSample(sample, 0);
	      angleMode.fetchSample(sample2, 0);
	      System.out.print(sample[0] + " ");
	      System.out.print(sample2[0]);
	      System.out.println();
	      switch((int)sample[0])
	      {
	      case 0:or.rotate(360);
	          rightMotor.rotate(-360);
	          break;
	      
	      case 7:
	    	  System.out.print("black");
	    	  leftMotor.setSpeed(200);
	    	  rightMotor.setSpeed(200);
	    	  leftMotor.forward();
	    	  rightMotor.forward();	    	  
	    	  break;
	      case 6:
	      default:
	    	  System.out.print("white");
	    	  leftMotor.stop(true);
	          rightMotor.stop(true);
	    	  br
	    	  System.out.print("red");
	    	  leftMoteak;
	      }*/
	     
	    
	       
	       
	      Delay.msDelay(2000);
	    }
	}

}
