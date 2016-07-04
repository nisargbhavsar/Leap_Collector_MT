package Data_Collector;
import javax.swing.*;

import com.leapmotion.leap.*;

import java.awt.Color;
import java.util.ArrayList;

import jssc.SerialPortException;

public class LeapListener extends Listener {

	// Declare variables
	private JTextArea frameData;
	private JTextArea[] fingerData;
	private JTextArea [] apertureData;
	private JTextArea [] velocityData; 
	private boolean isPause = false;
	private static boolean isStream = false;
	private Frame currentFrame;
	private static HandList hands;
	private FingerList fingers;
	private String handInfo = "";
	private String[] grasp_apertureInfo = new String[2]; 
	private String[] fingerInfo = new String[2];
	private String[] velocityInfo = new String[6];
	private ArrayList<String> hand_data = new ArrayList<String>();
//	private ArrayList<String> indexfinger_data = new ArrayList<String>();
//	private ArrayList<String> thumb_data = new ArrayList<String>();
//	private ArrayList<String> wrist_position_data = new ArrayList<String>();
	private ArrayList<String> calibrate_data = new ArrayList<String>();
	//private ArrayList<String> avg_sagittal_position = new ArrayList<String>();
	private long initialTimstamp = 0;
	static int counter = 0;
	static int counter2 = 0;
	
	@SuppressWarnings("unused")
	private RS232Protocol serial; //Initialize serial communication
	//float startTime =-1;
	
	public static final double dist = 0.0;	//distance of leap motion sensor to the participant
	//ASSUMPTION: Leap Motion sensor is centered relative to participant	
	
	
	/**
	 * Constructor for the class
	 * 
	 * @param frameData
	 *            Text Area that shows the data of the leap motion
	 */
	public LeapListener(JTextArea frameData, JTextArea[] fingerData, JTextArea[] apertureData, JTextArea [] velocityData, RS232Protocol serial) {
		this.frameData = frameData;
		this.fingerData = fingerData;
		this.apertureData = apertureData;
		this.velocityData = velocityData;
		this.serial = serial;
	}

	/**
	 * Run this method when leap motion is connected to the computer. Enable
	 * various gesture detections
	 */
	@Override
	public void onConnect(Controller controller) {
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
	}

	/**
	 * Run this method when leap motion is disconnected to the computer. Tell
	 * the user that leap motion is disconnected
	 */
	@Override
	public void onDisconnect(Controller controller) {
		frameData.setText("Leap motion disconnected.");
		try {
			RS232Protocol.port.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run this method when a new frame is available. Set info text to the text
	 * area
	 */
	@Override
	public void onFrame(Controller controller) {
		// Get a reference of the frame, hands and finger if the user did not
		// pause tracking
		if (!isPause) {
			currentFrame = controller.frame();
			hands = currentFrame.hands();
			fingers = currentFrame.fingers();
					
		}
		//Streaming Calibration Data
		if (LeapTest.isCalibrate)
		{
			counter = (int) ((currentFrame.timestamp() - initialTimstamp) / 1000000) + 1;
			counter2 = (int) ((currentFrame.timestamp() - initialTimstamp) / 10000) + 1;
			LeapTest.startStream.setBackground(Color.green);
			
			if (initialTimstamp == 0) {
				initialTimstamp = currentFrame.timestamp();
				System.out.println(initialTimstamp + "");
				counter = LeapTest.recordDuration;
			}
			
			if (LeapTest.recordDuration != 0
					&& LeapTest.recordDuration < (currentFrame.timestamp() - initialTimstamp) / 1000000 + 1) {
				LeapTest.saveFile();
			}
			
			if (hands.count() == 1){
			Finger index = hands.get(0).fingers().get(1);
			float x = index.tipPosition().getX();
			float y = index.tipPosition().getY();
			float z = index.tipPosition().getZ();
			
			calibrate_data.add(String.format("%f %f %f",x, y, z));
			calibrate_data.add((currentFrame.timestamp() - initialTimstamp)
					/ 1000 + " ");
			}
			else if (hands.count() == 0)
			{
				LeapTest.startStream.setBackground(Color.yellow);
				float time = (currentFrame.timestamp() - initialTimstamp)/ 1000;
				float null_value = 999;
				
				calibrate_data.add(String.format("%f %f %f",null_value, null_value, null_value));
				calibrate_data.add(time + " ");
				
			}
		}
		// Streaming Data
		else if (isStream) {
			counter = (int) ((currentFrame.timestamp() - initialTimstamp) / 1000000) + 1;
			counter2 = (int) ((currentFrame.timestamp() - initialTimstamp) / 10000) + 1;
			LeapTest.startStream.setBackground(Color.green);
			if (hands.count() == 1) {
				// Get reference Timestamp
				if (initialTimstamp == 0) {
					initialTimstamp = currentFrame.timestamp();
					System.out.println(initialTimstamp + "");
					counter = LeapTest.recordDuration;
				}

				if (LeapTest.recordDuration != 0
						&& LeapTest.recordDuration < (currentFrame.timestamp() - initialTimstamp) / 1000000 + 1) {
					LeapTest.saveFile();
				}

//				// Add hand data
//				hand_data.add(String.format("%f %f %f", hands.get(0)
//						.palmPosition().getX(), hands.get(0).palmPosition()
//						.getY(), hands.get(0).palmPosition().getZ()));
//				hand_data.add((currentFrame.timestamp() - initialTimstamp)
//						/ 1000 + " ");
				//Wrist Position
				Vector wrist_position = controller.frame().hands().frontmost().arm().wristPosition();
				float [] w_pos = new float [3];
				w_pos[0]=wrist_position.getX();
				w_pos[1]=wrist_position.getY();
				w_pos[2]=wrist_position.getZ();
				
				
				//Add Thumb Data
				Finger thumb = hands.get(0).fingers().get(0);
				Finger index = hands.get(0).fingers().get(1);
				float x1 = thumb.tipPosition().getX();//can use a float array
				float y1 = thumb.tipPosition().getY();
				float z1 = thumb.tipPosition().getZ();
				float x2 = index.tipPosition().getX();
				float y2 = index.tipPosition().getY();
				float z2 = index.tipPosition().getZ();
				
				hand_data.add(String.format("%f %f %f %f %f %f %f %f %f %f %f %f", x2,y2,z2, 
						hands.get(0).palmPosition().getX(), hands.get(0).palmPosition()
						.getY(), hands.get(0).palmPosition().getZ(),x1, y1, z1,
						w_pos[0],w_pos[1],w_pos[2]));			
				hand_data.add((currentFrame.timestamp() - initialTimstamp)
						/ 1000 + " ");
				
//				// Add index finger data
//				indexfinger_data.add(String.format("%f %f %f", x2, y2, z2, x1, y1, z1));
//				indexfinger_data.add((currentFrame.timestamp() - initialTimstamp)
//						/ 1000 + " ");
//				
//				
//				
//				thumb_data.add (String.format("%f %f %f", x1, y1, z1));
//				thumb_data.add((currentFrame.timestamp() - initialTimstamp) / 1000 + " ");
////				
////				//Add Velocity Data
////				Vector thumb_velocity = thumb.tipVelocity();
////				Vector index_velocity = index.tipVelocity();
////				Vector palm_velocity = hands.get(0).palmVelocity();
////				
////				float [] vel = new float [6];
////				vel[0] = thumb_velocity.getX()-palm_velocity.getX();
////				vel[1] = thumb_velocity.getY()-palm_velocity.getY();
////				vel[2] = thumb_velocity.getZ()-palm_velocity.getZ();
////				vel[3] = index_velocity.getX()-palm_velocity.getX();
////				vel[4] = index_velocity.getY()-palm_velocity.getY();
////				vel[5] = index_velocity.getZ()-palm_velocity.getZ();
////				
////				velocity_data.add (String.format("%f %f %f %f %f %f", vel[0], vel[1], vel[2], vel[3], vel[4], vel[5]));
////				velocity_data.add((currentFrame.timestamp() - initialTimstamp) / 1000 + "");
////			
////				//Add thumb and index fingers height
////				height_thumb_index_data.add(String.format("%f %f", y1,y2));
////				
////				
////				height_thumb_index_data.add((currentFrame.timestamp() - initialTimstamp) / 1000 + "");
////				
//				
//				
//				
//				wrist_position_data.add(String.format("%f %f %f", w_pos[0],w_pos[1],w_pos[2]));
//				wrist_position_data.add((currentFrame.timestamp() - initialTimstamp) / 1000 + " ");			
//				
////				//Sagittal Position (thumb ->z,x, index->z,x)
////				double thumb_sag_pos = Math.sqrt(Math.pow((z1 - dist),2)+Math.pow(x1,2));
////				double index_sag_pos = Math.sqrt(Math.pow((z2 - dist),2)+Math.pow(x2,2));
////				
////				sagittal_position_data.add(String.format("%f %f",thumb_sag_pos,index_sag_pos));
////				sagittal_position_data.add((currentFrame.timestamp() - initialTimstamp) / 1000 + "");			
//				
//				
			}
			
			//If lost track of the hands during motion, all data values will have 999
			else if (hands.count() == 0)
			{
				counter = (int) ((currentFrame.timestamp() - initialTimstamp) / 1000000) + 1;
				counter2 = (int) ((currentFrame.timestamp() - initialTimstamp) / 10000) + 1;
				LeapTest.startStream.setBackground(Color.yellow);
					// Get reference Timestamp
					if (initialTimstamp == 0) {
						initialTimstamp = currentFrame.timestamp();
						System.out.println(initialTimstamp + "");
						counter = LeapTest.recordDuration;
					}

					if (LeapTest.recordDuration != 0
							&& LeapTest.recordDuration < (currentFrame.timestamp() - initialTimstamp) / 1000000 + 1) {
						LeapTest.saveFile();
					}
				float time = (currentFrame.timestamp() - initialTimstamp)/ 1000 ;
				float null_value = 999;
				
				hand_data.add(String.format("%f %f %f %f %f %f %f %f %f %f %f %f",null_value, 
						null_value, null_value, null_value, null_value, null_value, null_value, 
						null_value, null_value, null_value, null_value, null_value));
				hand_data.add(time + " ");
				
//				indexfinger_data.add(String.format("%f %f %f",null_value, null_value, null_value));
//				indexfinger_data.add(time + " ");
//				
//				thumb_data.add(String.format("%f %f %f",null_value, null_value, null_value));
//				thumb_data.add(time + " ");
//				
//				wrist_position_data.add(String.format("%f %f %f",null_value, null_value, null_value));
//				wrist_position_data.add(time + " ");
				
			}
		
		
		} 
		
		else {
			counter = 0;
			counter2=0;
			LeapTest.startStream.setBackground(Color.red);
			LeapTest.endStream();
		}

		// Basic info for the leap motion on screen
		handInfo = "Frame Data: "
				+ String.format("\nCurrent Frames Per Seconds: %.0f",
						currentFrame.currentFramesPerSecond())
				+ "\nTimestamp: " + currentFrame.timestamp() + " μs"
				+ "\nNumber of hands: " + hands.count()
				+ "\nNumber of fingers: " + fingers.count() + "\n\n" + "Trial "
				+ LeapTest.getTrialNumber() + " - " + getTimer()
				+ "\n\nHand data: ";
		fingerInfo[0] = "Finger Data: ";
		fingerInfo[1] = "";

		if (hands.count() == 0) {
			handInfo += "\n\nNo hands are detected";
			fingerInfo[0] += "\n\nNo fingers are detected";
			grasp_apertureInfo[0]= "";
			grasp_apertureInfo[1]= "No fingers are detected";
			for (int i=1; i<6; i++)
				velocityInfo[i]= "";
			velocityInfo[0]= "No fingers are detected";
			
		} else {
			generateHandInfo(hands.count());
			generateFingerInfo(hands.count());
		}

		// Set text to text area
		frameData.setText(handInfo);
		fingerData[0].setText(fingerInfo[0]);
		fingerData[1].setText(fingerInfo[1]);
		apertureData[0].setText(grasp_apertureInfo[0]);
		apertureData[1].setText(grasp_apertureInfo[1]);
		for (int i =0; i<6; i++)
			velocityData[i].setText(velocityInfo[i]);
		
	}

	private String getTimer() {
		
//		float currTime = 0;
//		if (isStream)
//		{
//			currTime = System.nanoTime() - initialTimstamp;
//			if (initialTimstamp ==-1)
//			{
//				initialTimstamp = System.nanoTime();
//				currTime = 0;
//			}
//			
//			return String.format("%.0f",currTime%1e11);
//		}
//		return "NotStreaming";
		
		
		int temp = counter;
		int temp2 = counter2;
		int second = 0;
		int minute = 0;
		int millisecond =0;
		while (temp2>=100){
		while (temp >= 60) {
			temp -= 60;
			minute++;
		}
		temp2-=100;
		second ++;
		}
		millisecond = temp2;
		return String.format("%02d:%02d:%02d", minute, second,millisecond);
	}

	/**
	 * Pause the data tracking
	 */
	public void pause() {
		isPause = true;
	}

	/**
	 * Resume the data tracking
	 */
	public void resume() {
		isPause = false;
	}

	/**
	 * Generate info for hands and add to an info string
	 * 
	 * @param numberOfHands
	 */
	private void generateHandInfo(int numberOfHands) {
		for (int i = 0; i < numberOfHands; i++) {
			handInfo += "\n\nHand ID: " + hands.get(i).id();
			if (hands.get(i).isLeft()) {
				handInfo += "\nType: left hand";
			} else {
				handInfo += "\nType: right hand";
			}
			handInfo += "\nDirection: "
					+ String.format("(%.1f,%.1f,%.1f)", hands.get(i)
							.direction().get(0), hands.get(i).direction()
							.get(1), hands.get(i).direction().get(2))
					+ "\nPalm position: "
					+ String.format("(%.1f,%.1f,%.1f)", hands.get(i)
							.palmPosition().get(0), hands.get(i).palmPosition()
							.get(1), hands.get(i).palmPosition().get(2))
					+ " mm";
		}
	}

	/**
	 * Generate info for fingers and add to an info array
	 * 
	 * @param numberOfHands
	 */
	private void generateFingerInfo(int numberOfHands) {
		float velocity []= new float[6];
		for (int i = 0; i < numberOfHands; i++) {
			FingerList temp = hands.get(i).fingers();
			for (int j = 0; j < temp.count(); j++) {
				if (j==0)
				{
					float x1 = temp.get(j).tipPosition().get(0);//can use a float array
					float y1 = temp.get(j).tipPosition().get(1);
					float z1 = temp.get(j).tipPosition().get(2);
					float x2 = temp.get(j+1).tipPosition().get(0);
					float y2 = temp.get(j+1).tipPosition().get(1);
					float z2 = temp.get(j+1).tipPosition().get(2);
					double aperture_size = Math.sqrt(Math.pow((double)(x1-x2),2)+Math.pow((double)(y1-y2),2)+Math.pow((double)(z1-z2),2));
					
					grasp_apertureInfo[i]=null;
					grasp_apertureInfo[i]="\nGrasp Aperture of hand with ID " + hands.get(i).id() + String.format(": %.1f", aperture_size); 	
				}
				
				
				velocity[0] = temp.get(0).tipVelocity().getX()-hands.get(0).palmVelocity().getX();
				velocity[1] = temp.get(0).tipVelocity().getY()-hands.get(0).palmVelocity().getY();
				velocity[2] = temp.get(0).tipVelocity().getZ()-hands.get(0).palmVelocity().getZ();
				velocity[3] = temp.get(1).tipVelocity().getX()-hands.get(0).palmVelocity().getX();
				velocity[4] = temp.get(1).tipVelocity().getY()-hands.get(0).palmVelocity().getY();
				velocity[5] = temp.get(1).tipVelocity().getZ()-hands.get(0).palmVelocity().getZ();
				
				for (int ic=0; ic<6; ic++){
					
					switch (ic) {
					case 0:
						velocityInfo[ic] = "Velocity of Thumb (x): ";
						break;
					case 1:
						velocityInfo[ic] = "Velocity of Thumb (y): ";
						break;
					case 2:
						velocityInfo[ic] = "Velocity of Thumb (z): ";
						break;
					case 3:
						velocityInfo[ic] = "Velocity of Index (x): ";
						break;
					case 4:
						velocityInfo[ic] = "Velocity of Index (y): ";
						break;
					case 5:
						velocityInfo[ic] = "Velocity of Index (z): ";
					}
					
					velocityInfo[ic]+=(String.format("%.1f",velocity[ic]));
				}
				//assume java has adequate garbage collection
				//velocityInfo[]((currentFrame.timestamp() - initialTimstamp) / 1000 + "");
				fingerInfo[i] += "\n\nFinger ID: " + temp.get(j).id()
						+ "\nType: ";
				switch (j) {
				case 0:
					fingerInfo[i] += "Thumb";
					break;
				case 1:
					fingerInfo[i] += "Index finger";
					break;
				case 2:
					fingerInfo[i] += "Middle finger";
					break;
				case 3:
					fingerInfo[i] += "Ring finger";
					break;
				case 4:
					fingerInfo[i] += "Pinky finger";
					break;
				}
				fingerInfo[i] += "\nBelongs to hand with ID: "
						+ hands.get(i).id()
						+ String.format("\nLength: %.1f mm", temp.get(j)
								.length())
						+ String.format("\nTip position: (%.1f,%.1f,%.1f)",
								temp.get(j).tipPosition().get(0), temp.get(j)
										.tipPosition().get(1), temp.get(j)
										.tipPosition().get(2));
			}
		}
	}

	/**
	 * Start streaming data
	 */
	public void startStreaming() {
		isStream = true;
	}

	/**
	 * Stop stream data
	 * 
	 * @return an ArrayList of data
	 */
	public ArrayList<String> stopStreaming1() {
		isStream = false;
		initialTimstamp = 0;
		return hand_data;
	}

//	public ArrayList<String> stopStreaming2() {
//		isStream = false;
//		initialTimstamp = 0;
//		return indexfinger_data;
//	}
//	public ArrayList<String> stopStreaming3() {
//		isStream = false;
//		initialTimstamp = 0;
//		return thumb_data;
//	}
//	
//	public ArrayList<String> stopStreaming4() {
//		isStream = false;
//		initialTimstamp = 0;
//		return wrist_position_data;
//	}
//	
	public ArrayList<String> stopStreaming5() {
		isStream = false;
		initialTimstamp = 0;
		return calibrate_data;
	}
	
}
