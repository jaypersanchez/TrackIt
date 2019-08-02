package main;

import javax.microedition.io.*;
import com.motorola.iden.position.*;

public class GPSUtil {
	
	public static final int DELAY_HIGH = 1;
    public static final int DELAY_LOW = 2;
    public static final int DELAY_NO = 3;
    
    public GPSUtil() {
    }
    
    /**
     * Returns the GPS reading
     * @param delay
     * @return
     */
	public String getGPS(int delay) {
		String result = "";
		PositionConnection pc = null;
		try {
			if (delay == DELAY_HIGH)
				pc = (PositionConnection) Connector.open("mposition:delay=high");
			else if (delay == DELAY_LOW)
				pc = (PositionConnection) Connector.open("mposition:delay=low");
			else // delay == DELAY_NO
				pc = (PositionConnection) Connector.open("mposition:delay=no");				
		} catch (Exception ex) {
			return ex.getMessage();
		}
		AggregatePosition ap = null;
		if (delay == DELAY_HIGH)
			ap = pc.getPosition("delay=high;fix=extended");
		else if (delay == DELAY_LOW)
			ap = pc.getPosition("delay=low;fix=extended");
		else // delay == DELAY_NO
			ap = pc.getPosition("delay=low;fix=extended"); // not sure how to do a 'no delay' gps fix
		
		if(pc.getStatus() ==
			com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_RESTRICTED)
		{
			//means user has restricted permission to get position
			return "GPS,POSITION_RESPONSE_RESTRICTED";
		}
		else if(pc.getStatus() == 
			com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_NO_ALMANAC_OVERRIDE)
		{
			//means device has Almanac out of date and User has not granted to override
			return "GPS,POSITION_RESPONSE_NO_ALMANAC_OVERRIDE";
		}
		else if(pc.getStatus() ==
			com.motorola.iden.position.PositionConnection. POSITION_NO_RESPONSE)
		{
			//means no response from device
			return "GPS,POSITION_NO_RESPONSE";
		}
		
		if (ap != null ) {
			if(pc.getStatus() ==
				com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_OK)
			{
				//Good position
				//Check for any error from device on position
				//Application needs to check for null position
			
				if(ap.getResponseCode() == PositionDevice.POSITION_OK) {
					//no error in the position
					if(ap.hasLatLon()) {
						//String representation of the Latitude and Longitude.
						result = "GPS,";
						result += ap.getLatitude(Position2D.DEGREES) + ",";
						result += ap.getLongitude(Position2D.DEGREES) + ",";						
						if(ap.hasSpeedUncertainty()) {
							//speed and heading value are valid
							result += ap.getSpeed() + ",";
							if (ap.hasTravelDirection()) {
								//heading is available
								result += ap.getTravelDirection();
							}
						}
						result += "T" + ap.getTimeStamp();
					}
					return result;
				}
				//handle the errors…or request again for good position
				//or display message to the user.
				else if(ap.getResponseCode() == PositionDevice.ACCURACY_NOT_ATTAINABLE) {
					//the position information was provided but enough accuracy
					//may not be attainable
					return "GPS,ACCURACY_NOT_ATTAINABLE";
				}
				else if(ap.getResponseCode() == 
					PositionDevice.ACC_NOT_ATTAIN_ASSIST_DATA_UNAV)
				{
					//the position information was provided but enough accuracy
					//assistant data unavailable
					return "GPS,ACC_NOT_ATTAIN_ASSIST_DATA_UNAV";
				}
			} //end of position response ok
			
			else if(pc.getStatus() ==
				com.motorola.iden.position.PositionConnection.POSITION_RESPONSE_ERROR)
			{
				//indicate an error occurred while getting the position
				if(ap.getResponseCode() == PositionDevice.FIX_NOT_ATTAINABLE) {
					//means position information not provided (timeout)
					return "GPS,FIX_NOT_ATTAINABLE";
				}
				else if(ap.getResponseCode() ==
					PositionDevice.FIX_NOT_ATTAIN_ASSIST_DATA_UNAV) 
				{
					//means position information not provided (timeout) and
					//assistant data unavailable
					return "GPS,FIX_NOT_ATTAIN_ASSIST_DATA_UNAV";
				}
				else if(ap.getResponseCode() == PositionDevice.BATTERY_TOO_LOW) {
					//means battery is too low to provide fix
					return "GPS,BATTERY_TOO_LOW";
				}
				else if(ap.getResponseCode() == PositionDevice.GPS_CHIPSET_MALFUNCTION) {
					//means GPS chipset malfunction
					return "GPS,GPS_CHIPSET_MALFUNCTION";
				}
				else if(ap.getResponseCode() == PositionDevice.ALMANAC_OUT_OF_DATE) {
					//means almanac out of date to get fix
					//This scenario occurs when user overrides almanac but device is
					//not packet data provisioned
					return "GPS,ALMANAC_OUT_OF_DATE";
				}
				else {
					//Unknown error occurs
					return "GPS,UNKNOWN_ERROR";
				}
			} //end of position response error
			//position is null
		} 
		if ( pc != null) {
			try {
				pc.close();
			} catch (Exception ex) {
				// do nothing
			}
		}
		return "";
	} 
}