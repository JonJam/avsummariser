package uk.ecs.gdp.avsummariser.model.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.EffectiveSoundPressure;
import org.openimaj.video.xuggle.XuggleAudio;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.VolumeSection;
import uk.ecs.gdp.avsummariser.model.section.comparator.VolumeSectionComparator;

/**
 * Class to calculate volume of audio samples in the audio stream of a file.
 * 
 * @author Jenny Lantair
 * @author Jonathan Harrison
 * @version 1
 */
public class VolumeDetector extends AudioDetector{
	
	//Percentage of volumes which are loud sections
	private final static int LOUD_SECTION = 1;
	//Difference between loud sections that can be merged in milliseconds (1 second)
	private final static int MERGE_DIFFERENCE = 1000; 
	//Window size for EffectiveSoundPressure in millisecond (10 milliseconds)
	private final static int WINDOW_SIZE = 10;

	//Volume which above or equals mean a section is loud.
	private static double loudThreshold;
				
	/**
	 * Method to work out volumes for all audio samples in a video's audio stream.
	 * 
	 * @param video (VideoFile object)
	 */
	public static void findAllVolumesInVideo(VideoFile video){
		//Calculate volume for audio in video.
		ArrayList<VolumeSection> allVolumes = calculateVolumesInVideo(video);
		
		//Set variable in video;
		video.setAllVolumes(allVolumes);
	}
	
	/**
	 * Method to find loud sections in the audio stream of a video.
	 * 
	 * @param video (VideoFile object)
	 */
	public static void findLoudSectionsInVideo(VideoFile video){
		//Get allVolumes from Video.
		ArrayList<VolumeSection> allVolumes = video.getAllVolumes();
		
		//Set loud threshold value
		setLoudThreshold(allVolumes);
		
		//Find loud sections
		ArrayList<VolumeSection> loudVolumes = findAllLoudSectionsInVideo(allVolumes);
		
		//Merge loud sections
		ArrayList<VolumeSection> mergedLoudSections = mergeLoudSectionsInVideo(loudVolumes);
		
		//Set variable in video.
		video.setLoudSections(mergedLoudSections);
	}	
	
	/**
	 * Method to find volumes of samples in the audio stream of a video.
	 * 
	 * @param video (VideoFile object)
	 */
	private static ArrayList<VolumeSection> calculateVolumesInVideo(VideoFile video){
		ArrayList<VolumeSection> allVolumes = new ArrayList<VolumeSection>();		
		
		XuggleAudio xa = new XuggleAudio(new File(video.getAbsolutePath()));
		
		//stream, windowSize in milliseconds, overlapMillSeconds
		EffectiveSoundPressure esp = new EffectiveSoundPressure(xa, WINDOW_SIZE, 0);
		
		VolumeSection section = new VolumeSection(video);
		SampleChunk chunk = xa.nextSampleChunk();
		
		//While loop to work out effective sound pressure for samples
		while(true){
			try{
				long timeCode = chunk.getStartTimecode().getTimecodeInMilliseconds();
				esp.process(chunk);
				double effectiveSoundPressure = esp.getEffectiveSoundPressure();
				
				section.setStartTime(timeCode);
				section.setEffectiveSoundPressure(effectiveSoundPressure);
			
				chunk = xa.nextSampleChunk();
				
				if(chunk == null){
					//Next sample chunk is null so break
					
					section.setEndTime(timeCode + 1000);	
					allVolumes.add(section);
					break;
				}
				
				section.setEndTime(chunk.getStartTimecode().getTimecodeInMilliseconds());
				allVolumes.add(section);
				section = new VolumeSection(video);
			
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		return allVolumes; 
	}
	
	/**
	 * Method to find threshold value for a loud section and set loudThreshold variable.
	 * 
	 * @param allVolumes (ArrayList of VolumeSection Objects)
	 */
	private static void setLoudThreshold(ArrayList<VolumeSection> allVolumes){
		ArrayList<VolumeSection> copyOfAllVolumes = new ArrayList<VolumeSection>();
		
		//Copying allVolumes items.
		for(VolumeSection v : allVolumes){
			copyOfAllVolumes.add(v);
		}
		
		//Sort from highest to lowest.
		Collections.sort(copyOfAllVolumes, new VolumeSectionComparator());
		
		//Find index of where bottom LOUD_SECTION % lies.
		int whereThresholdOccurs = (int) ((copyOfAllVolumes.size() / 100) * LOUD_SECTION);
		
		//Get effective sound pressure and set loudThreshold
		loudThreshold = copyOfAllVolumes.get(whereThresholdOccurs).getEffectiveSoundPressure();
	}
	
	/**
	 * Method to find VolumeSection objects which are classed as loud using LOUD_SECTION.
	 * 
	 * @param allVolumes (ArrayList of VolumeSection Objects)
	 * @return ArrayList of VolumeSection objects
	 */
	private static ArrayList<VolumeSection> findAllLoudSectionsInVideo(ArrayList<VolumeSection> allVolumes){
		ArrayList<VolumeSection> loudSections = new ArrayList<VolumeSection>();		
			
		for(VolumeSection l : allVolumes){
			if(l.getEffectiveSoundPressure() >= loudThreshold){
				//Pressure is above or equal to value of loudThreshold so add to loudSections.
				
				loudSections.add(l);
			}
		}
		
		return loudSections;	
	}
	
	/**
	 * Method to merge loud consecutive VolumeSections if difference between end and start time of is MERGE_DIFFERENCE
	 * 
	 * @param allLoudSections (ArrayList of VolumeSection objects)
	 * @return ArrayList of VolumeSection objects
	 */
	private static ArrayList<VolumeSection> mergeLoudSectionsInVideo(ArrayList<VolumeSection> allLoudSections){
		ArrayList<VolumeSection> mergedLoudSections = new ArrayList<VolumeSection>();
		VolumeSection current = null;

		double totalLoudness = 0;
		int numberOfSamplesInSection = 0;

		for(VolumeSection l : allLoudSections){
			
			if(current == null){
				//Current section hasn't been set so set it to l.
				current = l;
				totalLoudness = l.getEffectiveSoundPressure();
				numberOfSamplesInSection = 1;
				
			} else{
				if(current.getEndTime() + MERGE_DIFFERENCE >= l.getStartTime()){
					//Loud sections should be merged
					
					current.setEndTime(l.getEndTime());
					totalLoudness += (long) l.getEffectiveSoundPressure();
	        		numberOfSamplesInSection++;
				
				} else{
					//Don't merge so add LoudSection object in current and set current to l.
					
					current.setEffectiveSoundPressure(totalLoudness / numberOfSamplesInSection);
					mergedLoudSections.add(current);
					
					current = l;
					totalLoudness = l.getEffectiveSoundPressure();
					numberOfSamplesInSection = 1;
				}
			}
			
			if(allLoudSections.indexOf(l) == allLoudSections.size() - 1){
				//Last loudSection Object in AllLoudSections so add.
				
				current.setEffectiveSoundPressure(totalLoudness / numberOfSamplesInSection);
				mergedLoudSections.add(current);
			}
		}
		
		return mergedLoudSections;
	}
}
