package Data_Collector;

//import java.awt.event.KeyEvent;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException; 
public class RS232Protocol extends Thread
{
//Serial port we're manipulating.
static SerialPort port; 
//Class: RS232Listener
public class RS232Listener implements SerialPortEventListener
{

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
	    	//For Synch Testing	    	   
	    	//System.out.println("Recieved the Bytes");
			//System.out.println("Finished Reading bytes, writing now");
			//port.writeByte((byte) 1);
			//System.out.println("Finished Writing");
	       } catch (SerialPortException e) {
			e.printStackTrace();
	       }
	   }     
   }
}

public void run()
{
	RS232Protocol serial = new RS232Protocol();
	serial.connect("COM3"); 
	
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
//Not used in collection, (used for Synch test)
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
