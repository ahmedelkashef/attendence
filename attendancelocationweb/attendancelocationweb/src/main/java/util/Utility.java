package util;

import java.util.TimeZone;

public class Utility {
	public final static int DAY_IN_MILISEC = 24*60*60*1000;
	public final static int HOUR_IN_MILISEC = 60*60*1000;

	public static String getDomainFromMail(String email){
		if(email == null)
			return null;
		
		return email.toLowerCase().substring(email.indexOf("@") + 1);
	}
	
	public static int getDateDay(long timeMili){
		
		return (int) Math.ceil(((double)timeMili)/DAY_IN_MILISEC);
	}
	
	
	public static TimeZone intToTimeZone(int offset) {
        String timeZoneStr = "GMT";

        if (offset >= 0) {
            timeZoneStr += "+" + offset;
        } else {
            timeZoneStr += offset;
        }
        return TimeZone.getTimeZone(timeZoneStr);
    }
}
