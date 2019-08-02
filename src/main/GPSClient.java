package main;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;
import com.motorola.iden.position.*;

public class GPSClient {
	
	private DatagramConnection dc;
	public boolean enabled;
	
	private IdenMIDlet parent;
	
	private GPSClientThread gpsClientThd; 
	
    public GPSClient(IdenMIDlet m) {
    	parent = m;
    	enabled = false;
    }
    
    public void start() {
    	enabled = true;
    	gpsClientThd = new GPSClientThread(parent);
    	gpsClientThd.start();
    }
  
    public void stop() {
    	enabled = false;
    	gpsClientThd.stop();
    }
}
