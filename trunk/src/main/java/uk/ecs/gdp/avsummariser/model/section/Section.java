package uk.ecs.gdp.avsummariser.model.section;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.time.TimeUtils;

/**
 * Class to represent a section of video.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class Section {
	
	//Video file for which this occurs in.
	protected VideoFile video;
	//Start time in milliseconds of loud audio section.
	protected long startTime;
	//End time in milliseconds of loud audio section.
	protected long endTime;
	
	//Used by subclasses
	protected Section(VideoFile video){
		this.video = video;
	}
	
	public Section(VideoFile video, long startTime, long endTime){
		this.video = video;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	//Used in Person / Location detection in Subtitles
	public Section(VideoFile video, String startTime, String endTime){
		this.video = video;
		this.startTime = TimeUtils.getTimeInMilliSeconds(startTime);
		this.endTime = TimeUtils.getTimeInMilliSeconds(endTime);
	}
		
	/**
	 * Method to get value of video
	 * 
	 * @return video (VideoFile object)
	 */
	public VideoFile getVideo(){
		return video;
	}

	
	/**
	 * Method to get value of startTime
	 * 
	 * @return startTime
	 */
	public long getStartTime(){
		return startTime;
	}
	
	/**
	 * Method to get start time as a String object.
	 * 
	 * @return String object
	 */
	public String getStartTimeString(){
		return TimeUtils.getTimeInString(startTime);
	}
	
	/**
	 * Method to get value of endTime
	 *
	 * @return endTime
	 */
	public long getEndTime(){
		return endTime;
	}
	
	/**
	 * Method to get end time as a String object.
	 * 
	 * @return String object
	 */
	public String getEndTimeString(){
		return TimeUtils.getTimeInString(endTime);
	}
	
	/**
	 * Method to set value of startTime
	 *
	 * @param time (long value)
	 */
	public void setStartTime(long time){
		startTime = time;
	}
	
	/**
	 * Method to set value of endTime
	 *
	 * @param time (long value)
	 */
	public void setEndTime(long time){
		endTime = time;
	}
}
