/*
 * ServerEnd.java
 *
 * Created on July 12, 2007, 9:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.Datagram;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
/**
 *
 * @author linuxwannabe
 */
public class ServerEnd extends Thread
{
    Display disp;
    ConfigData config;
    double lat = 0;
    double longi = 0;
    float speed = 0;
    float course = 0;
    DatagramConnection dc;
    String gpsData;
    
    public void run()
    {
        try
        {
            //connect to backend
            while(dc==null)
            {
                dc = (DatagramConnection) Connector.open("datagram://" + "209.226.80.182"+ ":" + 
                                                     4000, Connector.WRITE, false);
            }
            //data format lat,long,speed,dir"T"timestamp
            if(speed>0&course>0)
            {
                gpsData = config.phoneID+","+"209.226.80.182"+","+lat+","+longi+","+speed+","+course+"T"+
                             new java.util.Date(System.currentTimeMillis());
            }
            else
            {
                gpsData = config.phoneID+","+"209.226.80.182"+","+lat+","+longi+","+0+","+0+"T"+
                             new java.util.Date(System.currentTimeMillis());
            }
            
            byte[] buffer = gpsData.getBytes();
            Datagram dg = dc.newDatagram(buffer,buffer.length);
            dc.send(dg);
            sleep(1000);
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            disp.setCurrent( new Alert("Serven End", "Send err:"+e.getMessage(),null, AlertType.INFO));
        }
    }
    
    /** Creates a new instance of ServerEnd */
    public ServerEnd(Display _disp,ConfigData _config,double _lat,double _long,float sp,float dir) 
    {
        disp = _disp;
        config = _config;
        lat = _lat;
        longi = _long;
        speed = sp;
        course = dir;       
        this.start();
    }
    
}
