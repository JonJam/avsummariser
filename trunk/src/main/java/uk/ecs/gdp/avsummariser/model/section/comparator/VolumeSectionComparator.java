package uk.ecs.gdp.avsummariser.model.section.comparator;

import java.util.Comparator;

import uk.ecs.gdp.avsummariser.model.section.VolumeSection;

/**
 * Class to sort LoudSection objects in descending order of 
 *  - EffectiveSoundPressure
 * 
 * @author Jenny Lantair 
 * @author Jonathan Harrison
 * @version 1
 */
public class VolumeSectionComparator implements Comparator<VolumeSection>{

	/**
	 * Method to compare two VolumeSection objects
	 * 
	 * @param first (VolumeSection object)
	 * @param second (VolumeSection object)
	 * @return result of comparison (int value)
	 */
	public int compare(VolumeSection first, VolumeSection second) {
		return (first.getEffectiveSoundPressure() > second.getEffectiveSoundPressure() ? -1 : (first.getEffectiveSoundPressure() == second.getEffectiveSoundPressure() ? 0 : 1));
	}
}
