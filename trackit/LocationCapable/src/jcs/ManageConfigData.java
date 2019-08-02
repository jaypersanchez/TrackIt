/*
 * ManageConfigData.java
 *
 * Created on July 16, 2007, 3:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.*;
import java.io.*;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author linuxwannabe
 */
public class ManageConfigData implements CommandListener
{
    //class variables
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
    private Command exitCommand = new Command("Exit", Command.EXIT, 2);
    private Command saveCommand = new Command("Save", Command.OK, 1);
    Capable capable;
    Display display;
    
    /** Creates a new instance of ManageConfigData */
    public ManageConfigData(ConfigData _config,Capable _capable) 
    {
        try
        {
        capable = _capable;
        display = Display.getDisplay(capable);
        Form form = new Form("Manage TrackIt Configuration");
        form.append(_config.getPhoneID());
        form.append(_config.getIP());
        form.append(new Integer(_config.getPort()).toString());
        form.append(_config.getCarrierIP());
        form.append(new Integer(_config.getGPSInterval()).toString());
        form.append(_config.getEndTime());
        form.addCommand(saveCommand);
        form.addCommand(exitCommand);
        display.setCurrent(form);
        }
        catch(Exception e)
        {
            display.setCurrent( new Alert("Manage Config Data Error", e.getMessage(),null, AlertType.ERROR));
        }
    }
    void save()
    {
       try
       {
           
       }
       catch(Exception e)
       {
           
       }
    }
    
    public void commandAction(Command c, Displayable d) 
    {
      String label = c.getLabel();
      if(label.equals("Save")) save();
      if(label.equals("Exit")) 
      {
        capable.destroyApp(true);
      }
   }
}
