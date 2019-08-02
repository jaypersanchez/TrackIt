package main;

public class RequestGPSThread implements Runnable {
	
	private RequestGPS rGPS;
	public boolean enabled;
	
	public RequestGPSThread(RequestGPS reqGPS) {
		enabled = false;
		rGPS = reqGPS;
	}
	
	public void start() {
		enabled = true;
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		GPSUtil gpsUtil = new GPSUtil();
		String temp = gpsUtil.getGPS(GPSUtil.DELAY_LOW);
		if (enabled) rGPS.RequestGPSThreadCallback(temp); 			
	}
	
	public void stop() {
		enabled = false;
	}
}
