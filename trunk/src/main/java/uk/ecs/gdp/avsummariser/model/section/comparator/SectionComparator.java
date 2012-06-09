package uk.ecs.gdp.avsummariser.model.section.comparator;

import java.util.Comparator;

import uk.ecs.gdp.avsummariser.model.section.Section;

/**
 * Class to sort Section objects in ascending order of 
 * 	- Video file name then
 *  - Start and end times.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SectionComparator implements Comparator<Section>{

	/**
	 * Method to compare two Section objects
	 * 
	 * @param s1 (Section object)
	 * @param s2 (Section object)
	 * @return result of comparison (int value)
	 */
	public int compare(Section s1, Section s2) {
		
		int fileNameComparison = (s1.getVideo().getAbsolutePath()).compareTo(s2.getVideo().getAbsolutePath());
		
		if(fileNameComparison < 0){
			//s1 follows s2
			
			return -1;
		} else if(fileNameComparison > 0){
			//s2 follows s1
			
			return 1;
		} else{
			//Same file
			return (s1.getEndTime() > s2.getStartTime() ? 1 : (s1.getEndTime() == s2.getStartTime() ? 0 : -1));
		}
	}
}
