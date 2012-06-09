package uk.ecs.gdp.avsummariser.model.section.comparator;

import java.util.Comparator;

import uk.ecs.gdp.avsummariser.model.section.SummarySection;

/**
 * Class to sort SummarySection objects in descending order of:
 * 	- LocationMentionedScore then
 *  - TotalScore
 * Used when Location mentioned trailer type chosen.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class LocationMentionedComparator implements Comparator<SummarySection> {

	/**
	 * Method to compare two SummarySection objects
	 * 
	 * @param s1 (SummarySection object)
	 * @param s2 (SummarySection object)
	 * @return result of comparison (int value)
	 */
	public int compare(SummarySection s1, SummarySection s2){
		
		double s1Lm = s1.getLocationMentionedScore();
		double s2Lm = s2.getLocationMentionedScore();
		
		if(s1Lm > s2Lm){
			return -1;
			
		} else if(s1Lm < s2Lm){
			
			return 1;
		} else{
			
			double s1Ts = s1.getTotalScore();
			double s2Ts = s2.getTotalScore();
			
			if(s1Ts > s2Ts){
				return -1;
			} else if(s1Ts < s2Ts){
				return 1;
			} else{
				return 0;
			}
		}
	}
	
}