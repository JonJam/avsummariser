package uk.ecs.gdp.avsummariser.model.section;

import uk.ecs.gdp.avsummariser.model.VideoFile;

/**
 * Class to represent sections of video with its effective sound pressure value.
 * 
 * @author Jenny Lantair
 * @author Jonathan Harrison
 * @version 1
 */
public class VolumeSection extends Section{
	
	//EffectiveSoundPressure for samples in this VolumeSection
	private double effectiveSoundPressure;
	
	public VolumeSection(VideoFile video){
		super(video);
	}
	
	/**
	 * Method to set value of EffectiveSoundPressure
	 *
	 * @param time (double value)
	 */
	public void setEffectiveSoundPressure(double pressure){
		effectiveSoundPressure = pressure;
	}
	
	/**
	 * Method to set value of EffectiveSoundPressure
	 *
	 * @return time (double value)
	 */
	public double getEffectiveSoundPressure(){
		return effectiveSoundPressure;
	}
}
