/*
 * ManageRMS.java
 *
 * Created on July 19, 2007, 5:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

//import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.io.*;
//import javax.microedition.lcdui.*;
import java.io.*;

/**
 *
 * @author linuxwannabe
 */
public class ManageRMS {
    
    RecordStore recordStore = null;
    public boolean requestingGPS;
    public String phoneID;
    public String ipAddress;
    public int port;
    public boolean GPSEnabled;
    public int GPSInterval;
    public String carrierIP;
    public boolean carrierIPReportingEnabled;
    public String reportingStartTime, reportingEndTime;
    public String firmwareVersion;
    
    public ManageRMS() {
        try {
           recordStore = RecordStore.openRecordStore("settings", false);
           /**if (recordStore.getNumRecords()==0) {
               setCfg("sCFG,206.1.1.1,7000,T,10,1.1.1.1,T,00:00,24:00");
           }
           else {
               getCfg();
           }**/
        }
        catch(Exception e){
            //System.out.println(e.getMessage());
        }
    }
    
    /*
     *sCFG,ReportingIPAddress,ReportingPort,GPSEnabled,GPSInterval,CarrierIP, 
     *carrierIPReportingEnabledToken,ReportingStartTime,ReportingEndTime"
     */
    public synchronized void setCfg(String _string) {
        try {
            parse(_string);
            /**System.out.println(this.ipAddress+":"+this.port+":"+this.GPSEnabled+":"+this.GPSInterval+
                               ":"+this.carrierIP+":"+this.carrierIPReportingEnabled+":"+
                               this.reportingStartTime+":"+this.reportingEndTime);**/
        }
        catch(Exception e) {
            //System.out.println(e.getMessage());
        }
    }
    
    void parse(String data) {
        try {
            int indexPrev = data.indexOf(",");
    	    int index = data.indexOf("," , 5);
    	    String temp;
    	    this.ipAddress = data.substring(indexPrev + 1, index);
    	    indexPrev = index;
    	    index = data.indexOf("," , index + 1);
    	    this.port = Integer.parseInt(data.substring(indexPrev + 1, index));
    	    indexPrev = index;
    	    index = data.indexOf("," , index + 1);
    	    temp = data.substring(indexPrev + 1, index);
    	    if (temp.equals("T"))
    	    	this.GPSEnabled = true;
    	    else
    	    	this.GPSEnabled = false;
    	    indexPrev = index;
    	    index = data.indexOf("," , index + 1);
    	    this.GPSInterval = Integer.parseInt(data.substring(indexPrev + 1, index));
    	    indexPrev = index;
    	    index = data.indexOf("," , index + 1);
    	    this.carrierIP = data.substring(indexPrev + 1, index);
    	    indexPrev = index;
    	    index = data.indexOf("," , index + 1);
    	    temp = data.substring(indexPrev + 1, index);
    	    if (temp.equals("T"))
    	    	this.carrierIPReportingEnabled = true;
    	    else
    	        this.carrierIPReportingEnabled = false;
    	    indexPrev = index;
    	    index = data.indexOf("," , index + 1);
    	    this.reportingStartTime = data.substring(indexPrev + 1, index);
    	    indexPrev = index;
    	    index = data.indexOf(";" , index + 1);
    	    this.reportingEndTime = data.substring(indexPrev + 1, index);
            // stop GPS Client, and restart GPS Client if necessary
    	    /**this.gpsClient.stop();
            if (this.GPSEnabled) this.gpsClient.start();**/
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch(Exception e) {
            
        }
    }
    
    public synchronized void getCfg() {
        try {
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
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
