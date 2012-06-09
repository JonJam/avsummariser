package uk.ecs.gdp.avsummariser.model.time;

/**
 * A class which contains all the Time methods that are used throughout the system.
 * 
 * @author Jonathan Harrison
 * @author Michael Harris
 * @version 1
 */
public class TimeUtils {
	
	/**
	 * Method to convert a time in milliseconds to a String of the form: HH:MM:SS.
	 * 
	 * @param timeInMilliSeconds (long value)
	 * @return String Object
	 */
	public static String getTimeInString(long timeInMilliSeconds){ 
		long time = timeInMilliSeconds / 1000; 
		
		String seconds = Integer.toString((int)(time % 60));  
		String minutes = Integer.toString((int)((time % 3600) / 60));  
		String hours = Integer.toString((int)(time / 3600));  
		
		for (int i = 0; i < 2; i++) {  
			if (seconds.length() < 2) {  
				seconds = "0" + seconds;  
			}  
			if (minutes.length() < 2) {  
				minutes = "0" + minutes;  
			}  
			if (hours.length() < 2) {  
				hours = "0" + hours;  
			}  
		}  
		
		return hours + ":" + minutes + ":" + seconds;
	}
	
	/**
	 * Method to convert a String of the form: HH:MM:SS.MSMS into time in milliseconds
	 * 
	 * @param time (String object)
	 * @return long value
	 */
	public static long getTimeInMilliSeconds(String time){
		String[] timeParts = time.split(":");
		int hours = Integer.parseInt(timeParts[0]);
		int minutes = Integer.parseInt(timeParts[1]);

		String[] secondParts = (timeParts[2]).split("\\.");
		int seconds = Integer.parseInt(secondParts[0]);
		int milliseconds = Integer.parseInt(secondParts[1]);
		
		long hoursInMillis = hours * 60 * 60 * 1000;
		long minutesInMillis = minutes * 60 * 1000;
		long secondsInMillis = seconds * 1000;
		
		return hoursInMillis + minutesInMillis + secondsInMillis + milliseconds;
	}
	
	/**
	 * Method to convert a String of form MM:SS to time in  milliseconds
	 * 
	 * @param time (String object)
	 * @return long value
	 */
	public static long convertMMSStoTimeInMilliseconds(String time){
		String[] timeParts = time.split(":");
		
		int minutes = Integer.parseInt(timeParts[0]);
		int seconds = Integer.parseInt(timeParts[1]);
		
		long minutesInMillis = minutes * 60 * 1000;
		long secondsInMillis = seconds * 1000;
		
		return minutesInMillis + secondsInMillis;
	}
	
	/**
	 * Method to convert a time in milliseconds to a time in the form MM.xx
	 * 
	 * @param timeInMillis (long value)
	 * @return double value
	 */
	public static double formatMillisIntoSeconds(long timeInMillis) {	
		return (timeInMillis/1000);
	}
	
	/**
	 * Method to convert a time in milliseconds to a time into minutes
	 * 
	 * @param timeInMillis (long value)
	 * @return double value
	 */
	public static double formatMillisIntoMin(long timeInMillis) {

		return (timeInMillis/1000)/60;
	}
}
