/*
 * LocateTruck.java
 *
 * Created on January 30, 2007, 7:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.*;
import java.io.*;
import java.util.Date;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
/**
 *
 * @author linuxwannabe
 */
public class LocateTruck 
{
    //class variables
    //String url = "http://trucom2.rushtrucking.com/cgi-opn2/cellpos.pgm?";
    String url = null;
    Display display = null;
    //String frequency = null;
    
    private void sendPosition(String _deviceAddress,int _latdegrees,int _longdegrees,int _spd,int _dir)
    {
        String localInfo = "";
        try
        {
            if(_spd==0||_dir==0)
            {
                localInfo = url+"lat="+_latdegrees+"&lon="+_longdegrees; 
            }
            else
            {
                localInfo = url+"lat="+_latdegrees+"&lon="+_longdegrees+"&spd="+_spd+"&dir="+_dir;
            }
            display.setCurrent( new Alert("Current Location", localInfo,null, AlertType.CONFIRMATION) );
            //System.out.println(localInfo);
            //HttpConnection httpConn = (HttpConnection)Connector.open(localInfo);
            //InputStream in = httpConn.openInputStream();
            //in.close();
            //httpConn.close();
            //System.out.println("Location Sent: " + new Date(System.currentTimeMillis()));
            
        }
        catch(Exception e)
        {
           System.out.println("Send Position Failed: " + e.getMessage());
        }
    }
    
    private void getDevicePosition(com.motorola.iden.position.PositionConnection pc,com.motorola.iden.position.AggregatePosition oap)
    {
        if(oap.getResponseCode()==com.motorola.iden.position.PositionDevice.POSITION_OK)
        {
            if(oap.hasLatLon())
            {
                int lat = oap.getLatitude();
                int longt = oap.getLongitude();
                int spd = oap.getSpeed();
                int dir = oap.getTravelDirection();
                String deviceAddress = null;
                String latdegrees = oap.getLatitude(com.motorola.iden.position.Position2D.DEGREES);
                String longdegrees = oap.getLatitude(com.motorola.iden.position.Position2D.DEGREES);
                //send to backend web server
                sendPosition(deviceAddress,lat,longt,spd,dir);
            }
        }
        else
        {
            //determine error and tell user
            aggregateErr(oap);
        }
    }
    
    void aggregateErr(com.motorola.iden.position.AggregatePosition oap)
    {
        //oap.get
    }
    
    /** Creates a new instance of LocateTruck */
    public LocateTruck(Display _display) 
    {
        display = _display;
        javax.microedition.lcdui.Alert timeAlert = new javax.microedition.lcdui.Alert("Initializing");
        com.motorola.iden.position.PositionConnection pc = null;
        //url = _url;
        String name = "mposition:delay=low";
        try
        {
            pc = (com.motorola.iden.position.PositionConnection)Connector.open(name);
            com.motorola.iden.position.AggregatePosition oap = pc.getPosition();
            if(pc.getStatus()==com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_OK)
            {
                getDevicePosition(pc,oap);
                //sendPosition(deviceAddress,lat,longt,spd,dir);
                pc.close();
            }
            else if(pc.getStatus()!=com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_OK)
            {
                //display reason for not OK response
                errorReason(pc.getStatus());
                //tell user that device location api is probably restricted
                display.setCurrent( new Alert("Error", "Location API may be restricted on this device",
                                                                                   null, AlertType.ERROR) );
            }
        }
        catch(Exception e)
        {
            //System.out.print("Locate Truck: " + e.getMessage());
            display.setCurrent( new Alert("Error", e.getMessage(),null, AlertType.ERROR) );
        }
    }
    
    void errorReason(int err)
    {
        switch(err)
        {
            case com.motorola.iden.position.PositionConnection.POSITION_NO_RESPONSE: 
                 display.setCurrent( new Alert("Error", "Device not responding.  Position not available",
                                                                                    null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_ERROR: 
                 display.setCurrent( new Alert("Error", "Error occured while retrieving position information",
                                                                                    null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_RESTRICTED: 
                 display.setCurrent( new Alert("Error", "User restricted to provide position information",
                                                                                    null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_WAITING_RESPONSE: 
                 display.setCurrent( new Alert("Error", "API is waiting for a response from the position device",
                                                                                    null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_NO_ALMANAC_OVERRIDE: 
                 display.setCurrent( new Alert("Error", "Position ALMANAC is outdated and user is restricted to over ride",
                                                                                    null, AlertType.ERROR) );
                 break;
        }
    }
    
}
