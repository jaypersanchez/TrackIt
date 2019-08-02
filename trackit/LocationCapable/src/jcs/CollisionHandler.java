/*
 * CollisionHandler.java
 *
 * Created on July 11, 2007, 5:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jcs;
import javax.microedition.location.Location;
import javax.microedition.location.Coordinates;
import javax.microedition.location.ProximityListener;

/**
 *
 * @author linuxwannabe
 */
public class CollisionHandler 
{
    
    /** Creates a new instance of CollisionHandler */
    public CollisionHandler() 
    {
    }
    public void proximityEvent(Coordinates coordinates,Location location)
    {
        //handle collision here.
    }
    public void monitoringStateChange(boolean isActive)
    {
        //handle state change in provider
    }
}
