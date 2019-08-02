/*
 * Util.java
 *
 * Created on July 19, 2007, 8:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;

import javax.microedition.rms.RecordStore;
import javax.microedition.io.*;
import java.io.*;

/**
 *
 * @author linuxwannabe
 */
public class Util {
    
    RecordStore rc;
    double lat = 0;
    double longi = 0;
    
    /** Creates a new instance of Util */
    public Util() {
        try {
            rc = RecordStore.openRecordStore("db",true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    synchronized public String getPrevLoc() {
        String prevloc = null;
        try {
            //get data that is stored
            byte[] prev = {};
            prev = rc.getRecord(1);
            ByteArrayInputStream bais = new
            ByteArrayInputStream(prev);
            DataInputStream dis = new
            DataInputStream(bais);
            String settings = "";
            settings = dis.readUTF();
            int idx = settings.indexOf("^");
            lat = new Double(0).parseDouble(settings.substring(0,idx));
            longi = new Double(0).parseDouble(settings.substring(idx+1,settings.length()));
            prevloc = lat+"^"+longi;
            bais.close();
            dis.close();
            rc.closeRecordStore();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return prevloc;
    }
    
    synchronized public void saveLoc(double lat,double longi) {
        try {
            String prevLoc = new Double(lat).toString()+"^"+new Double(longi).toString();
            
            if (rc.getNumRecords()==0) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(baos);
                outputStream.writeUTF(prevLoc);
                baos.close();
                outputStream.close();
                rc.closeRecordStore();
            }
            else {
                updatePrevLoc(prevLoc);
                rc.closeRecordStore();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        /**finally {
            try { rc.closeRecordStore(); } catch(Exception i) {}
        }**/
    }
    
    void updatePrevLoc(String loc) {
        try {
            //get data that is stored
            byte[] prev = {};
            prev = rc.getRecord(1);
            ByteArrayInputStream bais = new
            ByteArrayInputStream(prev);
            DataInputStream dis = new
            DataInputStream(bais);
            String settings = "";
            settings = dis.readUTF();
            int idx = settings.indexOf("^");
            lat = new Double(0).parseDouble(settings.substring(0,idx));
            longi = new Double(0).parseDouble(settings.substring(idx+1,settings.length()));
            //now change values
            lat = new Double(0).parseDouble(loc.substring(0,idx));
            longi = new Double(0).parseDouble(loc.substring(idx+1,loc.length()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);
            String prevLoc = new Double(lat).toString()+"^"+new Double(longi).toString();
            outputStream.writeUTF(prevLoc);
            byte[] b = baos.toByteArray();
            this.rc.setRecord(1,b,0,b.length);
            bais.close();
            baos.close();
            outputStream.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    synchronized public void setLat(double val) {
        
        try {
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    synchronized public void setLong(double val) {
        
    }
    synchronized public double getStoredLat() {
        return 0;
    }
    synchronized public double getStoredLong() {
        return 0;
    }
    
}
