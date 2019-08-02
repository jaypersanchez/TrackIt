package main;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import java.io.*;
import java.util.Date;

public class RequestGPS implements CommandListener {
	
	private IdenMIDlet parent;
    private Display display;
    private Form f;
    private StringItem si1, si2;
        
    private String gpsString;
    
    private Command cmdBack = new Command("Back", Command.ITEM, 1);
    private Command sendGPS = new Command("Send", Command.ITEM, 1);
    
    RequestGPSThread rGPSthd;
	
	public RequestGPS(IdenMIDlet m) {
        parent = m;
        display = Display.getDisplay(parent);
        f = new Form("Request GPS");
        si1 = new StringItem("","Requesting GPS. Please Wait...");
        si2 = new StringItem("","");
        f.append(si1);
        f.append(si2);
        f.addCommand(cmdBack);
        f.setCommandListener(this);
        display.setCurrent(f);
        
        rGPSthd = new RequestGPSThread(this);
        rGPSthd.start();
    }
	
	public void RequestGPSThreadCallback(String sGPS) {
		gpsString = sGPS;
		if (gpsString.indexOf("GPS,") != -1) {
			// gps found
			si2.setText(gpsString.substring(4,gpsString.indexOf("T")));
			f.addCommand(sendGPS);
		}
		else {
			if (gpsString == "") si2.setText("Error: Please Try Again");
			else si2.setText("Error: " + gpsString);
		}
	}
	
	public void commandAction(Command c, Displayable s) {
		if (c == sendGPS) {
			RequestGPSSendThread rGPSsendThd;
			if (parent.carrierIPReportingEnabled) {
				rGPSsendThd = new RequestGPSSendThread(parent,parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
			}
			else {
				rGPSsendThd = new RequestGPSSendThread(parent,parent.phoneID + ";" + gpsString);								
			}
			rGPSsendThd.start();
			display.setCurrent(parent.f);
		}
		if (c == cmdBack) {
			rGPSthd.enabled = false;
			display.setCurrent(parent.f);
		}
	}
}
