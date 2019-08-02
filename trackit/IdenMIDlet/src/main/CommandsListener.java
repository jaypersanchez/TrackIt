package main;

import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;

import javax.wireless.messaging.*;

public class CommandsListener extends Thread {
	
	private IdenMIDlet parent;
	//private MessageConnection mc;
	private DatagramConnection dc;
	private Thread thd;
	private Display display;
	public boolean enabled;
	private RecordStore recordStore;
	
	public CommandsListener(IdenMIDlet m) {
		parent = m;
    	display = Display.getDisplay(m);
    	enabled = false;
    }
	
	public void start() {
    	enabled = true;
    	thd = new Thread(this);
    	thd.start();
    }
	
	public void openConnection() {
    	try {
    		/*mc = (MessageConnection) Connector.open("sms://:5557",Connector.READ,false);*/
    		dc = (DatagramConnection) Connector.open("datagram://:5557",Connector.READ,false);
    	} catch (Exception ex) {
            /*Alert a = new Alert("CommandsListener", "Cannot open connection",
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
    
    public void closeConnection() {
    	if (dc != null) {
    		try {
    			dc.close();
    		} catch (Exception ex) {
    			/*Alert a = new Alert("CommandsListener", "Exception Caught during closeConnection",
                        			null, AlertType.ERROR);
    			a.setTimeout(5000);
    			display.setCurrent(a);*/
    		}
    	}
    }
	
	public void run() {
		openConnection();
        while (enabled) {
        	while (dc == null) {
    			// keep trying to obtain connection until a connection is received (thus, when dc != null)
        		openConnection();
        	}
        	// Message msg = null;
        	String rcvString = "";
        	try {
        		// Testing
        		
        		/*Alert a = new Alert("CommandsListener", "Receiving data...",
            			null, AlertType.ERROR);
        		a.setTimeout(5000);
        		display.setCurrent(a);
        		
        		//System.out.println("Receiving data...");
     		
        		// END Testing
        		
        		/*
        		 * SMS
        		 * 
        		 *msg = mc.receive();
        		if (msg instanceof TextMessage) {
                    TextMessage tmsg = (TextMessage) msg;
                    //  Handle the text message...
                    rcvString = tmsg.getPayloadText();
                }*/
        		
        		Datagram dg = dc.newDatagram(100);
        		dc.receive(dg);
        		rcvString = new String(dg.getData(), 0, dg.getLength());
        		
        		// TESTING
        		        		
        		/*Alert a = new Alert("CommandsListener", rcvString,
        				null, AlertType.ERROR);
        		a.setTimeout(5000);
        		display.setCurrent(a);*/
        		
        		// END TESTING
        		
        		//System.out.println("Received data = " + rcvString);
        	}
        	catch (Exception e) {
        		
        		// Testing
        		
        		/*Alert a = new Alert("CommandsListener", "Exception caught during receiving data...",
            			null, AlertType.ERROR);
        		a.setTimeout(5000);
        		display.setCurrent(a);*/
        		
        		/*Alert a2 = new Alert("CommandsListener", "jeff1\n" + e.getMessage(),
            			null, AlertType.ERROR);
        		a2.setTimeout(5000);
        		display.setCurrent(a2);*/
        		
        		// END Testing
        		
        		
                e.printStackTrace();
            }
        	if (rcvString.indexOf("GPS") != -1) {
        		parent.requestingGPS = true;
        		try {
            		// Testing
            		
            		/*Alert a = new Alert("CommandsListener", "Received GPS String. Making GPS request and transmitting..",
                			null, AlertType.ERROR);
            		a.setTimeout(5000);
            		display.setCurrent(a);*/
        			
        			//System.out.println("Processing GPS request");
           		
            		// END Testing
        			GPSUtil gpsUtil = new GPSUtil();
        			try {
        				String gpsString = gpsUtil.getGPS(GPSUtil.DELAY_LOW);
            			if (gpsString.indexOf("GPS,") != -1) {
            				//System.out.println("sending gps (2)");
            				DatagramConnection dc2 = (DatagramConnection) Connector.open("datagram://" + parent.ipAddress + ":" + parent.port,Connector.WRITE,false);
                			Sender sender = new Sender(dc2,true);
                			if (parent.carrierIPReportingEnabled) {
                				sender.send(null, parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
                			}
                			else {
                				sender.send(null, parent.phoneID + ";" + gpsString);                				
                			}
            			}
            			else {
            				//System.out.println("sending gps (sleeping first)");
            				sleep(500);
            				gpsString = gpsUtil.getGPS(GPSUtil.DELAY_LOW);
            				DatagramConnection dc2 = (DatagramConnection) Connector.open("datagram://" + parent.ipAddress + ":" + parent.port,Connector.WRITE,false);
                			Sender sender = new Sender(dc2,true);
                			if (parent.carrierIPReportingEnabled) {
                				sender.send(null, parent.phoneID + "," + parent.carrierIP + ";" + gpsString);
                			}
                			else {
                				sender.send(null, parent.phoneID + ";" + gpsString);                				
                			}
            			}
        			} catch (Exception ex) {
        				/*Alert a2 = new Alert("CommandsListener", "Exception Caught trying to send GPS",
                    			null, AlertType.ERROR);
        				a2.setTimeout(5000);
        				display.setCurrent(a2);*/
        			}
        		}
        		catch (Exception ex) {
        			// Testing        			
            		/*Alert a = new Alert("CommandsListener", "Exception caught obtaining GPS",
                			null, AlertType.ERROR);
            		a.setTimeout(5000);
            		display.setCurrent(a);
            		Alert a2 = new Alert("CommandsListener", ex.getMessage(),
                			null, AlertType.ERROR);
            		a2.setTimeout(5000);
            		display.setCurrent(a2);*/
            		// END Testing
        			//ex.printStackTrace();
                }
        		parent.requestingGPS = false;
    		}
        	else if (rcvString.indexOf("gCFG") != -1) { // get configuration of phone
        		// Testing
        		
        		/*Alert a = new Alert("CommandsListener", "Received GPS String. Making GPS request and transmitting..",
            			null, AlertType.ERROR);
        		a.setTimeout(5000);
        		display.setCurrent(a);*/
    			
    			//System.out.println("Processing getConfig request");
        		
        		// END Testing
    			try {
    				String getConfigString, GPSEnabledToken, carrierIPReportingEnabledToken;

    				if (parent.GPSEnabled)
    					GPSEnabledToken = "T";
    				else
    					GPSEnabledToken = "F";
    				if (parent.carrierIPReportingEnabled)
    					carrierIPReportingEnabledToken = "T";
    				else
    					carrierIPReportingEnabledToken = "F";
    				
    				getConfigString = "CFG," + parent.ipAddress + "," + parent.port + "," 
	    								+ GPSEnabledToken + "," + parent.GPSInterval + ","
	    								+ parent.carrierIP + "," + carrierIPReportingEnabledToken + ","
	    								+ parent.reportingStartTime + "," + parent.reportingEndTime + ","
	    								+ parent.firmwareVersion;
    				
    				DatagramConnection dc2 = (DatagramConnection) Connector.open("datagram://" + parent.ipAddress + ":" + parent.port,Connector.WRITE,false);
        			Sender sender = new Sender(dc2,true);
    				sender.send(null, parent.phoneID + ";" + getConfigString);
    				
    				//System.out.println("getConfig sent");
    			} catch (Exception ex) {
    				/*Alert a2 = new Alert("CommandsListener", "Exception Caught trying to send config",
                			null, AlertType.ERROR);
    				a2.setTimeout(5000);
    				display.setCurrent(a2);*/
    			}
    		}
        	else if (rcvString.indexOf("sCFG") != -1) { // set configuration of phone
        		// Testing
        		
        		/*Alert a = new Alert("CommandsListener", rcvString,
            			null, AlertType.ERROR);
        		a.setTimeout(5000);
        		display.setCurrent(a);*/
    			
    			//System.out.println("Processing setConfig request.");
    			//System.out.println(rcvString);
        		
        		// END Testing
    			try {
    				int indexPrev = rcvString.indexOf(",");
    	    		int index = rcvString.indexOf("," , 5);
    	    		String temp;
    	    		parent.ipAddress = rcvString.substring(indexPrev + 1, index);
    	    		indexPrev = index;
    	    		index = rcvString.indexOf("," , index + 1);
    	    		parent.port = Integer.parseInt(rcvString.substring(indexPrev + 1, index));
    	    		indexPrev = index;
    	    		index = rcvString.indexOf("," , index + 1);
    	    		temp = rcvString.substring(indexPrev + 1, index);
    	    		if (temp.equals("T"))
    	    			parent.GPSEnabled = true;
    	    		else
    	    			parent.GPSEnabled = false;
    	    		indexPrev = index;
    	    		index = rcvString.indexOf("," , index + 1);
    	    		parent.GPSInterval = Integer.parseInt(rcvString.substring(indexPrev + 1, index));
    	    		indexPrev = index;
    	    		index = rcvString.indexOf("," , index + 1);
    	    		parent.carrierIP = rcvString.substring(indexPrev + 1, index);
    	    		indexPrev = index;
    	    		index = rcvString.indexOf("," , index + 1);
    	    		temp = rcvString.substring(indexPrev + 1, index);
    	    		if (temp.equals("T"))
    	    			parent.carrierIPReportingEnabled = true;
    	    		else
    	    			parent.carrierIPReportingEnabled = false;
    	    		indexPrev = index;
    	    		index = rcvString.indexOf("," , index + 1);
    	    		parent.reportingStartTime = rcvString.substring(indexPrev + 1, index);
    	    		indexPrev = index;
    	    		index = rcvString.indexOf(";" , index + 1);
    	    		parent.reportingEndTime = rcvString.substring(indexPrev + 1, index);
    	    		
    	    		// stop GPS Client, and restart GPS Client if necessary
    	    		parent.gpsClient.stop();
            		if (parent.GPSEnabled) parent.gpsClient.start();

            		// save settings to DB
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
            		
                	//System.out.println("Preparing to save Config. record = " + record);
                	
            		/*Alert a3 = new Alert("CommandsListener", "Preparing to save Config to device.",
                			null, AlertType.ERROR);
            		a3.setTimeout(5000);
            		display.setCurrent(a3);*/
                	
            		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            		DataOutputStream dos = new DataOutputStream(baos);
            		try {
            			dos.writeUTF(record);
            		} catch (Exception e) {
            			e.printStackTrace();
            			/*Alert a2 = new Alert("CommandsListener", "Exception 1.",
                    			null, AlertType.ERROR);
                		a2.setTimeout(5000);
                		display.setCurrent(a2);*/
            		}
            		byte[] b = baos.toByteArray();
            		try {
            			recordStore = RecordStore.openRecordStore("settings", true);
            			recordStore.setRecord(1, b, 0, b.length);
            			recordStore.closeRecordStore();
            		} catch (Exception e) {
            			e.printStackTrace();
            			/*Alert a2 = new Alert("CommandsListener", "Exception 2.",
                    			null, AlertType.ERROR);
                		a2.setTimeout(5000);
                		display.setCurrent(a2);*/
            		}
            		
            		//System.out.println("Config Set. Sending confirm");
            		
            		/*Alert a2 = new Alert("CommandsListener", "Config Set. Sending confirmation.",
                			null, AlertType.ERROR);
            		a2.setTimeout(5000);
            		display.setCurrent(a2);*/
            		
            		DatagramConnection dc2 = (DatagramConnection) Connector.open("datagram://" + parent.ipAddress + ":" + parent.port,Connector.WRITE,false);
        			Sender sender = new Sender(dc2,true);
            		sender.send(null, parent.phoneID + ";CFGSet");
            		
            		/*Alert a2 = new Alert("CommandsListener", "done",
                			null, AlertType.ERROR);
            		a2.setTimeout(5000);
            		display.setCurrent(a2);*/
            		
            		//System.out.println("setConfig done");
    	    		
    			} catch (Exception ex) {
    				/*Alert a2 = new Alert("CommandsListener", "Exception Caught in config set",
                			null, AlertType.ERROR);
    				a2.setTimeout(5000);
    				display.setCurrent(a2);*/
    				ex.printStackTrace();
    			}
    		}
        }
        closeConnection();
    }
	
	public void stop() {
    	enabled = false;
    }
}
