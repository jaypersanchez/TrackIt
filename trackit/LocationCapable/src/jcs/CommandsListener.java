package jcs;

import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
import javax.wireless.messaging.*;

public class CommandsListener extends Thread 
{
	
	private Capable parent;
	private DatagramConnection dc;
	private Thread thd;
	private Display display;
	public boolean enabled;
	private RecordStore recordStore;
        ConfigData config;
        int listeningport = 4000;
	
	public CommandsListener(Capable m,ConfigData _config) 
        {
            parent = m;
            config = _config;
            display = Display.getDisplay(m);
            enabled = true;
            //display.setCurrent( new Alert("CommandListener", "Instantiated",null, AlertType.INFO) ); 
        }
	
	public void start() 
        {
            enabled = true;
            thd = new Thread(this);
            thd.start();
        }
	
	public void openConnection() 
        {
            try 
            {
                dc = (DatagramConnection) Connector.open("datagram://:"+listeningport,
                                                         Connector.READ,false);
            } 
            catch (Exception ex) 
            {
                //display.setCurrent( new Alert("CommandListener", ex.getMessage(),null, AlertType.ERROR) );
    		try 
                {
                    sleep(1000);
		}
                catch (Exception e) 
                {
                    e.printStackTrace();
                    //display.setCurrent( new Alert("CommandListener", e.getMessage(),null, AlertType.ERROR) );
                }
            }
        }
    
    public void closeConnection() 
    {
    	if (dc != null)
        {
            try 
            {
                dc.close();
            } 
            catch (Exception ex) 
            {
    		/*Alert a = new Alert("CommandsListener", "Exception Caught during closeConnection",
                     			null, AlertType.ERROR);
    		a.setTimeout(5000);
    		display.setCurrent(a);*/
            }
    	}
    }
	
	public void run() 
        {
            openConnection();
            while (enabled)
            {
        	while (dc == null) 
                {
                    /**
                     * keep trying to obtain connection until a connection is received 
                     * (thus, when dc != null)
                     */
        	    openConnection();
        	}
        	// Message msg = null;
        	String rcvString = "";
        	try 
                {
                    Datagram dg = dc.newDatagram(100);
                    dc.receive(dg);
                    rcvString = new String(dg.getData(), 0, dg.getLength());
                    //dc.close();
                    //this.notifyAll();
                    //get ConfigData to handle command
                    if( !(rcvString.equals(null))||!(rcvString.equals("")) )
                    {
                        String cmd = new Character(rcvString.charAt(0)).toString();
                        if( rcvString.indexOf("sCFG")!=-1 )
                        {
                            this.setConfig(rcvString);
                        }
                        if( rcvString.indexOf("GPS")!=-1 )
                        {
                            this.getGPS(rcvString);
                        }
                        if( rcvString.indexOf("gCFG")!=-1 )
                        {
                            this.getCFG(rcvString);
                        }
                    }
                    /**Alert a = new Alert("cmdHandler",rcvString,null,AlertType.INFO);
                    a.setTimeout(10000);
                    this.display.setCurrent(a);**/
                    //clean up
                    dc.close();
                    this.notifyAll();
               }
        	catch (Exception e) 
                {
                    e.printStackTrace();
                    //display.setCurrent( new Alert("CommandListener", e.getMessage(),null, AlertType.ERROR) );
                }
            }
        }//while
        //closeConnection();
    //gCFG
    public void getCFG(String rcvString)
    {
        /**String message = config.getPhoneID()+","+config.getCarrierIP()+","+"1;gCFG,"+
                         config.getIP()+","+config.getPort()+","+config.GPSEnabled+","+
                         config.GPSInterval+","+config.getCarrierIP()+","+
                         config.carrierIPReportingEnabled+","+config.reportingStartTime+","+
                         config.reportingEndTime+","+config.firmwareVersion;**/
        ManageRMS mrms = new ManageRMS();
        mrms.getCfg();
        String message = config.getPhoneID()+","+mrms.carrierIP+","+"1;gCFG,"+
                         mrms.ipAddress+","+mrms.port+","+mrms.GPSEnabled+","+
                         mrms.GPSInterval+","+mrms.carrierIP+","+
                         mrms.carrierIPReportingEnabled+","+mrms.reportingStartTime+","+
                         mrms.reportingEndTime+","+mrms.firmwareVersion;
        //call new thread
        CommandResponse res = new CommandResponse(this.config,message);
        res.start();
        res.notifyAll();
        mrms = null;
    }
    //GPS
    public void getGPS(String rcvString)
    {
        //call separate thread to get current fix location
        GenericLocationAPI currentLoc = new GenericLocationAPI(this.display,config);
        String loc = currentLoc.getLat()+","+currentLoc.getLong()+","+
                     currentLoc.getSpeed()+","+currentLoc.getCourse();
        currentLoc.start();
        currentLoc.notifyAll();
        ManageRMS mrms = new ManageRMS();
        mrms.getCfg();
       //call new thread
       /**CommandResponse res = new CommandResponse(this.config,
                                                  config.getPhoneID()
                                                  +","+config.getCarrierIP()
                                                  +",1;"+loc);**/
        CommandResponse res = new CommandResponse(config,
                                                  config.phoneID
                                                  +","+mrms.carrierIP
                                                  +",1;"+loc);
        res.start();
        res.notifyAll(); 
        mrms = null;
    }
    //sCFG
    public void setConfig(String _rcvString)
    {
         ManageRMS mrms = new ManageRMS();
         mrms.setCfg(_rcvString);
        //call new thread
        /**CommandResponse res = new CommandResponse(this.config,
                                                 config.getPhoneID()+
                                                 config.getCarrierIP()+",1"+";sCFG");**/
        CommandResponse res = new CommandResponse(this.config,
                                                 config.getPhoneID()+","+
                                                 mrms.carrierIP+",1"+";sCFG"); 
        res.start();
        res.notifyAll();
        mrms = null;
    }//setConfig
        
    void respond(Datagram _dg)
    {
        try
        {
            String sourceaddy = _dg.getAddress();
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
        
    public void stop()
    {
    	enabled = false;
    }
}
