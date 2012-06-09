package uk.ecs.gdp.avsummariser.model.audio;

import java.util.Comparator;

/**
 * Class to sort FrequencyObject objects in order of frequency value in ascending order.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class FrequencyObjectComparator implements Comparator<FrequencyObject>{

	/**
	 * Method to compare two FrequencyObject objects
	 * 
	 * @param first (FrequencyObject object)
	 * @param second (FrequecyObject object)
	 * @return int value
	 */
	public int compare(FrequencyObject first, FrequencyObject second) {
		return (first.getFrequency() > second.getFrequency() ? 1 : (first.getFrequency() == second.getFrequency() ? 0 : -1));
	}

}
