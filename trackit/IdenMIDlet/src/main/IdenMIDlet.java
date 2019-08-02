package main;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.*;
import java.io.*;

public class IdenMIDlet extends MIDlet implements CommandListener {
	
    private static Display display;
    public Form f;
    private StringItem si1, si2, si3;
    
    private boolean isPaused;
    public boolean requestingGPS;
    public GPSClient gpsClient;
    public CommandsListener gpsCommandsListener;
    private AdvancedMenu advancedMenu;
    private PasswordMenu passwordMenu;

    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Command advMenuCommand = new Command("Admin", Command.SCREEN, 1);
    
    public String phoneID;
    public String ipAddress;
    public int port;
    public boolean GPSEnabled;
    public int GPSInterval;
    public String carrierIP;
    public boolean carrierIPReportingEnabled;
    public String reportingStartTime, reportingEndTime;
    public String firmwareVersion;
    private int numberOfTimesStartAppHasBeenCalled = 0;
    
    private RecordStore recordStore;
    
    public IdenMIDlet() {
    	
    	requestingGPS = false;
    	    	
    	try {
    	    recordStore = RecordStore.openRecordStore("settings", true);
    	} catch (RecordStoreException rse) {
    	    rse.printStackTrace();
    	}
    	
    	int numOfRecords = -1;
    	
    	try {
    		numOfRecords = recordStore.getNumRecords();
    	} catch (RecordStoreException rse) {
    	    rse.printStackTrace();
    	}
    	
    	if (numOfRecords == 0) {
    		// new record store; must initialize phone with a phoneId and default values
    		Random rand = new Random();
    		phoneID = Double.toString(rand.nextDouble()).substring(2);
    		if (phoneID.indexOf("E") != -1) phoneID = phoneID.substring(0, phoneID.indexOf("E"));
    		//ipAddress = "209.226.80.182";
    		//ipAddress = "localhost";
    		ipAddress = "69.90.136.41";
    		//port = 4000;
    		port = 5555;
    		GPSEnabled = true;
    		GPSInterval = 60;
    		carrierIP = "0.0.0.0";
    		carrierIPReportingEnabled = true;
    		reportingStartTime = "00:00";
    		reportingEndTime = "24:00";
    		firmwareVersion = "1.0.2";
 		    String record = phoneID + "," + ipAddress + "," + port + "," 
 		    					+ "T" + "," + GPSInterval + ","
 		    					+ carrierIP + "," + "T" + ","
 		    					+ reportingStartTime + "," + reportingEndTime + ","
 		    					+ firmwareVersion;
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		DataOutputStream dos = new DataOutputStream(baos);
    		try {
    			dos.writeUTF(record);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		byte[] b = baos.toByteArray();
    		try {
    			recordStore.addRecord(b, 0, b.length);    		
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	else {
    		// retrieve phone settings from RMS
    		byte[] b = {};
    		try {
    			b = recordStore.getRecord(1);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		ByteArrayInputStream bais = new
    			ByteArrayInputStream(b);
    		DataInputStream dis = new
    		DataInputStream(bais);
    		String settings = "";
    		try {
    			settings = dis.readUTF();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		int index = settings.indexOf(",");
    		int indexPrev = 0;
    		String temp;
    		phoneID = settings.substring(0,index);
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		ipAddress = settings.substring(indexPrev + 1, index);
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		port = Integer.parseInt(settings.substring(indexPrev + 1, index));
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		temp = settings.substring(indexPrev + 1, index);
    		if (temp.equals("T"))
    			GPSEnabled = true;
    		else
    			GPSEnabled = false;
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		GPSInterval = Integer.parseInt(settings.substring(indexPrev + 1, index));
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		carrierIP = settings.substring(indexPrev + 1, index);
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		temp = settings.substring(indexPrev + 1, index);
    		if (temp.equals("T"))
    			carrierIPReportingEnabled = true;
    		else
    			carrierIPReportingEnabled = false;
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		reportingStartTime = settings.substring(indexPrev + 1, index);
    		indexPrev = index;
    		index = settings.indexOf(",", index + 1);
    		reportingEndTime = settings.substring(indexPrev + 1, index);
    		firmwareVersion = settings.substring(index + 1);
    	}
    	
    	try {
    		recordStore.closeRecordStore();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	gpsClient = new GPSClient(this);
    	gpsCommandsListener = new CommandsListener(this);
    	
    	if (GPSEnabled) gpsClient.start();
		gpsCommandsListener.start();
		    	
    	display = Display.getDisplay(this);
        f = new Form("Trak It iDEN");
        
        si1 = new StringItem("","The Trak It iDEN application has now loaded.");
        si2 = new StringItem("","Press the 'End Call' button to suspend the program to the background.");
        si3 = new StringItem("Version","1.0.1");
        
        f.append(si1);
        f.append(si2);
        f.append(si3);
        //f.addCommand(exitCommand); // buggy; removing exit option
        f.addCommand(advMenuCommand);
        f.setCommandListener(this);

        display.setCurrent(f);
        
        // send 'startup' event
        try {
        	DatagramConnection dc = (DatagramConnection) Connector.open("datagram://" + this.ipAddress + ":" + this.port,Connector.WRITE,false);
        	Sender sender = new Sender(dc,true);
        	sender.send(null, this.phoneID + ";Startup");
        } catch (Exception ex) {
        	// do nothing       	
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void startApp() {
    	isPaused = false;
    	numberOfTimesStartAppHasBeenCalled++;
        if (numberOfTimesStartAppHasBeenCalled > 5) {
        	// show password screen
        	passwordMenu = new PasswordMenu(this);
        	
        } else {
        	this.notifyPaused();
        }
    }

    public void pauseApp() {
        isPaused = true;
    }

    public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    	
    	/*this.resumeRequest();
    	this.startApp();
    	
    	for (int i = 0 ; i < 999999999 ; i++);
    	try {
    		throw new MIDletStateChangeException();
    	} catch (Exception e) {
    		// do nothing
    	}
    	destroyApp(false);*/
   	
        if (gpsClient != null) gpsClient.stop();
        if (gpsCommandsListener != null) gpsCommandsListener.stop();
        
        // send 'shutdown' event
        try {
        	DatagramConnection dc = (DatagramConnection) Connector.open("datagram://" + this.ipAddress + ":" + this.port,Connector.WRITE,false);
        	Sender sender = new Sender(dc,true);
        	sender.send(null, this.phoneID + ";Shutdown");
        } catch (Exception ex) {
        	// do nothing       	
        }
        
        // must wait to give power off packet a chance to be sent (waiting 5 secs)
        Date startTime = new Date();
        while (true) {
        	Date endTime = new Date();
        	if ((endTime.getTime() - startTime.getTime()) > 5000)
        		break;
        }
    }

    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
        	try {
        		destroyApp(true);
        		// destroyApp(false);
        		notifyDestroyed();
        	} catch (Exception e) {
        		// do nothing
        	}
        } else if (c == advMenuCommand) {
        	advancedMenu = new AdvancedMenu(this);
        }
    }
    
    /**
     * Used to determine if a given gps reading is within the reporting Timerange
     * 
     * @param gpsString - the gps string created by the GPSUtil.getGPS function
     * @return - returns if the gps reading is between the reportingStartTime and reportingEndTime
     */
    
    public boolean withinReportingTimerange(String gpsString) {
    	Date gpsDate = new Date(Long.parseLong(gpsString.substring(gpsString.indexOf("T") + 1)));
    	Calendar myCalendar = Calendar.getInstance();
    	myCalendar.setTime(gpsDate);
    	
    	int startHour = Integer.parseInt(reportingStartTime.substring(0, reportingStartTime.indexOf(":")));
    	int startMin = Integer.parseInt(reportingStartTime.substring(reportingStartTime.indexOf(":") + 1));
    	int endHour = Integer.parseInt(reportingEndTime.substring(0, reportingEndTime.indexOf(":")));
    	int endMin = Integer.parseInt(reportingEndTime.substring(reportingEndTime.indexOf(":") + 1));
    	
    	boolean afterStartTime = false;
    	boolean beforeEndTime = false;
    	
    	/*Alert a = new Alert("GPSClient", "startHour = " + startHour + "\nstartMin = " + startMin
    									+ "\nendHour" + endHour + "\nendMin" + endMin,
                null, AlertType.ERROR);
					a.setTimeout(5000);
					display.setCurrent(a);*/
    	
    	if (myCalendar.get(Calendar.HOUR_OF_DAY) > startHour) {
    		afterStartTime = true;
    	}
    	else if (myCalendar.get(Calendar.HOUR_OF_DAY) == startHour) {
    		if (myCalendar.get(Calendar.MINUTE) >= startMin) {
    			afterStartTime = true;
    		}
    	}
    	if (myCalendar.get(Calendar.HOUR_OF_DAY) < endHour) {
    		beforeEndTime = true;
    	}
    	else if (myCalendar.get(Calendar.HOUR_OF_DAY) == endHour) {
    		if (myCalendar.get(Calendar.MINUTE) <= endMin) {
    			beforeEndTime = true;
    		}
    	}
    	
    	if (afterStartTime && beforeEndTime)
    		return true;
    	else  	
    		return false;
    }
}
