package main;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

public class RequestGPSSendThread extends Thread {
	
	private IdenMIDlet parent;
	private Display display;
	private DatagramConnection dc;
    Sender gpsSender;
    private String gpsString;
	
	public RequestGPSSendThread(IdenMIDlet m, String sGPS) {
		parent = m;
		display = Display.getDisplay(parent);
		gpsString = sGPS;
	}
	
	public void start() {
		Thread t = new Thread(this);
		t.start();
	}
	
	public void connect() {
    	try {
    		dc = (DatagramConnection) Connector.open("datagram://" + parent.ipAddress + ":" + parent.port);
    		gpsSender = new Sender(dc,true);
    	} catch (Exception ex) {
            Alert a = new Alert("RequestGPS", "Cannot connect to host server",
                    null, AlertType.ERROR);
            a.setTimeout(5000);
            display.setCurrent(a);
    	}
    }
    
    public void disconnect() {
    	if (dc != null) {
    		try {
    			dc.close();
    		} catch (Exception ex) {
    			Alert a = new Alert("RequestGPS", "Exception Caught during disconnect",
                        			null, AlertType.ERROR);
    			a.setTimeout(5000);
    			display.setCurrent(a);
    		}
    	}
    }
		
	public void run() {
		connect();	
		try {
			gpsSender.send(null, gpsString);
		}
		catch (Exception ex) {
            Alert a = new Alert("RequestGPS", "Exception Caught in RequestGPS",
                                null, AlertType.ERROR);
            a.setTimeout(5000);
            display.setCurrent(a);
        }
		/*try {
			sleep(3000);
		} catch (Exception e) {
			// do nothing
		}
    	disconnect();*/
		
		// above lines commented out; gpsSender is in autoClose mode
	}

}
