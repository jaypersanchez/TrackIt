/*
 * Capable.java
 *
 * Created on July 5, 2007, 4:13 PM
 */

package jcs;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Timer;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.Datagram;
import jcs.CommandsListener;
import java.util.Date;

/**
 *
 * @author  linuxwannabe
 * @version
 */
public class Capable extends MIDlet  implements CommandListener
{
    //class variables
    private Display display;
    private TextField latitude;
    private TextField longitude;
    private Form form;
    private Command close;
    int interval = 10000; //default to 10 seconds
    int startupTime = 10000; //default to 10 seconds;
    Timer timer = new Timer();
    LocatorTask locatorTask = null;
    public String phoneID;
    public String ipAddress;
    public int port;
    public boolean GPSEnabled;
    public int GPSInterval;
    public String carrierIP;
    public boolean carrierIPReportingEnabled;
    public String reportingStartTime, reportingEndTime;
    public String firmwareVersion;
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    ConfigData config;
    Datagram datagram;
    jcs.CommandsListener gpsCommandsListener = null;
    ManageRMS mrms = null;
    
    public Capable()
    {
      display = Display.getDisplay(this);
      latitude = new TextField("Lat:", "", 10, TextField.ANY);
      longitude = new TextField("Long:", "", 10, TextField.ANY);
      config = new ConfigData(display);
      //trouble shooting
      //new ManageConfigData(config,this);
      gpsCommandsListener = new jcs.CommandsListener(this,config);
      //gpsCommandsListener.start();
      mrms =  new ManageRMS();
    }
    
    public void commandAction(Command c, Displayable d) 
    {
      String label = c.getLabel();
      if(label.equals("Exit")) 
      {
        destroyApp(true);
      }
   }
   
   //give value in minutes and will return nano seconds
   int getNano(int _min)
   {
        //Integer I = new Integer(0);
        //int min = I.parseInt(_min);
        int nano = (_min*60)*1000;
        return nano;
   }
   
   public void startApp() 
   {
     try
     {
        gpsCommandsListener.start();
        //timer.schedule(new LocatorTask(this.display,config),startupTime,getNano("1")); //wait 1 minute
        timer.schedule(new LocatorTask(this.display,config),
                       startupTime,
                       getNano(mrms.GPSInterval) );
     }  
     catch(Exception e)
     {
        e.printStackTrace();
        //display.setCurrent( new Alert("Main Error", e.getMessage(),null, AlertType.ERROR) ); 
     }
   }
    
    public void pauseApp() 
    {
    }
    
    public void destroyApp(boolean unconditional) 
    {
        //notifyDestroyed();
        try {
                //get last know location as well.
                /**criteria = new Criteria();
                criteria.setPreferredResponseTime(20);
                criteria.setVerticalAccuracy(500);
                criteria.setHorizontalAccuracy(500);
                lp = LocationProvider.getInstance(criteria);
                loc = lp.getLocation(20); //10 seconds interval
                cor = loc.getQualifiedCoordinates();**/
        	//jcs.ManageRMS mrms =  new ManageRMS();
                /**DatagramConnection dc = 
                        (DatagramConnection) Connector.open("datagram://" + config.ipAddress + 
                                                            ":" + config.port,
                                                            Connector.WRITE,false);**/
                jcs.CommandResponse re = new CommandResponse(config,config.phoneID+";Shutdown");
                //Sender sender = new Sender(dc,true);
        	//sender.send(null, this.phoneID + ";Shutdown");
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
}
