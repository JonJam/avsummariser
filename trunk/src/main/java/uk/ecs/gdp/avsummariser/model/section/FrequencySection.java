package uk.ecs.gdp.avsummariser.model.section;

import java.util.ArrayList;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.audio.FrequencyObject;

/**
 * FrequencySection is a subclass of Section which contains the FrequencyObjects for this section of the audio.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class FrequencySection extends Section{

	//ArrayList to store FrequencyObject in this section of the audio.
	private ArrayList<FrequencyObject> frequencyObjects;
	
	public FrequencySection(VideoFile video){
		super(video);
	}
	
	/**
	 * Method to set the frequencyObjects ArrayList
	 * 
	 * @param frequencyObjects (ArrayList of FrequencyObject)
	 */
	public void setFrequencyObjects(ArrayList<FrequencyObject> frequencyObjects){
		this.frequencyObjects = frequencyObjects;
	}
	
	/**
	 * Method to get the frequencyObjects ArrayList
	 * 
	 * @return ArrayList of FrequencyObject
	 */
	public ArrayList<FrequencyObject> getFrequencyObjects(){
		return frequencyObjects;
	}
}
