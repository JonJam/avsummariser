package uk.ecs.gdp.avsummariser.model.section;

import uk.ecs.gdp.avsummariser.model.VideoFile;

/**
 * Class to represent shots which are chosen to possibly be included in a movie trailer.
 * The object contains scores for each different module of the system.
 * 
 * @author Jonathan Harrison
 */
public class SummarySection extends Section{

	private double totalScore;
	
	private double effectiveSoundPressureScore;
	private double peopleMentionedScore;
	private double locationMentionedScore;
	
	public SummarySection(VideoFile video, long startTime, long endTime){
		//N.B THE STARTTIME AND ENDTIME VALUES IN THIS OBJECT ARE FOR SHOTS UNLIKE OTHER SECTION OBJECTS
		
		super(video,startTime,endTime);
		
		totalScore = 0;
		
		effectiveSoundPressureScore = 0;
		peopleMentionedScore = 0;
		locationMentionedScore = 0;
	}
	
	//Total score
	/**
	 * Method to calculate totalScore.
	 * 
	 */
	public void calculateTotalScore(){
		totalScore = peopleMentionedScore + locationMentionedScore;
	}
	
	/**
	 * Method to get the totalScore.
	 * 
	 * @return totalScore (double value)
	 */
	public double getTotalScore(){
		return totalScore;
	}
	//
	
	//Effective loud pressure score
	/**
	 * Method to set effectiveSoundPressureScore.
	 * 
	 * @param effectiveSoundPressureScore (double value)
	 */
	public void setEffectiveSoundPressureScore(double effectiveSoundPressureScore){
		this.effectiveSoundPressureScore = effectiveSoundPressureScore;
	}
	
	/**
	 * Method to get the effectiveSoundPressureScore.
	 * 
	 * @return effectiveSoundPressureScore (double value)
	 */
	public double getEffectiveSoundPressureScore(){
		return effectiveSoundPressureScore;
	}
	//
	
	//People mentioned score
	/**
	 * Method to set peopleMentionedScore.
	 * 
	 * @param peopleMentionedScore (double value)
	 */
	public void setPeopleMentionedScore(double peopleMentionedScore){
		this.peopleMentionedScore = peopleMentionedScore;
	}
	
	/**
	 * Method to get the peopleMentionedScore.
	 * 
	 * @return peopleMentionedScore (double value)
	 */
	public double getPeopleMentionedScore(){
		return peopleMentionedScore;
	}
	//
	
	//Location mentioned score
	/**
	 * Method to set locationMentionedScore.
	 * 
	 * @param locationMentionedScore (double value)
	 */
	public void setLocationMentionedScore(double locationMentionedScore){
		this.locationMentionedScore = locationMentionedScore;
	}
	
	/**
	 * Method to get the locationMentionedScore.
	 * 
	 * @return locationMentionedScore (double value)
	 */
	public double getLocationMentionedScore(){
		return locationMentionedScore;
	}
	//
	
	/**
	 * Method to return a score based upon how a Section object represented by startTime and endTime is contained
	 * within this SummarySection
	 * 
	 * @param video (VideoFile object)
	 * @param startTime (long value)
	 * @param endTime (long value)
	 * @return double value representing the score.
	 */
	public double contains(VideoFile video, long startTime, long endTime){
		double contains = 0.0;
		
		if(this.video.getAbsolutePath().equals(video.getAbsolutePath())){
			//Videos are the same
			
			if(startTime >= this.startTime && endTime <= this.endTime){
				/*Fully contained within or equal to SummarySection.
				 * 
				 * 	1. TST -------- TET
				 * 		 ST ---- ET
				 * 
				 *  2. TST -------- TET
				 *     ST  -------- ET
				 * 
				 */
				contains = 1;
			
			} else if((startTime > this.startTime && startTime < this.endTime) && endTime > this.endTime){
				/*Start is contained with this SummarySection but not end
				 * 
				 * 	1. TST -------- TET
				 * 	          ST -------- ET
				 */
				contains = 0.5;
			
			} else if(startTime < this.startTime && (endTime > this.startTime && this.endTime < endTime)){
				/* End is contained with this SummarySection but not start
				 * 
				 * 	1.       TST -------- TET
				 * 	   ST -------- ET
				 * 
				 */
				contains = 0.5;
			
			}
			
		}
		return contains;
	}
}
