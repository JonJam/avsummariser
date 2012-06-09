package uk.ecs.gdp.avsummariser.model.section.comparator;

import java.util.Comparator;

import uk.ecs.gdp.avsummariser.model.section.SummarySection;

/**
 * Class to sort SummarySection objects in descending order of:
 *  - TotalScore then
 *  - EffectiveSoundPressureScore
 * Used when default trailer type chosen.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class TotalScoreComparator implements Comparator<SummarySection>{
	
	/**
	 * Method to compare two SummarySection objects
	 * 
	 * @param s1 (SummarySection object)
	 * @param s2 (SummarySection object)
	 * @return result of comparison (int value)
	 */
	public int compare(SummarySection s1, SummarySection s2){
		
		double s1Ts = s1.getTotalScore();
		double s2Ts = s2.getTotalScore();
		
		if(s1Ts > s2Ts){
			return -1;
			
		} else if(s1Ts < s2Ts){
			
			return 1;
		} else{
			double s1Ep = s1.getEffectiveSoundPressureScore();
			double s2Ep = s2.getEffectiveSoundPressureScore();
			
			if(s1Ep > s2Ep){
				return -1;
			} else if(s1Ep < s2Ep){
				return 1;
			} else{
				return 0;
			}
		}
	}
}
