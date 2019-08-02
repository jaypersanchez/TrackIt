/*
 * @(#)Sender.java	1.6 04/04/25
 *
 * Copyright (c) 2000-2004 Sun Microsystems, Inc. All rights reserved. 
 * PROPRIETARY/CONFIDENTIAL
 * Use is subject to license terms
 */
package jcs;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class Sender extends Thread {

    private DatagramConnection dc;
    private String address;
    private String message;
    private boolean autoClose;

    public Sender(DatagramConnection dc, boolean autoClose) {
        this.dc = dc;
        this.autoClose = autoClose;
        start();
    }

    public synchronized void send(String addr, String msg) {
        address = addr;
        message = msg;
        //notify(); //awakens
        this.notifyAll();
    }

    public synchronized void run() {

        while (true) {

            // If no client to deal, wait until one connects
            if (message == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

            try {
                byte[] bytes = message.getBytes();
                Datagram dg = null;
                // Are we a sender thread for the client ? If so then there's
                // no address parameter
                if (address == null) {
                    dg = dc.newDatagram(bytes, bytes.length);
                } else {
                    dg = dc.newDatagram(bytes, bytes.length, address);
                }
                dc.send(dg);
                dc.close();
                this.notifyAll();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            
            if (this.autoClose) {
            	try {
            		dc.close();
            	} catch (Exception ex) {
                    // do nothing
                    ex.printStackTrace();
            	}
            }

            // Completed client handling, return handler to pool and
            // mark for wait
            message = null;
        }
    }

}

