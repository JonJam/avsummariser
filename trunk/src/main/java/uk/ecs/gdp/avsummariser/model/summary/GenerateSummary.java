package uk.ecs.gdp.avsummariser.model.summary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.VideoShot;
import uk.ecs.gdp.avsummariser.model.section.Section;
import uk.ecs.gdp.avsummariser.model.section.SummarySection;
import uk.ecs.gdp.avsummariser.model.section.VolumeSection;
import uk.ecs.gdp.avsummariser.model.section.comparator.LocationMentionedComparator;
import uk.ecs.gdp.avsummariser.model.section.comparator.LoudnessComparator;
import uk.ecs.gdp.avsummariser.model.section.comparator.PeopleMentionedComparator;
import uk.ecs.gdp.avsummariser.model.section.comparator.SectionComparator;
import uk.ecs.gdp.avsummariser.model.section.comparator.TotalScoreComparator;
import uk.ecs.gdp.avsummariser.model.time.TimeUtils;
import uk.ecs.gdp.avsummariser.model.videotools.VideoFileHelper;

/**
 * Class to produce a Summary object containing processed data for all files loaded into system and a movie trailer.
 * 
 * @author Jonathan Harrison
 * @author Michael Harris
 * @version 1
 */
public class GenerateSummary {

	//Number of modules which are run on each video
	private static final int NUMBER_OF_MODULES = 4; // 4
	//Difference between shots that can be merged in milliseconds.
	private static final long MERGE_DIFFERENCE = 1000;
	
	/**
	 * Method to produce Summary object and the items it contains.
	 * 
	 * @param model (AVSummaryModel object)
	 */
	public static void summarise(AVSummaryModel model){
		ArrayList<Boolean> prepared = new ArrayList<Boolean>();
		
		//Getting data from model
		ArrayList<VideoFile> videoFiles = model.getLoadedVideos();
		Series series = model.getChosenSeries();
		List<Actor> actors = model.getSeriesActors();
		
		String genre = model.getGenreSelected();
		String trailerType = model.getTrailerTypeSelected();
		long duration = TimeUtils.convertMMSStoTimeInMilliseconds(model.getTrailerDuration());
		
		
		//Call prepare on all videos.
		for (VideoFile videoFile : videoFiles) {
			int count = prepare(model,videoFile);
			
			if(count == NUMBER_OF_MODULES){
				//All modules completed
				prepared.add(true);
			}
		}
		
		//Loop while wait for all videos to finishing processing
		while(true){
			if(prepared.size() == videoFiles.size()){
				//All videos ready
				break;
			} else{
				//Not all videos ready
				prepared.clear();
				
				for (VideoFile videoFile : videoFiles) {
					int count = prepare(model,videoFile);
					
					if(count == NUMBER_OF_MODULES){
						//All modules completed
						prepared.add(true);
					}
				}
				try {
					//Sleep for a minute
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}		
		
		//Merge data from all videos into single Map or ArrayList.
		Map<String, ArrayList<Section>> mergedPeopleNamesMentioned = mergeMaps(model, "People");	
		Map<String, ArrayList<Section>> mergedLocationsMentioned = mergeMaps(model, "Location");
		ArrayList<VolumeSection> mergedLoudSections = mergeLoudSections(model);
		
		//Stores SummarySection objects which will make up trailer.
		ArrayList<SummarySection> trailerSegments = null;
		
		//If to produce appropriate SummarySection object ArrayList.
		if(trailerType.equals("Default")){
			trailerSegments = createDefaultTrailer(mergedPeopleNamesMentioned,mergedLocationsMentioned,mergedLoudSections);
		} else if (trailerType.equals("Loudness")){
			trailerSegments = createLoudnessTrailer(mergedPeopleNamesMentioned,mergedLocationsMentioned,mergedLoudSections);
		} else if(trailerType.equals("People mentioned")){
			
			Set<String> peopleKeySet = mergedPeopleNamesMentioned.keySet();
			
			if(peopleKeySet.size() != 0){
				trailerSegments = createPeopleMentionedTrailer(mergedPeopleNamesMentioned,mergedLocationsMentioned,mergedLoudSections);
				
			} else{
				//No people detected or no subtitles loaded so use DefaultTrailer
				JOptionPane.showMessageDialog(null, "No people names detected or no subtitle files loaded. Using Default trailer type instead.");
				
				trailerSegments = createDefaultTrailer(mergedPeopleNamesMentioned,mergedLocationsMentioned,mergedLoudSections);
			}
		} else if(trailerType.equals("Locations mentioned")){
			
			Set<String> locationKeySet = mergedLocationsMentioned.keySet();
			
			if(locationKeySet.size() != 0){
				trailerSegments = createLocationMentionedTrailer(mergedPeopleNamesMentioned,mergedLocationsMentioned,mergedLoudSections);
				
			} else{
				//No locations detected or no subtitles loaded so use DefaultTrailer
				JOptionPane.showMessageDialog(null, "No location names detected or no subtitle files loaded. Using Default trailer type instead.");
				
				trailerSegments = createDefaultTrailer(mergedPeopleNamesMentioned,mergedLocationsMentioned,mergedLoudSections);
			}
		}
		
		//Produce trailer file.
		File trailer = produceTrailerFile(trailerSegments, duration);
		
		//Create Summary object
		Summary summaryObject = new Summary(videoFiles, genre, trailerType, series, actors, mergedPeopleNamesMentioned, mergedLocationsMentioned, mergedLoudSections, trailer);
		model.setSummary(summaryObject);
	}
	
	
	/**
	 * Method to check for each video whether all things (person, location, shots, ....) have been run and completed.
	 * 
	 * @param model (AVSummaryModel object)
	 * @param video (VideoFile object)
	 */
	private static int prepare(AVSummaryModel model, VideoFile video){
		int count = 0;
		
		if(video.getSubtitleFilePath() != null){
			//Subtitle path is not null
			
			if (video.isPersonNameFinderFinished() == false) {
				//Person name finder not been finished
				
				if(video.isPersonNameFinderStarted() == false){
					//Person name finder not been started.
					model.runPeopleFinder(null, video);
				}
				
			} else{
				//Person name finder complete
				count++;
			}
			
			if (video.isLocationNameFinderFinished() == false) {
				//Location name finder not been finished
				
				if(video.isLocationNameFinderStarted() == false){
					//Person name finder not been started.
					model.runLocationFinder(null, video);
				}
				
			} else{
				//Location name finder complete
				count++;
			}
		} else{
			//No subtitles loaded so increase count by 2
			count += 2;
		}
		
		if(video.isVideoShotDetectionFinished() == false){
			//Shot detection not run.
			
			if(video.isVideoShotDetectionStarted() == false){
				//Shot detection hasn't been started.
				try{
					model.runShotDetection(video);
				} catch(FileNotPlayableException e1) {
					e1.printStackTrace();
				}
			}
		} else{
			//Shot detection complete
			count++;
		}
		
		if(video.isVolumeDetectorFinished() == false){
			//Volume detection not run.
			
			if(video.isVolumeDetectorStarted() == false){
				//Volume detection hasn't been started.
				model.runVolumeDetector(video);
			}
		} else{
			//Volume detection complete
			count++;
		}
		
//		if(video.isFrequencyDetectorFinished() == false){
//			//Frequency detection not run.
//			
//			if(video.isFrequencyDetectorStarted() == false){
//				//frequency detection hasn't been started.
//				model.runFrequencyDetector(video);
//			}
//		} else{
//			//Frequency detection complete
//			count++;
//		}

		return count;
		
	}
	
	/**
	 * Method to merge maps in videos.
	 * 
	 * @param model (AVSummaryModel object)
	 * @return Map of String to ArrayList of Section objects
	 */
	private static Map<String, ArrayList<Section>> mergeMaps(AVSummaryModel model, String module){
		Map<String, ArrayList<Section>> mergedMap = new HashMap<String, ArrayList<Section>>();
				
		//Loop to merge data from different videos together
		for (VideoFile videoFile : model.getLoadedVideos()) {
			
			if (videoFile.getSubtitleFilePath() != null) {
				//Subtitles file loaded for video so will contain maps
				
				Map<String, ArrayList<Section>> map = null;
				
				if(module.equals("People")){
					map = videoFile.getPeopleMentioned();
				} else if(module.equals("Location")){
					map = videoFile.getLocationsMentioned();
				}
				
				Iterator<String> it = map.keySet().iterator();
				
				while (it.hasNext()) {
					String name = it.next();
	
					if (mergedMap.containsKey(name)) {
						// meredMap contains name so merge ArrayList in mergedMap and map
						
						ArrayList<Section> appearances = mergedMap.get(name);
						appearances.addAll(map.get(name));
						mergedMap.put(name, appearances);
	
					} else {
						// mergedMap doesn't contain name so add ArrayList from map.
						
						mergedMap.put(name,map.get(name));
					}
				}
				
			}
		}
		
		sortMap(mergedMap);
		
		return mergedMap;
	}
		
	/**
	 * Method to merge loud sections in videos.
	 * 
	 * @param model (AVSummaryModel object)
	 */
	private static ArrayList<VolumeSection> mergeLoudSections(AVSummaryModel model){
		ArrayList<VolumeSection> mergedLoudSections = new ArrayList<VolumeSection>();
		
		//Loop to merge data from different videos together
		for (VideoFile videoFile : model.getLoadedVideos()) {
			mergedLoudSections.addAll(videoFile.getLoudSections());
		}
		
		Collections.sort(mergedLoudSections, new SectionComparator());
		
		return mergedLoudSections;
	}	

	/**
	 * Method to sort a Map in video filename and then time stamp order.
	 * 
	 * @param map (Map of String object to an ArrayList of Section Objects)
	 * @return Map<String,ArrayList<Section>>
	 */
	private static Map<String,ArrayList<Section>> sortMap(Map<String,ArrayList<Section>> map){
		Iterator<String> keyIt = map.keySet().iterator();
		
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			
			Collections.sort(map.get(key), new SectionComparator());
		}
		
		return map;
	}
	
	/**
	 * Method to produce an ArrayList of SummarySection objects to produce trailer with default priority.
	 * 
	 * @param peopleMentioned (ArrayList of Section objects)
	 * @param locationMentioned (ArrayList of Section objects)
	 * @param loudSections (ArrayList of VolumeSection objects)
	 * @return ArrayList of SummarySection objects
	 */
	private static ArrayList<SummarySection> createDefaultTrailer(Map<String, ArrayList<Section>> peopleMentioned,Map<String, ArrayList<Section>> locationMentioned, ArrayList<VolumeSection> loudSections){
		//SummarySection which contains all data and use shot times.
		ArrayList<SummarySection> summarySections = getShotTimesForLoudSections(loudSections);
		
		//Collapse Map objects
		ArrayList<Section> peopleMentionedSections = collapseMap(peopleMentioned);
		ArrayList<Section> locationMentionedSections = collapseMap(locationMentioned);
		
		//Add scores to SummarySection objects for people mentioned and location mentioned
		addPeopleMentionedScores(summarySections,peopleMentionedSections);
		addLocationMentionedScores(summarySections,locationMentionedSections);
		
		//Work out total scores for SummarySection objects
		calculateTotalScores(summarySections);
		
		//Sort sections according to criteria
		Collections.sort(summarySections, new TotalScoreComparator());
		
		return summarySections;
	}
	
	/**
	 * Method to produce an ArrayList of SummarySection objects to produce trailer with Loudness as priority.
	 * 
	 * @param peopleMentioned (ArrayList of Section objects)
	 * @param locationMentioned (ArrayList of Section objects)
	 * @param loudSections (ArrayList of VolumeSection objects)
	 * @return ArrayList of SummarySection objects
	 */
	private static ArrayList<SummarySection> createLoudnessTrailer(Map<String, ArrayList<Section>> peopleMentioned,Map<String, ArrayList<Section>> locationMentioned, ArrayList<VolumeSection> loudSections){
		//SummarySection which contains all data and use shot times.
		ArrayList<SummarySection> summarySections = getShotTimesForLoudSections(loudSections);
		
		//Collapse Map objects
		ArrayList<Section> peopleMentionedSections = collapseMap(peopleMentioned);
		ArrayList<Section> locationMentionedSections = collapseMap(locationMentioned);
		
		//Add scores to SummarySection objects for people mentioned and location mentioned
		addPeopleMentionedScores(summarySections,peopleMentionedSections);
		addLocationMentionedScores(summarySections,locationMentionedSections);
		
		//Work out total scores for SummarySection objects
		calculateTotalScores(summarySections);
		
		//Sort sections according to criteria
		Collections.sort(summarySections, new LoudnessComparator());
		
		return summarySections;
	}
	
	/**
	 * Method to produce an ArrayList of SummarySection objects to produce trailer with People mentioned as priority.
	 * 
	 * @param peopleMentioned (ArrayList of Section objects)
	 * @param locationMentioned (ArrayList of Section objects)
	 * @param loudSections (ArrayList of VolumeSection objects)
	 * @return ArrayList of SummarySection objects
	 */
	private static ArrayList<SummarySection> createPeopleMentionedTrailer(Map<String, ArrayList<Section>> peopleMentioned,Map<String, ArrayList<Section>> locationMentioned, ArrayList<VolumeSection> loudSections){
		//Collapse Map objects
		ArrayList<Section> peopleMentionedSections = collapseMap(peopleMentioned);
		ArrayList<Section> locationMentionedSections = collapseMap(locationMentioned);
	
		//SummarySection which contains all data and use shot times.
		ArrayList<SummarySection> summarySections = getShotTimesForMentionSections(peopleMentionedSections, "People");
	
		//Add scores to SummarySection objects for location mentioned and loudness
		addEffectiveSoundPressureScores(summarySections,loudSections);
		addLocationMentionedScores(summarySections,locationMentionedSections);
		
		//Work out total scores for SummarySection objects
		calculateTotalScores(summarySections);
		
		//Sort sections according to criteria
		Collections.sort(summarySections, new PeopleMentionedComparator());
		
		return summarySections;
	}
	
	/**
	 * Method to produce an ArrayList of SummarySection objects to produce trailer with Location mentioned as priority.
	 * 
	 * @param peopleMentioned (ArrayList of Section objects)
	 * @param locationMentioned (ArrayList of Section objects)
	 * @param loudSections (ArrayList of VolumeSection objects)
	 * @return ArrayList of SummarySection objects
	 */
	private static ArrayList<SummarySection> createLocationMentionedTrailer(Map<String, ArrayList<Section>> peopleMentioned,Map<String, ArrayList<Section>> locationMentioned, ArrayList<VolumeSection> loudSections){
		
		//Collapse Map objects
		ArrayList<Section> peopleMentionedSections = collapseMap(peopleMentioned);
		ArrayList<Section> locationMentionedSections = collapseMap(locationMentioned);
	
		//SummarySection which contains all data and use shot times.
		ArrayList<SummarySection> summarySections = getShotTimesForMentionSections(locationMentionedSections, "Location");
	
		//Add scores to SummarySection objects for location mentioned and loudness
		addEffectiveSoundPressureScores(summarySections,loudSections);
		addPeopleMentionedScores(summarySections, peopleMentionedSections);
		
		//Work out total scores for SummarySection objects
		calculateTotalScores(summarySections);
		
		//Sort sections according to criteria
		Collections.sort(summarySections, new LocationMentionedComparator());
		
		return summarySections;
	}
	
	/**
	 * Method to produce SummarySection objects representing shots from LoudSection objects.
	 * 
	 * @param loudSections (ArrayList of VolumeSection objects)
	 * @return ArrayList of SummarySection objects
	 */
	private static ArrayList<SummarySection> getShotTimesForLoudSections(ArrayList<VolumeSection> loudSections) {
		//ArrayList to return.
		ArrayList<SummarySection> shotTimes = new ArrayList<SummarySection>();
		
		Map<VolumeSection,ArrayList<VideoShot>> loudSectionToShots = new HashMap<VolumeSection,ArrayList<VideoShot>>();
		
		for(VolumeSection s : loudSections){	
			ArrayList<VideoShot> shots = s.getVideo().getShotsFromTimePoint(s.getStartTime(),s.getEndTime());
			loudSectionToShots.put(s, shots);	
		}
		System.out.println("LoudSectionTOSHots length: "+ loudSectionToShots.keySet().size());
		//Cleaning shots
		VideoFile currentVideo = null;
		long currentStart = 0;
		long currentEnd = 0;
		double currentEp = 0;
		
		for(VolumeSection s : loudSections){
			ArrayList<VideoShot> videoShots = loudSectionToShots.get(s);
			
			if(videoShots.size() == 0){
				continue;
			}
			
			VideoFile thisVideoFile = s.getVideo();
			long thisStart = videoShots.get(0).getTimecode().getTimecodeInMilliseconds();
			long thisEnd = videoShots.get(videoShots.size() - 1).getTimecode().getTimecodeInMilliseconds();
			double thisEp = s.getEffectiveSoundPressure();
			
			if(currentVideo == null && currentStart == 0 && currentEnd == 0){
				//First s so set current
				currentVideo = thisVideoFile;
				currentStart = thisStart;
				currentEnd = thisEnd;
				currentEp = thisEp;
			} else{
				
				if((currentVideo.getAbsolutePath()).equals(thisVideoFile.getAbsolutePath())){
					//Video files are the same.
					
					if(thisStart >= currentStart && thisEnd <= currentEnd){
						/* current and this are same or this contained in current so don't add. E.g.
						 * 
						 * 	currentStart/thisStart -> currentEnd/thisEnd
						 *  
						 *  or 
						 *  
						 *  currentStart -> thisStart -> thisEnd -> currentEnd
						 *  
						 *  or combination of above two cases.
						 */
						
						//Set currentEp to average of two sections.
						currentEp = (currentEp + thisEp) / 2;
	
					} else if(currentStart > thisStart && thisEnd > currentEnd){
						/* 
						 * DONT THINK USED BUT HERE JUST IN CASE DUE TO SECTION TO VIDEOSHOT ROUNDING.
						 * Shots overlap in this way:
						 * 	
						 * currentStart --> thisStart --> currentEnd --> thisEnd
						 */
						currentEnd = thisEnd;
						
						//Set currentEp to average of two sections.
						currentEp = (currentEp + thisEp) / 2;
						
					} else if(currentEnd + MERGE_DIFFERENCE >= thisStart){
						//Shots are 1 second apart then merge
						currentEnd = thisEnd;
	
						//Set currentEp to average of two sections
						currentEp = (currentEp + thisEp) / 2;
						
					} else{
						//Different so add current and set current to this values
						SummarySection ss = new SummarySection(currentVideo,currentStart,currentEnd);
						ss.setEffectiveSoundPressureScore(currentEp);
						
						shotTimes.add(ss);
						
						currentVideo = thisVideoFile;
						currentStart = thisStart;
						currentEnd = thisEnd;
						currentEp = thisEp;
					}
				} else{
					//Video files are different.
					SummarySection ss = new SummarySection(currentVideo,currentStart,currentEnd);
					ss.setEffectiveSoundPressureScore(currentEp);
					
					shotTimes.add(ss);
					
					currentVideo = thisVideoFile;
					currentStart = thisStart;
					currentEnd = thisEnd;
					currentEp = thisEp;
				}
			}
			
			if(loudSections.indexOf(s) == loudSections.size() - 1){
				//Last in list
				SummarySection ss = new SummarySection(currentVideo,currentStart,currentEnd);
				ss.setEffectiveSoundPressureScore(currentEp);
				
				shotTimes.add(ss);
			}
			
		}
		
		return shotTimes;
	}
	
	/**
	 * Method to produce SummarySection objects representing shots from Section objects.
	 * 
	 * @param sections (ArrayList of VolumeSection objects)
	 * @return ArrayList of SummarySection objects
	 */
	private static ArrayList<SummarySection> getShotTimesForMentionSections(ArrayList<Section> sections, String module) {
		//ArrayList to return.
		ArrayList<SummarySection> shotTimes = new ArrayList<SummarySection>();
		
		Map<Section,ArrayList<VideoShot>> sectionToShots = new HashMap<Section,ArrayList<VideoShot>>();
		
		for(Section s : sections){	
			ArrayList<VideoShot> shots = s.getVideo().getShotsFromTimePoint(s.getStartTime(),s.getEndTime());
			sectionToShots.put(s, shots);	
		}
		
		//Cleaning shots
		VideoFile currentVideo = null;
		long currentStart = 0;
		long currentEnd = 0;
		double currentNum = 0;
		
		for(Section s : sections){
			ArrayList<VideoShot> videoShots = sectionToShots.get(s);
			
			VideoFile thisVideoFile = s.getVideo();
			long thisStart = videoShots.get(0).getTimecode().getTimecodeInMilliseconds();
			long thisEnd = videoShots.get(videoShots.size() - 1).getTimecode().getTimecodeInMilliseconds();
			
			if(currentVideo == null && currentStart == 0 && currentEnd == 0){
				//First s so set current
				currentVideo = thisVideoFile;
				currentStart = thisStart;
				currentEnd = thisEnd;
				currentNum = 1;
			} else{
				
				if((currentVideo.getAbsolutePath()).equals(thisVideoFile.getAbsolutePath())){
					//Video files are the same.
					
					if(thisStart >= currentStart && thisEnd <= currentEnd){
						/* current and this are same or this contained in current so don't add. E.g.
						 * 
						 * 	currentStart/thisStart -> currentEnd/thisEnd
						 *  
						 *  or 
						 *  
						 *  currentStart -> thisStart -> thisEnd -> currentEnd
						 *  
						 *  or combination of above two cases.
						 */
						
						//Another mention in same section so add.
						currentNum += 1;
	
					} else if(currentStart > thisStart && thisEnd > currentEnd){
						/* 
						 * Shots overlap in this way:
						 * 	
						 * currentStart --> thisStart --> currentEnd --> thisEnd
						 */
						currentEnd = thisEnd;
						currentNum += 0.5;
						
					} else if(currentEnd + MERGE_DIFFERENCE >= thisStart){
						//Shots are 1 second apart then merge
						currentEnd = thisEnd;
						currentNum += 1;
						
					} else{
						//Different so add current and set current to this values
						SummarySection ss = new SummarySection(currentVideo,currentStart,currentEnd);
						
						if(module.equals("People")){
							ss.setPeopleMentionedScore(currentNum);
						} else if(module.equals("Location")){
							ss.setLocationMentionedScore(currentNum);
						}
						
						shotTimes.add(ss);
						
						currentVideo = thisVideoFile;
						currentStart = thisStart;
						currentEnd = thisEnd;
						currentNum = 1;
					}
				} else{
					//Video files are different.
					SummarySection ss = new SummarySection(currentVideo,currentStart,currentEnd);
					if(module.equals("People")){
						ss.setPeopleMentionedScore(currentNum);
					} else if(module.equals("Location")){
						ss.setLocationMentionedScore(currentNum);
					}
					
					shotTimes.add(ss);
					
					currentVideo = thisVideoFile;
					currentStart = thisStart;
					currentEnd = thisEnd;
					currentNum = 1;
				}
			}
			
			if(sections.indexOf(s) == sections.size() - 1){
				//Last in list
				SummarySection ss = new SummarySection(currentVideo,currentStart,currentEnd);
	
				if(module.equals("People")){
					ss.setPeopleMentionedScore(currentNum);
				} else if(module.equals("Location")){
					ss.setLocationMentionedScore(currentNum);
				}
				
				shotTimes.add(ss);
			}
			
		}
		
		return shotTimes;
	}
	
	/**
	 * Method to collapse Map into a single ArrayList of Section objects
	 * 
	 * @param mentions (Map of String to Section objects)
	 * @return ArrayList of Section objects
	 */
	private static ArrayList<Section> collapseMap(Map<String, ArrayList<Section>> mentions){
		ArrayList<Section> mentionSections = new ArrayList<Section>();
		
		if (mentions != null) {
			Iterator<String> it = mentions.keySet().iterator();

			//Loop to merge all Section objects into one ArrayList
			while (it.hasNext()) {
				String name = it.next();
				mentionSections.addAll(mentions.get(name));
			}
		}
		
		//Sort mentionSections list
		Collections.sort(mentionSections,new SectionComparator());
		
		return mentionSections;
	}
	
	/**
	 * Method to add People mentioned scores to SummarySection objects which will make up trailer.
	 * 
	 * @param sectionsToScore (ArrayList of SummarySection objects)
	 * @param sections (ArrayList of SummarySection objects)
	 */
	private static void addPeopleMentionedScores(ArrayList<SummarySection> sectionsToScore, ArrayList<Section> sections){
		for(SummarySection ss : sectionsToScore){
			for(Section s : sections){
				//Get score.
				double contains = ss.contains(s.getVideo(), s.getStartTime(), s.getEndTime());
				
				ss.setPeopleMentionedScore(ss.getPeopleMentionedScore() + contains);
			}
		}
	}
	
	/**
	 * Method to add Location mentioned scores to SummarySection objects which will make up trailer.
	 * 
	 * @param sectionsToScore (ArrayList of SummarySection objects)
	 * @param sections (ArrayList of SummarySection objects)
	 */
	private static void addLocationMentionedScores(ArrayList<SummarySection> sectionsToScore, ArrayList<Section> sections){
		for(SummarySection ss : sectionsToScore){
			for(Section s : sections){
				//Get score.
				double contains = ss.contains(s.getVideo(), s.getStartTime(), s.getEndTime());
				
				ss.setLocationMentionedScore(ss.getLocationMentionedScore() + contains);
			}
		}
	}
	
	/**
	 * Method to add ESP scores to SummarySection objects which will make up trailer.
	 * 
	 * @param sectionsToScore (ArrayList of SummarySection objects)
	 * @param sections (ArrayList of SummarySection objects)
	 */
	private static void addEffectiveSoundPressureScores(ArrayList<SummarySection> sectionsToScore, ArrayList<VolumeSection> sections){
		for(SummarySection ss : sectionsToScore){
			for(VolumeSection s : sections){
				//Get score.
				double contains = ss.contains(s.getVideo(), s.getStartTime(), s.getEndTime());
				
				if(contains != 0){
					ss.setEffectiveSoundPressureScore((ss.getEffectiveSoundPressureScore() + (contains * s.getEffectiveSoundPressure())) / 2);
				}
			}
		}
	}
	
	/**
	 * Method to calculate total scores for all SummarySection objects.
	 * 
	 * @param sections (ArrayList of SummarySection objects)
	 */
	private static void calculateTotalScores(ArrayList<SummarySection> sections){
		for(SummarySection ss : sections){
			ss.calculateTotalScore();
		}
	}
	
	/**
	 * Method to produce the trailer.
	 * 
	 * @param sections (ArrayList of SummarySection objects)
	 * @return File object
	 */
	private static File produceTrailerFile(ArrayList<SummarySection> sections, long duration){
		
		long time = 0;
		int count = 0;
		ArrayList<SummarySection> sectionsForTrailer = new ArrayList<SummarySection>();
		
		//Choose SummarySection objects from sections ArrayList while less than target duration and count is less then size of sections ArrayList
		while(time < duration && count < sections.size()){
			SummarySection s = sections.get(count);
			
			time += (s.getEndTime() - s.getStartTime());
			
			if(time >= duration){
				//Adding next SummarySection will be over duration so don't
				break;
			}
			
			sectionsForTrailer.add(s);
			count++;
		}
				
		//Sort sectionsForTrailer back into video / time order
		Collections.sort(sectionsForTrailer,new SectionComparator());
		
		ArrayList<File> segments = new ArrayList<File>();
		
		for(SummarySection s : sectionsForTrailer){
			File file = new File(s.getVideo().getAbsolutePath());
			
			File nextSegment = VideoFileHelper.splitVideo(file, s.getStartTime(), s.getEndTime());
			segments.add(nextSegment);
		}
		
		File trailer = VideoFileHelper.concatFiles(segments);
				
		return trailer;
	}
}