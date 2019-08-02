/*
 * GenericLocationAPI.java
 *
 * Created on July 5, 2007, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.Criteria;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.location.Coordinates;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.Datagram;
import java.util.Random;

/**
 *
 * @author linuxwannabe
 */
public class GenericLocationAPI extends Thread
{
    LocationProvider lp = null;
    Location loc = null;
    Criteria criteria = null;
    double lat = 0;
    double longi = 0;
    float speed = 0;
    float course = 0;
    String way1 = "";
    String way2 = "";
    MIDlet m = null;
    Display d = null;
    Coordinates cor = null;
    DatagramConnection dc;
    String gpsData;
    Datagram datagram;
    ConfigData config;
    //ManageRMS mrms = null;
   public void run()
    {
        ManageRMS mrms = new ManageRMS();
        try
        {
            //connect to backend
            /**/
            while(dc==null)
            {
                dc = (DatagramConnection) Connector.open("datagram://" + this.config.getIP() +":" + 
                                                     config.getPort(), Connector.WRITE, false);
                /**dc = (DatagramConnection) Connector.open("datagram://" + mrms.ipAddress +":" + 
                                                     mrms.port, Connector.WRITE, false);**/
            }/**/
            
                if(lat>0) 
                { way1="N "; }
                else
                { way1="S "; }
                if(longi>0)
                { way2="E "; }
                else
                { way2="W "; }
               /**gpsData = "28808648279590787"+","+"1.1.1.1"+
                          ";GPS,"+way1+lat+","+way2+longi+","+
                          new Float(this.getSpeed()).toString()+","+new Float(this.getCourse()).toString()+
                          "T"+(System.currentTimeMillis())*1969/10000;**/
               /**gpsData = this.config.getPhoneID()+","+this.config.getCarrierIP()+
                          ",1;GPS,"+way1+this.getLat()+","+way2+getLong()+","+
                          new Float(this.getSpeed()).toString()+","+new Float(this.getCourse()).toString()+
                          "T"+(System.currentTimeMillis())*1969/10000;**/
               mrms.getCfg();
               String begin = mrms.reportingStartTime;
               String end = mrms.reportingEndTime;
               jcs.Util util = new Util();
               /**if (latDiff > 0.0005 || latDiff < -0.0005 || lonDiff > 0.0005 || lonDiff < -0.0005)**/
               double latDiff = getLat() - util.getStoredLat();
               double lonDiff = getLong() - util.getStoredLong();
               gpsData = this.config.getPhoneID()+","+mrms.carrierIP+
                          ",1;GPS,"+way1+this.getLat()+","+way2+getLong()+","+
                          new Float(this.getSpeed()).toString()+","+new Float(this.getCourse()).toString()+
                          "T"+(System.currentTimeMillis())*1969/10000; 
               if (latDiff > 0.0005 || latDiff < -0.0005 || lonDiff > 0.0005 || lonDiff < -0.0005) {
                    // gps fix has moved enough away from previous position; send gps
                    byte[] buffer = gpsData.getBytes();
                    Datagram dg = dc.newDatagram(buffer,buffer.length);
                    dc.send(dg);
               }
               dc.close();
               this.notifyAll();
               mrms = null;
               util = null;
               //sleep(1000);
               /**Alert a = new Alert("GenericLocatin API",gpsData,null,AlertType.INFO);
               a.setTimeout(10000);
               d.setCurrent(a);**/
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //d.setCurrent( new Alert("GenericLocationAPI Error", e.getMessage(),null, AlertType.ERROR));
        }
    }
    /** Creates a new instance of GenericLocationAPI */
    public GenericLocationAPI(Display _d,ConfigData _config) 
    {
        try
        {
            //mrms = new ManageRMS();
            d = _d;
            config = _config;
            criteria = new Criteria();
            criteria.setPreferredResponseTime(20);
            criteria.setVerticalAccuracy(500);
            criteria.setHorizontalAccuracy(500);
            lp = LocationProvider.getInstance(criteria);
            loc = lp.getLocation(20); //10 seconds interval
            cor = loc.getQualifiedCoordinates();
            if(cor != null ) 
            {
                // Use coordinate information
                lat = cor.getLatitude();
                longi = cor.getLongitude();
                speed = loc.getSpeed();
                course = loc.getCourse();
            }
            else {
                if (lp.getState()==lp.TEMPORARILY_UNAVAILABLE) {
                    //send response
                    ManageRMS m = new ManageRMS();
                    //"9433900420840426,10.19.114.124;GPS,ACC_NOT_ATTAIN_ASSIST_DATA_UNAV"
                    String message = config.phoneID+","+m.carrierIP+
                                     ",;,ACC_NOT_ATTAIN_ASSIST_DATA_UNAV";
                    jcs.CommandResponse res = new CommandResponse(this.config,message);
                    res.notifyAll();
                }
            }
        }//exception
        catch(Exception e)
        {
            e.printStackTrace();
            //d.setCurrent( new Alert("Generic Class Error", e.getMessage(),null, AlertType.ERROR));
        }
    }
    
   
     public double getLat()
    {
        return lat;
    }
    public double getLong()
    {
        return longi;
    }
    public float getSpeed()
    {
        return speed;
    }
    public float getCourse()
    {
        return course;
    }
    public void setCourse(float val)
    {
        course = val;
    }
    public void setSpeed(float val)
    {
        this.speed = val;
    }
    public void setLong(double val)
    {
        this.longi = val;
    }
    public void setLat(double val)
    {
        this.lat = val;
    }
}


