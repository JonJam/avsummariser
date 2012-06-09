package uk.ecs.gdp.avsummariser.model.subtitles;

/**
 * Subtitle class represents a period of time in video and the lines spoken during that period.
 * 
 * @author Jonathan Harrison
 * @version 4
 */
public class Subtitle {

	private String beginTime;
	private String endTime;
	private String colour;
	private String speech;
	
	public Subtitle(String beginTime, String endTime, String colour, String speech){
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.colour = colour;
		this.speech = speech;
	}
	
	/**
	 * Get subtitle's begin time String object.
	 * 
	 * @return beginTime (String Object)
	 */
	public String getBeginTime(){		
		return beginTime;
	}
	
	/**
	 * Get subtitle's end time String object.
	 * 
	 * @return endTime (String Object)
	 */
	public String getEndTime(){
		return endTime;
	}
	
	/**
	 * Get subtitle's colour.
	 * 
	 * @return colour (String Object)
	 */
	public String getColour(){
		return colour;
	}
	
	/**
	 * Get subtitle's speech.
	 * 
	 * @return speech (String Object)
	 */
	public String getSpeech(){
		return speech;
	}
}
