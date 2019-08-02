/*
 * ConfigData.java
 *
 * Created on July 12, 2007, 4:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.*;
import java.io.*;


/**
 *
 * @author linuxwannabe
 */
public class ConfigData
{
    //class variables
    public Form f;
    private StringItem si1, si2, si3;
    private boolean isPaused;
    public boolean requestingGPS;
    //public GPSClient gpsClient;
    //public CommandsListener gpsCommandsListener;
    //private AdvancedMenu advancedMenu;
    //private PasswordMenu passwordMenu;
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
    private RecordStore recordStore;
    Display display = null;
    
    //command handler called from CommandListener sCFG,gCFG,GPS
    public String cmdHandler(String _cmdString)
    {
        String success = null;
        //StringBuffer str = new StringBuffer(_cmdString);
        int strIdx = 5; //starting index for parsing data for commands; sCFG,<begin data parse>
        String cmd = new Character(_cmdString.charAt(0)).toString();
        return success;
    }
    
    //CFG
    String getGPS()
    {
        return "";
    }
    //sCFG
    public String setConfig(String rcvString)
    {
        String data = null;
        try 
        {
            Alert v = new Alert("cmdHandler","invoked:"+rcvString,null,AlertType.INFO);
            v.setTimeout(10000);
            this.display.setCurrent(v);
            int indexPrev = rcvString.indexOf(",");
    	    int index = rcvString.indexOf("," , 5);
            String temp;
            Alert q = new Alert("cmdHandler","rcvString:"+indexPrev+":"+index,null,AlertType.INFO);
            q.setTimeout(10000);
            this.display.setCurrent(q);
            this.ipAddress = rcvString.substring(indexPrev + 1, index);
    	    indexPrev = index;
    	    index = rcvString.indexOf("," , index + 1);
    	    this.port = Integer.parseInt(rcvString.substring(indexPrev + 1, index));
    	    indexPrev = index;
    	    index = rcvString.indexOf("," , index + 1);
    	    temp = rcvString.substring(indexPrev + 1, index);
            Alert a = new Alert("cmdHandler","temp:"+temp,null,AlertType.INFO);
            a.setTimeout(10000);
            this.display.setCurrent(a);
    	    if (temp.equals("T"))
                this.GPSEnabled = true;
            else
                this.GPSEnabled = false;
            
    	    indexPrev = index;
    	    index = rcvString.indexOf("," , index + 1);
    	    this.GPSInterval = Integer.parseInt(rcvString.substring(indexPrev + 1, index));
    	    indexPrev = index;
    	    index = rcvString.indexOf("," , index + 1);
    	    this.carrierIP = rcvString.substring(indexPrev + 1, index);
    	    indexPrev = index;
    	    index = rcvString.indexOf("," , index + 1);
    	    temp = rcvString.substring(indexPrev + 1, index);
    	    if (temp.equals("T"))
                this.carrierIPReportingEnabled = true;
    	    else
                this.carrierIPReportingEnabled = false;
            indexPrev = index;
            index = rcvString.indexOf("," , index + 1);
            this.reportingStartTime = rcvString.substring(indexPrev + 1, index);
            indexPrev = index;
            index = rcvString.indexOf(";" , index + 1);
            this.reportingEndTime = rcvString.substring(indexPrev + 1, index);
            Alert z = new Alert("cmdHandler","reportingEndTime:"+reportingEndTime,null,AlertType.INFO);
            z.setTimeout(10000);
            this.display.setCurrent(z);
                    /*RESTART INFO*/
                    // stop GPS Client, and restart GPS Client if necessary
                    //this.gpsClient.stop();
                    //if (this.GPSEnabled) this.gpsClient.start();
                    // save settings to DB
                    String record = "";
            	    String GPSEnabledToken, carrierIPReportingEnabledToken;
                    if (this.GPSEnabled)
                        GPSEnabledToken = "T";
                    else
                        GPSEnabledToken = "F";
                    if (this.carrierIPReportingEnabled)
                        carrierIPReportingEnabledToken = "T";
                    else
                        carrierIPReportingEnabledToken = "F";
    			record = this.phoneID + "," + this.ipAddress + "," + this.port + "," 
    				+ GPSEnabledToken + "," + this.GPSInterval + ","
    				+ this.carrierIP + "," + carrierIPReportingEnabledToken + ","
    				+ this.reportingStartTime + "," + this.reportingEndTime + ","
    				+ this.firmwareVersion;
                        ///
            Alert c = new Alert("cmdHandler","record:"+record,null,AlertType.INFO);
            c.setTimeout(10000);
            this.display.setCurrent(c);
            ///
            		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            		DataOutputStream dos = new DataOutputStream(baos);
            		try 
                        {
            			dos.writeUTF(record);
            		} 
                        catch (Exception e) 
                        {
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
            		DatagramConnection dc2 = (DatagramConnection) Connector.open("datagram://" + 
                                             this.ipAddress + ":" + this.port,Connector.WRITE,false);
        		Sender sender = new Sender(dc2,true);
            		sender.send(null, this.phoneID + ";CFGSet");
                        /**Alert a = new Alert("cmdHandler","config save",null,AlertType.INFO);
                        a.setTimeout(10000);
                        this.display.setCurrent(a);**/
                        } catch (Exception ex) {
                            //ex.printStackTrace();
                            Alert a = new Alert("cmdHandler Error",ex.getMessage(),null,AlertType.INFO);
                            a.setTimeout(10000);
                            this.display.setCurrent(a);
    			}
        return data;
    }//setConfig
    //gCFG
    String getConfig()
    {
        return "";
    }
    
    public void saveCfg(String _ReportingIPAddress,int _ReportingPort,boolean _GPSEnabled,
                        int _GPSInterval,String _CarrierIP,
                        boolean _carrierIPReportingEnabled,
                        String _ReportingStartTime,String _ReportingEndTime)
    {
        try
        {
                //remove current settings
                RecordStore.deleteRecordStore("settings");
                //recreate rms
                RecordStore recordStore = RecordStore.openRecordStore("settings", true);
                //int numOfRecords = -1;
                //numOfRecords = recordStore.getNumRecords();
                // new record store; must initialize phone with a phoneId and default values
    		Random rand = new Random();
    		phoneID = Double.toString(rand.nextDouble()).substring(2);
    		if (phoneID.indexOf("E") != -1) phoneID = phoneID.substring(0, phoneID.indexOf("E"));
    		//linuxwannabe
                ipAddress = "linuxwannabe.homelinux.com";
                port = 7000;
                //debugging local to Roxroy's 
                /**ipAddress = "209.226.80.182";
    		port = 7000;**/
    		//test/production 
                /**ipAddress = "69.90.136.41";
                port = 5555;**/
    		GPSEnabled = true;
    		GPSInterval = 10;
    		carrierIP="1.1.1.1";
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
                dos.writeUTF(record);
    		byte[] b = baos.toByteArray();
    		recordStore.addRecord(b, 0, b.length);
                recordStore.closeRecordStore();
    		//display.setCurrent( new Alert("new rc", this.phoneID,null, AlertType.ERROR) );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void saveCfgDefault(RecordStore recordStore)
    {
        try
        {
                //recordStore = RecordStore.openRecordStore("settings", true);
                //int numOfRecords = -1;
                //numOfRecords = recordStore.getNumRecords();
                // new record store; must initialize phone with a phoneId and default values
    		Random rand = new Random();
    		phoneID = Double.toString(rand.nextDouble()).substring(2);
    		if (phoneID.indexOf("E") != -1) phoneID = phoneID.substring(0, phoneID.indexOf("E"));
    		//linuxwannabe
                ipAddress = "linuxwannabe.homelinux.com";
                port = 7000;
                //debugging local to Roxroy's 
                /**ipAddress = "209.226.80.182";
    		port = 7000;**/
    		//test/production 
                /**ipAddress = "69.90.136.41";
                port = 5555;**/
    		GPSEnabled = true;
    		GPSInterval = 10;
    		carrierIP="1.1.1.1";
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
                dos.writeUTF(record);
    		byte[] b = baos.toByteArray();
    		recordStore.addRecord(b, 0, b.length);
                recordStore.closeRecordStore();
    		//display.setCurrent( new Alert("new rc", this.phoneID,null, AlertType.ERROR) );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void getCfg(RecordStore recordStore)
    {
        try
        {
            // retrieve phone settings from RMS
    		byte[] b = {};
    		b = recordStore.getRecord(1);
    		ByteArrayInputStream bais = new
    			ByteArrayInputStream(b);
    		DataInputStream dis = new
    		DataInputStream(bais);
    		String settings = "";
    		settings = dis.readUTF();
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
    		reportingEndTime= settings.substring(indexPrev + 1, index);
    		firmwareVersion = settings.substring(index + 1);
                recordStore.closeRecordStore();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /** Creates a new instance of ConfigData */
    public ConfigData(Display _display) 
    {
        try 
        {
            display = _display;
            recordStore = RecordStore.openRecordStore("settings", true);
            int numOfRecords = -1;
            numOfRecords = recordStore.getNumRecords();
            if (numOfRecords == 0) 
            {
                //this.saveCfgDefault(recordStore);
                //recordStore.closeRecordStore();
    		// new record store; must initialize phone with a phoneId and default values
    		Random rand = new Random();
    		phoneID = Double.toString(rand.nextDouble()).substring(2);
    		if (phoneID.indexOf("E") != -1) phoneID = phoneID.substring(0, phoneID.indexOf("E"));/**/
    		//linuxwannabe
                /**ipAddress = "linuxwannabe.homelinux.com";
                port = 7000;**/
                //debugging local to Roxroy's 
                /**/ipAddress = "209.226.80.182";
    		port = 7000;/**/
    		//test/production 
                /**ipAddress = "69.90.136.41";
                port = 5555;**/
    		GPSEnabled = true;
    		GPSInterval = 10;
    		carrierIP="1.1.1.1";
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
                dos.writeUTF(record);
    		byte[] b = baos.toByteArray();
    		recordStore.addRecord(b, 0, b.length); 		
    		//display.setCurrent( new Alert("new rc", this.phoneID,null, AlertType.ERROR) );
            }
            else
            {
                //this.getCfg(recordStore);
                //recordStore.closeRecordStore();
                // retrieve phone settings from RMS
    		byte[] b = {};
    		b = recordStore.getRecord(1);
    		ByteArrayInputStream bais = new
    			ByteArrayInputStream(b);
    		DataInputStream dis = new
    		DataInputStream(bais);
    		String settings = "";
    		settings = dis.readUTF();
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
    		reportingEndTime= settings.substring(indexPrev + 1, index);
    		firmwareVersion = settings.substring(index + 1);
                recordStore.closeRecordStore();
                //display.setCurrent( new Alert("rc exist", this.phoneID,null, AlertType.ERROR) );
            }//else
        } 
        catch (Exception rse) //(RecordStoreException rse) 
        {
    	    rse.printStackTrace();
            //display.setCurrent( new Alert("Config Error", rse.getMessage(),null, AlertType.ERROR) );
    	}
    }//ConfigData
    
    public String getEndTime()
    {
        return reportingEndTime;
    }
    public String getStartTime()
    {
        return reportingStartTime;
    }
    public String getCarrierIP()
    {
        return carrierIP;
    }
    public int getGPSInterval()
    {
        return GPSInterval;
    }
    public int getPort()
    {
        return port;
    }
    public String getIP()
    {
        return ipAddress;
    }
    public String getPhoneID()
    {
        return phoneID;
    }
    
    public void setIP(String val)
    {
        this.ipAddress=val;
    }
    public void setPort(int val)
    {
        this.port = val;
    }
    public void setGPSInterval(int val)
    {
      this.GPSInterval = val;
    }
    
    public void setStartTime(String val)
    {
        reportingStartTime = val;
    }
    
    public void setEndTime(String val)
    {
        this.reportingEndTime = val;
    }
        
}
