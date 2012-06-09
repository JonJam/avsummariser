package uk.ecs.gdp.avsummariser.model;

import java.awt.image.BufferedImage;

import org.openimaj.video.timecode.VideoTimecode;

/**
 * Class to represent a video shot in a video.
 * 
 * @author Michael Harris
 * @version 1
 */
public class VideoShot {
	
	private BufferedImage thumbnail;
	private VideoTimecode timecode;
	
	public VideoShot(VideoTimecode timecode, BufferedImage thumbnail){
		this.thumbnail = thumbnail;
		this.timecode = timecode;
	}

	/**
	 * Method to get thumbnail image.
	 * 
	 * @return BufferedImage object
	 */
	public BufferedImage getThumbnail() {
		return thumbnail;
	}

	/**
	 * Method to get timecode.
	 * 
	 * @return VideoTimeCode object
	 */
	public VideoTimecode getTimecode() {
		return timecode;
	}
}
