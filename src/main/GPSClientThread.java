package main;

import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

public class GPSClientThread extends Thread {
	
	private DatagramConnection dc;
	private Thread thd;
	Sender gpsSender;
	private Display display;
	public boolean enabled;
	private boolean connected;
	
	private IdenMIDlet parent;
	
	public GPSClientThread(IdenMIDlet m) {
		parent = m;
    	display = Display.getDisplay(m);
    	enabled = false;
	}
	
	public void start() {
		enabled = true;
		thd = new Thread(this);
		thd.start();
	}
	
	public void connect() {
    	try {
    		dc = (DatagramConnection) Connector.open("datagram://" + parent.ipAddress + ":" + parent.port, Connector.WRITE, false);
    		gpsSender = new Sender(dc,false);
    	} catch (Exception ex) {
            /*Alert a = new Alert("GPSClient", "Cannot connect to host server",
                    null, AlertType.ERROR);
            a.setTimeout(5000);
            display.setCurrent(a);*/
    		try {
				sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void disconnect() {
    	if (dc != null) {
    		try {
    			dc.close();
    		} catch (Exception ex) {
    			/*Alert a = new Alert("GPSClient", "Exception Caught during disconnect",
                        			null, AlertType.ERROR);
    			a.setTimeout(5000);
    			display.setCurrent(a);*/
    		}
    	}
    }
    
    public void run() {
    	connect();
    	String prevLat = "-1";
    	String prevLon = "-1";
    	while (enabled) {
    		while (dc == null) {
    			// keep trying to obtain connection until a connection is received (thus, when dc != null)
    			connect();
    		}
 		
    		Date startTimeGPS = new Date();
    		    		
    		try {
    			while (parent.requestingGPS) {
    				sleep(500); 				   				 
    			}
    			GPSUtil gpsUtil = new GPSUtil();
    			String gpsString = gpsUtil.getGPS(GPSUtil.DELAY_LOW);
    			if (!gpsString.equals("")) {
    				if (gpsString.indexOf("_") != -1) {
    					// obtained gps fix not available; send a gps fix unavailable if prev fix was good. Otherwise send nothing
    					if (!prevLat.equals("0") && !prevLon.equals("0")) {
    						// NOTE: not using reportingStartTime and reportingEndTime for GPS fix unavailable (timestamp unavailable) 
    						if (parent.carrierIPReportingEnabled) {
    	    					gpsSender.send(null, parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
    	        			}
    	        			else {
    	        				gpsSender.send(null, parent.phoneID + ";" + gpsString);                				
    	        			}
    					}
    					prevLat = "0";
    					prevLon = "0";
    				}
    				else {
        				// gps fix good. Send if gps has moved a far enough distance from current point
        				String temp = gpsString.substring(4); // remove 'GPS,'
        				String currentLat = temp.substring(0,temp.indexOf(","));
        				temp = temp.substring(temp.indexOf(",") + 1);
        				String currentLon = temp.substring(0,temp.indexOf(","));
        				
        				if (currentLat.startsWith("N")) {   				
        					currentLat = currentLat.substring(currentLat.indexOf(" ") + 1);
        				}
        				else {
        					currentLat = "-" + currentLat.substring(currentLat.indexOf(" ") + 1);
        				}
        				
        				if (currentLon.startsWith("E")) {   				
        					currentLon = currentLon.substring(currentLon.indexOf(" ") + 1);
        				}
        				else {
        					currentLon = "-" + currentLon.substring(currentLon.indexOf(" ") + 1);
        				}
       				
        				double latDiff = Double.parseDouble(currentLat) - Double.parseDouble(prevLat);
        				double lonDiff = Double.parseDouble(currentLon) - Double.parseDouble(prevLon);
        				
        				/*Alert a = new Alert("GPSClient", "latDiff = " + latDiff + "\nlonDiff = " + lonDiff,
                                null, AlertType.ERROR);
        							a.setTimeout(5000);
        							display.setCurrent(a);*/
        				
        				if (prevLat.equals("-1") && prevLon.equals("-1")) {
        					// never reported in GPS previously at all; report in GPS
        					if (parent.withinReportingTimerange(gpsString)) {
	        					if (parent.carrierIPReportingEnabled) {
	    	    					gpsSender.send(null, parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
	    	        			}
	    	        			else {
	    	        				gpsSender.send(null, parent.phoneID + ";" + gpsString);                				
	    	        			}
        					}
        					
        					prevLat = currentLat;
            				prevLon = currentLon;
        				}
        				else if (prevLat.equals("0") && prevLon.equals("0")) {
        					// last GPS was gps fix unavailable; report in GPS
        					if (parent.withinReportingTimerange(gpsString)) {
	        					if (parent.carrierIPReportingEnabled) {
	    	    					gpsSender.send(null, parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
	    	        			}
	    	        			else {
	    	        				gpsSender.send(null, parent.phoneID + ";" + gpsString);                				
	    	        			}
        					}
        					
        					prevLat = currentLat;
            				prevLon = currentLon;
        				}
        				else if (latDiff > 0.0005 || latDiff < -0.0005 || lonDiff > 0.0005 || lonDiff < -0.0005) {
        					// gps fix has moved enough away from previous position; send gps
        					if (parent.withinReportingTimerange(gpsString)) {
	        					if (parent.carrierIPReportingEnabled) {
	    	    					gpsSender.send(null, parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
	    	        			}
	    	        			else {
	    	        				gpsSender.send(null, parent.phoneID + ";" + gpsString);                				
	    	        			}
        					}
        					
        					prevLat = currentLat;
            				prevLon = currentLon;
        				}
        			}
    			}
    		}
    		catch (Exception ex) {
                /*Alert a = new Alert("GPSClient", "Exception Caught in GPSClient",
                                    null, AlertType.ERROR);
                a.setTimeout(5000);
                display.setCurrent(a);*/
            }
    		        		
    		Date endTimeGPS = new Date();
    		
    		// Sleep for 'GPSInterval' seconds before getting another GPS fix (minus time during execution)
    		long waitTime = (parent.GPSInterval * 1000) - (endTimeGPS.getTime() - startTimeGPS.getTime());
    		if (waitTime > 0) {
    			try {
    				sleep(waitTime);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	disconnect();
    }

    public void stop() {
    	enabled = false;
    }
}
