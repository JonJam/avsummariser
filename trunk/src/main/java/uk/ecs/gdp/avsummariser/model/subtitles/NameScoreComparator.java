package uk.ecs.gdp.avsummariser.model.subtitles;

import java.util.Comparator;

/**
 * Comparator to sort NameScore objects using score values in descending order.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class NameScoreComparator implements Comparator<NameScore>{
		 
	/**
	 * Method to compare two NameScore objects
	 * 
	 * @param ns1 (NameScore object)
	 * @param ns2 (NameScore object)
	 */
	public int compare(NameScore ns1, NameScore ns2) {
		return (ns1.getCount() > ns2.getCount() ? -1 : (ns1.getCount() == ns2.getCount() ? 0 : 1));
	}
}
