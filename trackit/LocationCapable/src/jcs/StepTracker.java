package jcs;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.Location;

public class StepTracker implements LocationListener
{
    double longi = 0;
    double lat = 0;
    float sp = 0;
    float course = 0;
    
    public float getCourse()
    {
        return course;
    }
    public float getSpeed()
    {
        return sp;
    }
    public double getLat()
    {
        return lat;
    }
    public double getLong()
    {
        return longi;
    }
    
    public void locationUpdated(LocationProvider provider,Location location)
    {
        //do something with new location
        javax.microedition.location.QualifiedCoordinates qc =
                                                        location.getQualifiedCoordinates();
        lat = qc.getLatitude();
        longi = qc.getLongitude();
        //sp = location.getSpeed();
        //course = location.getCourse();
        
    }
    public void providerStateChanged(LocationProvider provider,int newState)
    {
        //handle the state change. if provider becomes unavailable, alert user
        
    }
    
}
