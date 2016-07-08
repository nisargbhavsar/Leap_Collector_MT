package Data_Collector;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;

import com.leapmotion.leap.*;

import javax.swing.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//import jssc.SerialPortEvent;
//import java.io.File; 
//import java.util.Date; 
//import jxl.*; 
//import jxl.write.*; 

//public class LeapTest extends JFrame implements KeyListener{
public class LeapTest extends JFrame {
	private static final long serialVersionUID = 1L;

	// Declare variables
	static JTextArea frameData;
	private JTextArea[] fingerData;
	private JTextArea [] apertureData;
	private JTextArea [] velocityData; 
	private JButton changeDirectory;
	private JButton pause;
	private JButton resume;
	private JButton calibrate;
	private JButton next_subject;
	private JButton event_file;
	private JButton save;
	//private JLabel status_update;
	static JButton startStream;
	private HintTextField trialNumber;
	private HintTextField duration;
	
	static HintTextField subject_id; //User input in subject id before trials start
	static HintTextField trial_condition; //User input in trial condition after trial end, upto 5 inputs possible, unfilled places filled with 'N/A'
	static HintTextField view_condition; //User input viewing condition after trial ends
	static HintTextField target_location; //User input target location after trial ends
	
	static ArrayList<String> event_subject = new ArrayList<String>(); //Corresponds to subject_id
	static ArrayList<String> event_trialnum = new ArrayList<String>(); //Assumed to go up linearly by 1 until user ends collection
	static ArrayList<String> event_view_condition = new ArrayList<String>(); //Corresponds to view_condition
	static ArrayList<String> event_target_loc = new ArrayList<String>(); //Corresponds to target_location
	static ArrayList<String> event_trial_conditions = new ArrayList<String>(); // Corresponds to trial_condition
	
	private Controller controller = new Controller();
	static LeapListener listener;
	private static FileWriter fileWriter1 = null;
	private static FileWriter fileWriter2 = null;
	private static FileWriter fileWriter3 = null;
	//private static FileWriter fileWriter4 = null;
	private static FileWriter fileWriter5 = null;
	//private static FileWriter fileWriter6 = null;
	//private static FileWriter fileWriter7 = null;
	private static JFileChooser fc = new JFileChooser();
	private static int trial = 1;
	private static int subject_num = 1;
	private static int condition_num = 5;
	public static int recordDuration = 0;
	public static String s_id = null; 
	public static String t_condition =""; 
	public static String t_c = "";
	private static boolean isDirectoryChanged = false;
	private static boolean isStream = false;
	private static boolean isSubjectChanged = false;
	static boolean isCalibrate = false;
	static boolean save_conditionfile = false;
	
	static boolean isTargetLocChanged = false;
	static boolean isViewCondChanged = false;
	
	//Initialize serial communications
//	RS232Protocol serial = new RS232Protocol();
		
	JTextField typingArea;

	/**
	 * 
	 * Constructor for the main
	 */
	LeapTest() {
		super("Leap Motion Data Collector");
		initiateUI();
	}

//	public static void main (String[] args) {
//		// Initiate new window
//		new LeapTest();
//		
//	}

	/**
	 * Initiate UI - Add texts, buttons and button listener
	 * 
	 *	
	 */
	private void initiateUI() {
		this.setResizable(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setSize(1000, 680);//changed from: 800,550

		
		// Initiate widgets
		frameData = new JTextArea();
		frameData.setEditable(false);
		frameData.setOpaque(false);
		frameData.setBounds(10, 10, 600, 500);

		fingerData = new JTextArea[2];
		for (int i = 0; i < 2; i++) {
			fingerData[i] = new JTextArea();
			fingerData[i].setEditable(false);
			fingerData[i].setOpaque(false);
			fingerData[i].setBounds(310+(250*i),10,600,600);
		}
		//fingerData[0].setBounds(310, 10, 600, 600);
		//fingerData[1].setBounds(560, 10, 600, 600);

		//apertureData stores distance between index and thumb 
		//[0] stores aperture data for left hand
		//[1] stores aperture data for right hand
		
		apertureData = new JTextArea[2];
		for (int i=0; i<2; i++){
			apertureData[i] = new JTextArea();
			apertureData[i].setEditable(false);
			apertureData[i].setOpaque(false);
			apertureData[i].setBounds(310+(250*i),510,600,600);//Have to adjust
					
		}
		//velocityData stores velocities of thumb and index fingers
		
		int j =-1;
		velocityData = new JTextArea[6];
		for (int i=0; i<6; i++){
			velocityData[i] = new JTextArea();
			velocityData[i].setEditable(false);
			velocityData[i].setOpaque(false);
			if (i >= 3)
			{
				j+=1;
				velocityData[i].setBounds(310+(155*j),575,200,600);
			}
			else	
				velocityData[i].setBounds(310+(155*i),550,200,600);//Have to adjust
					
		}
		
		// Initiate buttons and set event listeners
		startStream = new JButton("Start Streaming");
		startStream.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!get_isStream()) {		
					listener.startStreaming();
					set_isStream(true);						
					condition_num = 5;
					startStream.setText("Streaming...");
					t_condition = t_condition + " " + t_c;
					
					trial_condition.setText("Trial Condition, Trial: "+ trial);
					view_condition.setText("Viewing Condition,Trial:" + trial);
					target_location.setText("Target Location, Trial:" + trial);
				} else if (get_isStream() && recordDuration == 0) {
					saveFile();
					set_isStream(false);
					startStream.setText("Start Streaming");
					trial_condition.setText("Trial Condition, Trial: "+ (trial-1));
					view_condition.setText("Viewing Condition,Trial:" + (trial-1));
					target_location.setText("Target Location, Trial:" + (trial-1));
				}
			}
		});
		startStream.setBounds(10, 340, 140, 50);
		startStream.setBackground(Color.red);
		
		//Code for keystroke signaling start of streaming
		typingArea = new JTextField(20);
        //typingArea.addKeyListener(this);
        
        getContentPane().add(typingArea, BorderLayout.PAGE_START);
		
		

		duration = new HintTextField("Record Duration (s)");
		duration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(duration.getText()) >= 0) {
						recordDuration = Integer.parseInt(duration.getText());
						JOptionPane.showMessageDialog(null, "Streaming duration changed to " + recordDuration + " seconds",
								"Duration Changed", JOptionPane.PLAIN_MESSAGE);
					} else
						JOptionPane.showMessageDialog(null, "Invalid Number",
								"Error", JOptionPane.PLAIN_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid Number",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		duration.setBounds(150, 340, 140, 50);
		
		subject_id = new HintTextField("Subject ID");
		subject_id.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (subject_id.getText() != null) {
						s_id = subject_id.getText();
						JOptionPane.showMessageDialog(null, "Subject ID changed to: " + s_id + " ",
								"Subject ID Changed", JOptionPane.PLAIN_MESSAGE);
						
						
					} else
						JOptionPane.showMessageDialog(null, "Invalid",
								"Error", JOptionPane.PLAIN_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		subject_id.setBounds(150, 520, 140, 50);
		
		trial_condition = new HintTextField("Please run a trial");
		trial_condition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (condition_num >0){
						if (trial_condition.getText() != null) {
							JOptionPane.showMessageDialog(null, "Added Trial Condition: " + trial_condition.getText() + " for trial:  " + (trial-1),
									"Trial Condition Added, Conditions Left: "+(condition_num), JOptionPane.PLAIN_MESSAGE);
							t_c = trial_condition.getText();
							condition_num = condition_num - 1; 
							event_trial_conditions.add(t_c);
							trial_condition.setText("Trial Condition, Trial: "+ (trial-1));
						}
					} else
						JOptionPane.showMessageDialog(null, "Invalid",
								"Error", JOptionPane.PLAIN_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		trial_condition.setBounds(10, 580, 140, 25);
		
		view_condition = new HintTextField("Please run a trial");
		view_condition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (view_condition.getText() != null) {
						JOptionPane.showMessageDialog(null, "Added Viewing Condition: " + view_condition.getText() + " for trial: " +(trial-1),
								"Viewing Condition Added", JOptionPane.PLAIN_MESSAGE);
						event_view_condition.add(view_condition.getText());
						isViewCondChanged = true;
						
						
					} else{
						JOptionPane.showMessageDialog(null, "Invalid",
								"Error", JOptionPane.PLAIN_MESSAGE);
						isViewCondChanged = false;
					}
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		view_condition.setBounds(10, 605, 140, 25);
		
		trialNumber = new HintTextField("Change Trial #");
		//trialNumber.setEditable(true);
		trialNumber.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(trialNumber.getText()) > 0)
						trial = Integer.parseInt(trialNumber.getText());
					else
						JOptionPane.showMessageDialog(null, "Invalid Number",
								"Error", JOptionPane.PLAIN_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid Number",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		trialNumber.setBounds(150, 460, 140, 50);
		
		target_location = new HintTextField("Please run a trial");
		target_location.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (target_location.getText() != null) {
						JOptionPane.showMessageDialog(null, "Added target location: " + target_location.getText() + " for trial: " +(trial-1),
								"Target Location Added", JOptionPane.PLAIN_MESSAGE);
						event_target_loc.add(target_location.getText());
						isTargetLocChanged = true;
						
					} else
					{
						JOptionPane.showMessageDialog(null, "Invalid",
								"Error", JOptionPane.PLAIN_MESSAGE);
						isTargetLocChanged = false;
					}
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		target_location.setBounds(10, 630, 140, 25);
				
		next_subject = new JButton("Next Subject");
		next_subject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isSubjectChanged = !isSubjectChanged;//true;
				isTargetLocChanged = false;
				isViewCondChanged = false;
				save_conditionfile = true;
				saveFile();
				trial = 1;
				s_id = null; 
				t_condition = "";
				t_c = "";
				subject_num +=1;
				subject_id.setText("Subject ID");
				trial_condition.setText("Please Run a Trial");
				view_condition.setText("Please Run a Trial");
				target_location.setText("Please Run a Trial");
			}
		});
		next_subject.setBounds(150, 580, 140, 25);
		
		event_file = new JButton ("Create Event File");
		event_file.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed (ActionEvent e){
				try {
					save_eventFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				}
			}
		);
		event_file.setBounds(150, 605, 140, 25);
		
		save = new JButton ("Save Comments");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed (ActionEvent e){
				for (int i =condition_num; i>0; i--) //Populate the trial conditions array if not all 5 trial conditions are filled in
				{
					event_trial_conditions.add("N/A");
				}
				if(!isTargetLocChanged)
				{
					event_target_loc.add("N/A");
				}
				if(!isViewCondChanged)
				{
					event_view_condition.add("N/A");
				}
				//System.out.println(event_trial_conditions);
				
				event_trialnum.add(Integer.toString(trial-1));
				event_subject.add(s_id);
				//System.out.println(event_subject);
				}
			}
		);
		save.setBounds(150, 630, 140, 25);
		

		pause = new JButton("Pause Tracking");
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controller.isConnected()) {
					listener.pause();
				}
			}
		});
		pause.setBounds(10, 400, 140, 50);

		resume = new JButton("Resume Tracking");
		resume.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controller.isConnected()) {
					listener.resume();
				}
			}
		});
		resume.setBounds(150, 400, 140, 50);

		changeDirectory = new JButton("Change Directory");
		changeDirectory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				isDirectoryChanged = true;
				// Get custom file directory
				fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("."));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null,
							"Stream Data will be saved to "
									+ fc.getSelectedFile().toString()
									+ "/Leap Motion Data", "Directory Changed",
							JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		changeDirectory.setBounds(10, 460, 140, 50);

		calibrate = new JButton ("Calibrate");
		calibrate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed (ActionEvent e){
				if (!isCalibrate) {
					startStream.setText("Streaming...");
					listener.startStreaming();
					isCalibrate = true;
				} else if (isCalibrate) {
					set_isStream(false);
					saveFile();
					isCalibrate = false;
				}
				}
			}
		);
		calibrate.setBounds(10, 520, 140, 50);
		
		//serial.connect("COM3"); 
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        //serial.disconnect();
		        System.exit(0);
		    }
		});
		
		
		// Add widgets to window
		this.setLayout(null);
		this.add(startStream);
		this.add(duration);
		this.add(changeDirectory);
		this.add(pause);
		this.add(resume);
		this.add(frameData);
		this.add(trialNumber);
		this.add(fingerData[0]);
		this.add(fingerData[1]);
		this.add(apertureData[0]);
		this.add(apertureData[1]);
		for (int i=0; i<6; i++){
			this.add(velocityData[i]);
		}
		this.add(calibrate);
		this.add(subject_id);
		this.add(trial_condition);
		this.add(next_subject);
		this.add(view_condition);
		this.add(target_location);
		this.add(event_file);
		this.add(save);
		//this.add(status_update);

		// Define leap motion event listener and add listener to leap motion
		listener = new LeapListener(frameData, fingerData, apertureData, velocityData);
		controller.addListener(listener);

		// Check if leap motion is connected to the computer
		if (!controller.isConnected()) {
			frameData
				.setText("Leap motion not found. \nMake sure your leap motion is connected and you have the latest SDK installed");
		}

		// Display the window
		setVisible(true);
	}

	public static String getTrialNumber() {
		return trial + "";
	}
	public static void save_eventFile() throws IOException
	{	
		String event_file_date = JOptionPane.showInputDialog("Please input the date (DDMMYY)");
		String event_file_name = JOptionPane.showInputDialog("Please input the event file name");
		File dir;
		if (!isDirectoryChanged) {
		dir = new File(System.getProperty("user.dir")
				+ "/Leap Motion Data/"+event_file_date);
		}
		else
		{
			dir = new File(fc.getSelectedFile().toString()
					+ "/Leap Motion Data/"+event_file_date);
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File Event_File = new File(dir, event_file_name + "_" + event_file_date + ".txt");
		Event_File.createNewFile();
		fileWriter3 = new FileWriter(Event_File);
		String event_file_data = "";
		event_file_data =  "Subject_Name,Trial_Number,View_Condition,Target_Location,"
				+ "Trial_Condition_5,Trial_Condition_4,Trial_Condition_3,Trial_Condition_2,"
				+ "Trial_Condition_1" + "\r\n";
		for (int i = 0; i < event_trialnum.size(); i += 1) {
			event_file_data = event_file_data + event_subject.get(i) + "," + event_trialnum.get(i) + "," + 
					event_view_condition.get(i) + "," + event_target_loc.get(i);
			
			for(int j = (5*(i+1))-1; j>=(5*i);j--)
				{
					event_file_data = event_file_data + ","+event_trial_conditions.get(j);
				}
			event_file_data = event_file_data + "\r\n";
		}
		
		fileWriter3.write(event_file_data);
		fileWriter3.close();
	}
	public static void saveFile() {
		startStream.setText("Start Streaming");
		try {
			File dir;
			// Check if there is data in the stream data ArrayList
			if (listener.stopStreaming1().size() == 0 && listener.stopStreaming5().size()==0 && !save_conditionfile) {
				trial--;
				return;
			}
			// Create new directory if it does not exist
			if (!isDirectoryChanged) {
				if(listener.stopStreaming5().size()!=0){
				dir = new File(System.getProperty("user.dir")
						+ "/Leap Motion Data/Subject " + subject_num + "_" + s_id + "");
				}
				else {
				dir = new File(System.getProperty("user.dir")
					+ "/Leap Motion Data/Subject " + subject_num + "_" + s_id + "");	
				}
					
			} else {
				if(listener.stopStreaming5().size()!=0){
//					dir = new File(fc.getSelectedFile().toString()
//							+ "/Leap Motion Data/Trial " + getTrialNumber() + "");
					dir = new File(fc.getSelectedFile().toString()
							+ "/Leap Motion Data/Subject " + subject_num + "_" + s_id + "");
					}
					else {
//						dir = new File(fc.getSelectedFile().toString()
//								+ "/Leap Motion Data/Trial " + getTrialNumber() + "");
						dir = new File(fc.getSelectedFile().toString()
								+ "/Leap Motion Data/Subject " + subject_num + "_" + s_id + "");
					}
				
			}
			
			System.out.println(dir.toString());

			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			if (!isCalibrate){

				if (save_conditionfile)
				{
				File condition_File = new File (dir, "Trial Conditions.txt");
				condition_File.createNewFile();
				fileWriter2 = new FileWriter(condition_File);
				fileWriter2.write(s_id + t_condition);
				fileWriter2.close();
				isSubjectChanged = false;
				save_conditionfile = false;
				return;
				
				}
				
			File Master_File = new File(dir, "Trial_" + getTrialNumber() + ".txt");
			
			Master_File.createNewFile();
	

			// Write the file to computer
			fileWriter1 = new FileWriter(Master_File);

			// Get data from ArrayList and produce a string
			String streamData1 = "";

			ArrayList<String> data1 = listener.stopStreaming1();

			for (int i = 0; i < data1.size() - 1; i += 2) {
				streamData1 += String.format("%-40s%20s\n", data1.get(i),
						data1.get(i + 1));
			}

			fileWriter1.write(streamData1);

			// Remove all data in ArrayList for next stream
			listener.stopStreaming1().clear();
			
			try {
				fileWriter1.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
			else if (isCalibrate)
			{
				File calFile = new File(dir, "Calibration.txt");
				calFile.createNewFile();
				fileWriter5 = new FileWriter(calFile);
				String streamData5 = "";
				ArrayList<String> data5 = listener.stopStreaming5();
				for (int i = 0; i < data5.size() - 1; i += 2) {
					streamData5 += String.format("%-40s%20s\n", data5.get(i),
							data5.get(i + 1));
				}
				fileWriter5.write(streamData5);
				listener.stopStreaming5().clear();
				
				try {
						fileWriter5.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		
		finally {
			if(!isCalibrate){trial++;}
		}
		
		
	}
	public static void endStream() {
		isStream = false;
	}

//	@Override
//	public void keyTyped(KeyEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void keyPressed(KeyEvent e) {
//	
//		if (!get_isStream()) {
//			startStream.setText("Streaming...");
//			listener.startStreaming();
//			set_isStream(true);
//		} else if (get_isStream()) {
//			saveFile();
//			//set_isStream(false);
//			//startStream.setText("Start Streaming");
//		}
//		
//	}
//
//	@Override
//	public void keyReleased(KeyEvent e) {
//		// TODO Auto-generated method stub
//		
//	}

	public static boolean get_isStream() {
		return isStream;
	}

	public static void set_isStream(boolean Stream) {
		isStream = Stream;
	}

//	public static void keyPressed(SerialPortEvent event) {
//		if (!get_isStream()) {
//			startStream.setText("Streaming...");
//			listener.startStreaming();
//			set_isStream(true);
//		} else if (get_isStream()) {
//			saveFile();
//			//set_isStream(false);
//			//startStream.setText("Start Streaming");
//		}
//		
//	}
}

class HintTextField extends JTextField implements FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		this.showingHint = true;
		super.addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			showingHint = true;
		}
	}
	
//	@Override
//	public void focusLost(FocusEvent e) {
//		if (this.getText().isEmpty()) {
//			super.setText(hint);
//			showingHint = true;
//		}
//	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}
}
