package uk.ecs.gdp.avsummariser.model.section;

import com.echonest.api.v4.Track;

import uk.ecs.gdp.avsummariser.model.VideoFile;

/**
 * MusicSection is a child of Section class to represent a section of Music within a video. It contains a Track object which provides
 * all available information about the piece of music identified.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class MusicSection extends Section{

	private Track track;
	
	public MusicSection(VideoFile video, long startTime, long endTime, Track track){
		super(video,startTime,endTime);
		this.track = track;
	}
	
	//Method to get Track object.
	public Track getTrack(){
		return track;
	}
}
