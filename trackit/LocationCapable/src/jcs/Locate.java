/*
 * Locate.java
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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.*;
//import javax.microedition.lcdui.AlertType;
/**
 *
 * @author linuxwannabe
 */
public class Locate
{
    //class variables
    Display display = null;
    String url = null;
    String deviceAddress = null;
    String latdegrees = null;
    String longdegrees = null;
    int spd = 0;
    int dir = 0;
    
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
            //System.out.println(localInfo);
            //HttpConnection httpConn = (HttpConnection)Connector.open(localInfo);
            //InputStream in = httpConn.openInputStream();
            //in.close();
            //httpConn.close();
            System.out.println("Location Sent: " + new Date(System.currentTimeMillis()));
        }
        catch(Exception e)
        {
           System.out.println("Send Position Failed: " + e.getMessage());
        }
    }
    
    public String getLat()
    {
        return latdegrees;
    }
    public String getLong()
    {
        return longdegrees;
    }
    
    public int getSpd()
    {
        return spd;
    }
    public int getDir()
    {
        return dir;
    }
    
    private void getDevicePosition(com.motorola.iden.position.PositionConnection pc,com.motorola.iden.position.AggregatePosition oap)
    {
        if(oap.getResponseCode()==com.motorola.iden.position.PositionDevice.POSITION_OK)
        {
            if(oap.hasLatLon())
            {
                int lat = oap.getLatitude();
                int longt = oap.getLongitude();
                spd = oap.getSpeed();
                dir = oap.getTravelDirection();
                //String deviceAddress = null;
                latdegrees = oap.getLatitude(com.motorola.iden.position.Position2D.DEGREES);
                longdegrees = oap.getLatitude(com.motorola.iden.position.Position2D.DEGREES);
                System.out.println(latdegrees + ":" + longdegrees);
                //send to backend web server
                //sendPosition(deviceAddress,latdegrees,longdegrees,spd,dir);
                //sendPosition(deviceAddress,lat,longt,spd,dir);
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
        //display.setCurrent( new Alert("Error", "Device not responding.Agrregate Error",
          //                                                                          null, AlertType.ERROR) );
        System.out.print("Device not responding.Agrregate Error");
    }
    
    /** Creates a new instance of Locate */
    public Locate(Display d) 
    {
        display = d;
        javax.microedition.lcdui.Alert timeAlert = new javax.microedition.lcdui.Alert("Initializing");
        com.motorola.iden.position.PositionConnection pc = null;
        String name = "mposition:delay=low";
        try
        {
            pc = (com.motorola.iden.position.PositionConnection)Connector.open(name);
            com.motorola.iden.position.AggregatePosition oap = pc.getPosition();
            if(pc.getStatus()==com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_OK)
            {
                getDevicePosition(pc,oap);
                pc.close();
            }
            else if(pc.getStatus()!=com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_OK)
            {
                //display reason for not OK response
                errorReason(pc.getStatus());
                //tell user that device location api is probably restricted
                //display.setCurrent( new Alert("Error", "Location API may be restricted on this device",
                  //                                                                   null, AlertType.ERROR) );
            }
        }
        catch(Exception e)
        {
            System.out.print("Locate Truck: " + e.getMessage());
        }
    }
    
    void errorReason(int err)
    {
        switch(err)
        {
            case com.motorola.iden.position.PositionConnection.POSITION_NO_RESPONSE: 
            //     display.setCurrent( new Alert("Error", "Device not responding.  POSITION_NO_RESPONSE",
              //                                                                      null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_ERROR: 
                // display.setCurrent( new Alert("Error", "Error occured while retrieving position information",
                  //                                                                  null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_RESTRICTED: 
                 //display.setCurrent( new Alert("Error", "User restricted to provide position information",
                   //                                                                 null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_WAITING_RESPONSE: 
                 //display.setCurrent( new Alert("Error", "API is waiting for a response from the position device",
                   //                                                                 null, AlertType.ERROR) );
                 break;
            case com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_NO_ALMANAC_OVERRIDE: 
                 //display.setCurrent( new Alert("Error", "Position ALMANAC is outdated and user is restricted to over ride",
                   //                                                                 null, AlertType.ERROR) );
                 break;
        }
    }
    
}
