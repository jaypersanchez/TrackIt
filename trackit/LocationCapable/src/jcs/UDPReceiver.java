/*
 * UDPReceiver.java
 *
 * Created on July 15, 2007, 9:33 PM
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
public class UDPReceiver extends Thread
{
    Display display;
    Datagram datagram;
    ConfigData config;
    
    /** Creates a new instance of UDPReceiver */
    public UDPReceiver(Display _d,ConfigData _c) 
    {
        display = _d;
        config =  _c;
        display.setCurrent( new Alert("UDPReceiver","UDPReceiver Started",null, AlertType.INFO));
    }
 
    public void run()
    {
        try
        {
           DatagramConnection dgc = (DatagramConnection) 
           Connector.open("datagram://:4000");
           //try 
           //{
                int size = 100;
                datagram = dgc.newDatagram(size);
                dgc.receive(datagram);
                //System.out.println(new String(datagram.getData()).trim());
                display.setCurrent( new Alert("UDPReceiver Recieve Handler", 
                                    new String(datagram.getData()).trim(),null, AlertType.INFO));
           //}
           //finally
           //{
           //     dgc.close();
           //}
 
        }
        catch(Exception e)
        {
           //e.printStackTrace(); 
            display.setCurrent( new Alert("UDPReceiver Error", e.getMessage(),null, AlertType.ERROR));
        }
    }
}
