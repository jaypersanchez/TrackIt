package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;


public class AdvancedMenu implements CommandListener {
	
	private static final String ON = "ON";
    private static final String OFF = "OFF";

    private static final String[] options = {ON, OFF};

    private IdenMIDlet parent;
    private Display display;
    private Form f;
    private TextField tfReportingIP;
    private TextField tfPort;
    private ChoiceGroup cgGPSEnabled;
    private TextField tfGPSInterval;
    private TextField tfCarrierIP;
    private ChoiceGroup cgCarrierIPReportingEnabled;
    private TextField tfReportingStartTime;
    private TextField tfReportingEndTime;
    private StringItem siPhoneID;
    
    private RequestGPS requestGPS;

    private Command backCommand = new Command("Back", Command.ITEM, 1);
    private Command saveCommand = new Command("Save", Command.SCREEN, 1);
    private Command requestGPSCommand = new Command("Request GPS", Command.SCREEN, 1);
    
    private RecordStore recordStore;
    
    public AdvancedMenu(IdenMIDlet m) {
    	
    	parent = m;
    	display = Display.getDisplay(parent);
    	
    	f = new Form("Admin Menu");
        
        tfReportingIP = new TextField("Host Address:", parent.ipAddress, 30, TextField.NON_PREDICTIVE);
        tfPort = new TextField("Port:", Integer.toString(parent.port), 10, TextField.NUMERIC);
        cgGPSEnabled = new ChoiceGroup("GPS Enabled:",
                                            Choice.EXCLUSIVE, options, null);
        if (!parent.GPSEnabled) {
        	cgGPSEnabled.setSelectedIndex(1, true);
        }
        tfGPSInterval = new TextField("GPS Interval:", Integer.toString(parent.GPSInterval), 10, TextField.NUMERIC);
        tfCarrierIP = new TextField("Carrier IP:", parent.carrierIP, 30, TextField.NON_PREDICTIVE);
        cgCarrierIPReportingEnabled = new ChoiceGroup("Carrier IP Reporting:",
        									Choice.EXCLUSIVE, options, null);
        if (!parent.carrierIPReportingEnabled) {
        	cgCarrierIPReportingEnabled.setSelectedIndex(1, true);
        }
        tfReportingStartTime = new TextField("Reporting Start Time:", parent.reportingStartTime, 10, TextField.NON_PREDICTIVE);
        tfReportingEndTime = new TextField("Reporting End Time:", parent.reportingEndTime, 10, TextField.NON_PREDICTIVE);
        siPhoneID = new StringItem("","Phone ID:\n" + parent.phoneID);
        
        f.append(tfReportingIP);
        f.append(tfPort);
        f.append(cgGPSEnabled);
        f.append(tfGPSInterval);
        f.append(tfCarrierIP);
        f.append(cgCarrierIPReportingEnabled);
        f.append(tfReportingStartTime);
        f.append(tfReportingEndTime);
        f.append(siPhoneID);
                
        f.addCommand(backCommand);
        f.addCommand(saveCommand);
        f.addCommand(requestGPSCommand);
        f.setCommandListener(this);

        display.setCurrent(f);
    }

    public void commandAction(Command c, Displayable s) {
        if (c == backCommand) {
        	display.setCurrent(parent.f);
        } else if (c == saveCommand) {
        	parent.ipAddress = tfReportingIP.getString();
        	parent.port = Integer.parseInt(tfPort.getString());
        	if (cgGPSEnabled.getSelectedIndex() == 0) {// 'ON'
        		parent.GPSEnabled = true;
        	}
        	else {// 'OFF'
        		parent.GPSEnabled = false;
        	}
        	parent.GPSInterval = Integer.parseInt(tfGPSInterval.getString());
        	parent.carrierIP = tfCarrierIP.getString();
        	if (cgCarrierIPReportingEnabled.getSelectedIndex() == 0) {// 'ON'
        		parent.carrierIPReportingEnabled = true;
        	}
        	else {// 'OFF'
        		parent.carrierIPReportingEnabled = false;        		
        	}
        	parent.reportingStartTime = tfReportingStartTime.getString();
        	parent.reportingEndTime = tfReportingEndTime.getString();
        	
        	// Switch On/Off gpsClient if necessary
        	
        	if (parent.GPSEnabled) {// 'ON'
        		parent.gpsClient.stop();
        		parent.gpsClient.start();
        	}
        	else {// 'OFF' (!parent.GPSEnabled)
        		if (parent.gpsClient.enabled) parent.gpsClient.stop();
        	}
        	
        	// save configuration to record store
        	String record = "";
    		String GPSEnabledToken, carrierIPReportingEnabledToken;

			if (parent.GPSEnabled)
				GPSEnabledToken = "T";
			else
				GPSEnabledToken = "F";
			if (parent.carrierIPReportingEnabled)
				carrierIPReportingEnabledToken = "T";
			else
				carrierIPReportingEnabledToken = "F";
			
			record = parent.phoneID + "," + parent.ipAddress + "," + parent.port + "," 
						+ GPSEnabledToken + "," + parent.GPSInterval + ","
						+ parent.carrierIP + "," + carrierIPReportingEnabledToken + ","
						+ parent.reportingStartTime + "," + parent.reportingEndTime + ","
						+ parent.firmwareVersion;
        	
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		DataOutputStream dos = new DataOutputStream(baos);
    		try {
    			dos.writeUTF(record);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		byte[] b = baos.toByteArray();
    		try {
    			recordStore = RecordStore.openRecordStore("settings", true);
    			recordStore.setRecord(1, b, 0, b.length);
    			recordStore.closeRecordStore();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		Alert a = new Alert("Admin Menu", "\n\n\nSettings Saved",
        			null, AlertType.CONFIRMATION);
    				a.setTimeout(3000);
    				display.setCurrent(a);
        } else if (c == requestGPSCommand) {
        	requestGPS = new RequestGPS(parent);
        }
    }

}
