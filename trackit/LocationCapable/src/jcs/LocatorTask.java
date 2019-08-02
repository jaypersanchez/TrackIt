/*
 * LocatorTask.java
 *
 * Created on January 30, 2007, 7:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jcs;

import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author linuxwannabe
 */
public class LocatorTask  extends TimerTask
{
    Display display = null;
    String url = null;
    ConfigData config = null;
    CommandsListener cmdlst = null;
    public void run()
    {
        //determine first if within timeframe
        jcs.ManageRMS mrms = new ManageRMS();
        String begin = mrms.reportingStartTime;
        String end = mrms.reportingEndTime;
        /**if (latDiff > 0.0005 || latDiff < -0.0005 || lonDiff > 0.0005 || lonDiff < -0.0005)
        double latDiff = Double.parseDouble(currentLat) - Double.parseDouble(prevLat);
        double lonDiff = Double.parseDouble(currentLon) - Double.parseDouble(prevLon);**/
        new GenericLocationAPI(display,config).start();
        //display.setCurrent( new Alert("Location", gAPI.getLat()+":"+gAPI.getLong(),null, AlertType.INFO));
        //new LocateTruck(display);
    }
    
    /** Creates a new instance of LocatorTask */
    public LocatorTask(Display _d,ConfigData _config) 
    {
        try
        {
            display = _d;
            config = _config;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
