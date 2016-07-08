package Data_Collector;

public class StartThreads {

	public StartThreads() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
	LeapThread thread1 = new LeapThread ();
	thread1.start();
	
	RS232Protocol thread2 = new RS232Protocol ();
	thread2.setDaemon(true);
	thread2.run();

	}

}
