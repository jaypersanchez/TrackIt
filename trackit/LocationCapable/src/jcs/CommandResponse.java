/*
 * CommandResponse.java
 *
 * Created on July 18, 2007, 6:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;
import javax.wireless.messaging.*;

/**
 *
 * @author linuxwannabe
 */
public class CommandResponse extends Thread
{
    ConfigData config;
    String response = null;
    
    /** Creates a new instance of CommandResponse */
    public CommandResponse(ConfigData _c,String _res) 
    {
        config = _c;
        response = _res;
    }
    
    public void run()
    {
        StringBuffer data = null;
        String curchar = null;
        jcs.ManageRMS mrms =  new ManageRMS();
        try 
        {
            DatagramConnection dc2 = null;
            while(dc2==null)
            {
                /**dc2 = (DatagramConnection) Connector.open("datagram://" + config.getIP() +":" + 
                                                     config.getPort(), Connector.WRITE, false);**/
                dc2 = (DatagramConnection) Connector.open("datagram://" + mrms.ipAddress +":" + 
                                                     mrms.port, Connector.WRITE, false);
               //byte[] bytes = (config.getPhoneID()+";CFGSet").getBytes();
               byte[] bytes = response.getBytes();
               Datagram dg = null;
               dg = dc2.newDatagram(bytes, bytes.length);
               dc2.send(dg);
               dc2.close();
            }
            mrms = null;
            //this.notifyAll();
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
            /**Alert a = new Alert("cmdHandler Error",ex.getMessage(),null,AlertType.INFO);
            a.setTimeout(10000);
            this.display.setCurrent(a);**/
        }
    }
}
