package Data_Collector;

//import java.awt.event.KeyEvent;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException; 
public class RS232Protocol
{
//Serial port we're manipulating.
static SerialPort port; 
//Class: RS232Listener
public class RS232Listener implements SerialPortEventListener
{
//	public int logicLevel=0;
   public void serialEvent(SerialPortEvent event)
   {	
	   if (event.getEventValue()>0){      
		   if (LeapTest.get_isStream()==false) {
				LeapListener.counter=0;
				LeapTest.listener.startStreaming();
				LeapTest.startStream.setText("Streaming...");
				LeapTest.set_isStream(true);
				LeapTest.t_condition = LeapTest.t_condition + " " + LeapTest.t_c;
			} else if (LeapTest.get_isStream()==true) {
				LeapTest.set_isStream(false);
				LeapTest.saveFile();	
				LeapTest.trial_condition.setText("Trial Condition");
			}
		   
		   int bytesCount = event.getEventValue();
	       try {
			System.out.println(port.readBytes(bytesCount));
	       } catch (SerialPortException e) {
			e.printStackTrace();
	       }
	   }
     //Check if data is available.
     //if (event.isRXCHAR() && event.getEventValue() >= 0)
//	   System.out.println("Leap is streaming");
//	 if (event.getEventValue()>0)
//     {
//       try
//       {
//         logicLevel = event.getEventValue();
//         if (logicLevel == 2)
//         {
//        	// LeapTest.startStream.setText("Streaming...");
//        	 System.out.println("Leap is streaming");
//        	 //LeapTest.keyPressed(event);
//        	 LeapTest.listener.startStreaming();
//        	 LeapTest.set_isStream(true);
//        	 
//        	 
//         }
//         else if (logicLevel == 1)
//         {
//        	 //LeapTest.startStream.setText("Start Streaming"); 
//        	 System.out.println("Leap is not streaming");
//        	
//        	 LeapTest.saveFile();
//        	 LeapTest.set_isStream(false); 
//        	
//
//         }
//         System.out.print(port.readString(logicLevel));
//         
//       }          
//       catch (SerialPortException e) { e.printStackTrace(); //For debugging
//         try {
//			System.out.print(port.readString(logicLevel));
//		} catch (SerialPortException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} }
     
   }
}
//Member Function: connect
public void connect(String newAddress)
{
   try
   {
     //Set up a connection.
     port = new SerialPort(newAddress);    
     //Open the new port and set its parameters.
     port.openPort();
     port.setParams(38400, 8, 1, 0);          
     //Attach our event listener.
     port.addEventListener(new RS232Listener());
   }  
   catch (SerialPortException e) 
   {
	   e.printStackTrace(); 
   }
}
//Member Function: disconnect
public void disconnect()
{
   try 
   { 
	   port.closePort(); 
   }  
   catch (SerialPortException e) 
   {
	   e.printStackTrace(); 
   }
}
//Member Function: write
public void write(String text)
{
   try
   {
	   port.writeBytes(text.getBytes());
   }  
   catch (SerialPortException e)
   { 
	   e.printStackTrace();
   }
}


}
