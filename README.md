# Leap Data Collection Application
For Dallas

# Synopsis:

Multi-threaded java data collection application for the Leap Motion Controller.
This application is used to collect hand motion data for tasks such as pointing and grasping for later analysis.
It includes Arduino files needed for temporal sync with the Eyelink eye-tracker.

# Sync:
1. The Eyelink program (Experiment Builder) can change the TTL level of the status pins of the parallel port on the host computer. The pre-set TTL level is high. When recording is begining, the TTL level changes from high to low, then back to high (inverted pulse). When recording has ended, the TTL level again changes mimicking the inverted pulse.
2. When the Arduino pin 12 changes logic state twice, a '1' is written to the serial buffer. 
3. The collection application has a RS232 protocol daemon thread which monitors for a serial event. When there is a serial event, the recording turns on/off (by changing the 'isStreaming' boolean variable).
4. The daemon thread clears the serial buffer by outputting to the console.

### Sync Setup:
1. Ensure the parallel port cable is plugged into the parallel port of the host computer.
2. Two Arduino wires can be plugged into the parallel port head. They plug into the Arduino (Ground and Pin 12). 
  - The placement of the two wires is written on the parallel port cable.
3. Plug in the Arduino Uno into the collection device (using USB port 3).
  - Verify using the Device Manager.
4. Open Command Prompt and 'cd' into the directory containing the runnable-jar.
  - ex. ``` cd C:\Users\Nisarg\Desktop\Eclipse_Workspace\Leap_Motion_Data_Collector\bin\Data_Collector ```
5. Use the run command to run the application.
  - ```java -jar name-of-application.jar ```

  
# FAQs:

- There is an error appearing when I run the application and the sync is not working.

Confirm that the Arduino is plugged into the correct port (port 3). Also confirm that there are no other programs open and using port 3.
One can change the port being used by the Arduino Uno by going into device manager, and going into 'Advanced Port Settings'.

- There are strings appearing on the Command Prompt during collection.
  
Those are due to the clearing of the serial buffer. The Arduino and colllection application communicate via the serial buffer, and it must be cleared by outputting to the console. It is a byproduct of the sync and does not impede it.

- The Leap is not being recognized by the application.

If there is no visible light inside the Leap, it is not turned on. Verify it is plugged into the USB port. Reset the Leap service by going into 'Services'.

Try running the applications that come with the Leap Motion Sensor. If they do not pickup any hands over the Leap verify that the most current version of the firmware has been uploaded to the Leap via the Leap Control Panel.

- Leap is tracking poorly, it cuts off even when my hand is in range.

If the Leap is heating up, try letting it rest for half an hour before trying again. The longer is is used, the less reliable the data streamed is.
Change the lighting conditions, incandesent lights dont seem to be benefical to the quality of data. Make sure there are no significant sources of infa-red radiation being pointed at the Leap when streaming data.


![alt text][logo]

[logo]: https://github.com/nisargbhavsar/Leap_Collector_MT/blob/master/2016-07-04.png "Collection Application"
