package uk.ecs.gdp.avsummariser.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jfree.data.xy.XYSeries;

import uk.ecs.gdp.avsummariser.model.facialdetection.VideoCharacter;

import uk.ecs.gdp.avsummariser.model.section.FrequencySection;
import uk.ecs.gdp.avsummariser.model.section.Section;
import uk.ecs.gdp.avsummariser.model.section.VolumeSection;
import uk.ecs.gdp.avsummariser.model.subtitles.Subtitle;
import uk.ecs.gdp.avsummariser.model.time.TimeUtils;

/**
 * Class to represent a loaded video and all the data associated with it.
 * 
 * @author Michael Harris
 * @author Jonathan Harrison
 * @author Samantha Kanza
 * @version 1
 */
public class VideoFile {
	
	//Minimum shot lenght required in finding shots for a Section object
	private static final double SHOT_LENGTH = 3000;
	
	//Video file variables
	private String friendlyName;
	private String fileName;
	private String absolutePath;
	
	//File path to subtitle file
	private String subtitleFilePath;
	//List of parsed Subtitle objects
	private ArrayList<Subtitle> subtitles;
	
	//List of video shots
	private ArrayList<VideoShot> shots;
	//Boolean whether shot detection started
	private boolean videoShotDetectionStarted;
	//Boolean whether shot detection finished
	private boolean videoShotDetectionFinished;

	//Map of people name Strings to where mentioned Section objects
	private Map<String, ArrayList<Section>> peopleMentioned;
	//Boolean whether person name finder started.
	private boolean personNameFinderStarted;
	//Boolean whether person name finder finished.
	private boolean personNameFinderFinished;

	//Map of location name Strings to where mentioned Section objects
	private Map<String, ArrayList<Section>> locationsMentioned;
	//Boolean whether location name finder started.
	private boolean locationNameFinderStarted;
	//Boolean whether location name finder finished.
	private boolean locationNameFinderFinished;
	
	//List of all VolumeSection objects created for audio.
	private ArrayList<VolumeSection> allVolumes;
	//List of all loudest VolumeSection objects from audio
	private ArrayList<VolumeSection> loudSections;
	//Boolean whether volume detector started.
	private boolean volumeDetectorStarted;
	//Boolean whether volume detector finished.
	private boolean volumeDetectorFinished;
	
	//List of all FrequencySection objects created for audio.
	private ArrayList<FrequencySection> allFrequencies;
	//Boolean whether frequency detector started.
	private boolean frequencyDetectorStarted;
	//Boolean whether frequency detector finished.
	private boolean frequencyDetectorFinished;
	
	
	//ADD INTO SYSTEM PROPERLY	
	private ArrayList<VideoCharacter> characterAppearances;
	private boolean characterFinderStarted;
	private boolean characterFinderFinished;
	//

	public VideoFile(String friendlyName, String fileName, String absolutePath) {
		super();
		
		this.setShots(new ArrayList<VideoShot>());
		this.friendlyName = friendlyName;
		this.fileName = fileName;
		this.absolutePath = absolutePath;

		//Set booleans
		personNameFinderStarted= false;
		personNameFinderFinished = false;
		
		locationNameFinderStarted = false;
		locationNameFinderFinished = false;
		
		volumeDetectorStarted = false;
		volumeDetectorFinished = false;

		frequencyDetectorStarted = false;
		frequencyDetectorFinished = false;
		
		characterFinderStarted = false;
	}
	
	/**
	 * Method to get friendly name string.
	 * 
	 * @return String object
	 */
	public String getFriendlyName() {
		return friendlyName;
	}

	/**
	 * Method to get file name string.
	 * 
	 * @return String object
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Method to get absolute path string.
	 * 
	 * @return String object
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	//Subtitles
	/**
	 * Method to get subtitle file path string.
	 * 
	 * @return String object
	 */
	public String getSubtitleFilePath() {
		return subtitleFilePath;
	}

	/**
	 * Method to set path to subtitle file.
	 * 
	 * @param subtitleFilePath (String object)
	 */
	public void setSubtitleFilePath(String subtitleFilePath) {
		this.subtitleFilePath = subtitleFilePath;
	}

	/**
	 * Method to get list of Subtitle Objects.
	 * 
	 * @return ArrayList of Subtitle objects.
	 */
	public synchronized ArrayList<Subtitle> getSubtitles() {
		return subtitles;
	}

	/**
	 * Method to set list of Subtitle Objects.
	 * 
	 * @param subtitles (ArrayList of Subtitle objects)
	 */
	public void setSubtitles(ArrayList<Subtitle> subtitles) {
		this.subtitles = subtitles;
	}
	//
	
	//Person name finder methods
	/**
	 * Method to find out if person name finder has been started.
	 * 
	 * @return boolean value
	 */
	public boolean isPersonNameFinderStarted() {
		return personNameFinderStarted;
	}
	
	/**
	 * Method to find out if person name finder has been finished.
	 * 
	 * @return boolean value
	 */
	public boolean isPersonNameFinderFinished() {
		return personNameFinderFinished;
	}

	/**
	 * Method to set person name finder started to true.
	 */
	public void setPersonNameFinderStarted() {
		personNameFinderStarted = true;
	}
	
	/**
	 * Method to set person name finder finished to true.
	 */
	public void setPersonNameFinderFinished() {
		personNameFinderFinished = true;
	}
	
	/**
	 * Method to get people mentioned map.
	 * 
	 * @return Map of String to ArrayList of Section objects
	 */
	public Map<String, ArrayList<Section>> getPeopleMentioned() {
		return peopleMentioned;
	}

	/**
	 * Method to set people mentioned map.
	 * 
	 * @param peopleMentioned (Map of String objects to ArrayList of Section objects)
	 */
	public void setPeopleMentioned(Map<String, ArrayList<Section>> peopleMentioned) {
		this.peopleMentioned = peopleMentioned;
	}
	//
	
	//Location name finder methods
	/**
	 * Method to find out if location name finder has started.
     *
	 * @return boolean value
	 */
	public boolean isLocationNameFinderStarted() {
		return locationNameFinderStarted;
	}
	
	/**
	 * Method to find out if location name finder has finished.
	 * 
	 * @return boolean value
	 */
	public boolean isLocationNameFinderFinished() {
		return locationNameFinderFinished;
	}

	/**
	 * Method to set location name finder started to true
	 */
	public void setLocationNameFinderStarted() {
		locationNameFinderStarted = true;
	}
	
	/**
	 * Method to set location name finder finish to true.
	 */
	public void setLocationNameFinderFinished() {
		locationNameFinderFinished = true;
	}
	
	/**
	 * Method to get location mentioned map.
	 * 
	 * @return Map of String to ArrayList of Section objects
	 */
	public Map<String, ArrayList<Section>> getLocationsMentioned() {
		return locationsMentioned;
	}

	/**
	 * Method to set location mentioned map.
	 * 
	 * @param locationsMentioned (Map of String objects to ArrayList of Section objects)
	 */
	public void setLocationMentioned(Map<String, ArrayList<Section>> locationsMentioned) {
		this.locationsMentioned = locationsMentioned;
	}
	//
	
	//Shot detection methods
	/**
	 * Method to find out whether video shot detection has started.
	 * 
	 * @return boolean value
	 */
	public boolean isVideoShotDetectionStarted() {
		return videoShotDetectionStarted;
	}

	/**
	 * Method to find out whether video shot detection has finished.
	 * 
	 * @return boolean value
	 */
	public boolean isVideoShotDetectionFinished() {
		return videoShotDetectionFinished;
	}
	
	/**
	 * Method to set video shot detection started to true.
	 */
	public void setVideoShotDetectionStarted() {
		this.videoShotDetectionStarted = true;
	}
	
	/**
	 * Method to set video shot detection finished to true.
	 */
	public void setVideoShotDetectionFinished() {
		this.videoShotDetectionFinished = true;
	}

	/**
	 * Method to set list of video shots.
	 * 
	 * @param shots (ArrayList of VideoShot objects)
	 */
	public void setShots(ArrayList<VideoShot> shots) {
		this.shots = shots;
	}

	/**
	 * Method to get list of video shots.
	 * 
	 * @return ArrayList of VideoShot Objects
	 */
	public ArrayList<VideoShot> getShots() {
		return shots;
	}	
	//
	
	//Volume detection methods
	/**
	 * Method to find out whether volume detector has started.
	 * 
	 * @return boolean value
	 */
	public boolean isVolumeDetectorStarted(){
		return volumeDetectorStarted;
	}
	
	/**
	 * Method to find out whether volume detector has finished.
	 * 
	 * @return boolean value
	 */
	public boolean isVolumeDetectorFinished(){
		return volumeDetectorFinished;
	}
	
	/**
	 * Method to set volume detector started as true
	 */
	public void setVolumeDetectorStarted(){
		volumeDetectorStarted = true;
	}
	
	/*
	 * Method to set volume detector finished as true.
	 */
	public void setVolumeDetectorFinished(){
		volumeDetectorFinished = true;
	}
	
	/**
	 * Method to set list of all VolumeSection objects
	 * 
	 * @param allVolumes (ArrayList of VolumeSection objects)
	 */
	public void setAllVolumes(ArrayList<VolumeSection> allVolumes){
		this.allVolumes = allVolumes;
	}
	
	/**
	 * Method to get list of all VolumeSection objects
	 * 
	 * @return ArrayList of all VolumeSection objects
	 */
	public ArrayList<VolumeSection> getAllVolumes(){
		return allVolumes;
	}
	
	/**
	 * Method to set list of loud VolumeSection objects.
	 * 
	 * @param loudSections (ArrayList of VolumeSection objects)
	 */
	public void setLoudSections(ArrayList<VolumeSection> loudSections){
		this.loudSections = loudSections;
	}
	
	/**
	 * Method to get list of loud VolumeSection objects.
	 * 
	 * @return ArrayList of VolumeSection objects
	 */
	public ArrayList<VolumeSection> getLoudSections(){
		return loudSections;
	}	
	//
	
	//Frequency detection methods
	/**
	 * Method to find out whether frequency detector has started.
	 * 
	 * @return boolean value
	 */
	public boolean isFrequencyDetectorStarted(){
		return frequencyDetectorStarted;
	}
	
	/**
	 * Method to find out whether frequency detector has finished.
	 * 
	 * @return boolean value
	 */
	public boolean isFrequencyDetectorFinished(){
		return frequencyDetectorFinished;
	}
	
	/**
	 * Method to set frequency detector started to true.
	 */
	public void setFrequencyDetectorStarted(){
		frequencyDetectorStarted = true;
	}
	
	/**
	 * Method to set frequency detector finished to true.
	 */
	public void setFrequencyDetectorFinished(){
		frequencyDetectorFinished = true;
	}
	
	/**
	 * Method to set list of all FrequencySection objects.
	 * 
	 * @param allFrequencies (ArrayList of FrequencySection objects)
	 */
	public void setAllFrequencies(ArrayList<FrequencySection> allFrequencies){
		this.allFrequencies = allFrequencies;
	}
	
	/**
	 * Method to get list of all FrequencySection objects
	 *
	 * @return ArrayList of FrequencySection objects
	 */
	public ArrayList<FrequencySection> getAllFrequencies(){
		return allFrequencies;
	}

	/**
	 * Method to get list of VideoShot objects given a Section objects start and end time in milliseconds.
	 * 
	 * @param start (long value)
	 * @param end (long value)
	 * @return ArrayList of VideoShot objects
	 */
	public ArrayList<VideoShot> getShotsFromTimePoint(long start,long end) {
		boolean foundStart = false;
		ArrayList<VideoShot> returnedShots = new ArrayList<VideoShot>();

		if (start == shots.get(0).getTimecode().getTimecodeInMilliseconds()) {
			//Start time is first shot so set foundStart to true and add shot.
			
			foundStart = true;
			returnedShots.add(shots.get(0));
		}

		for (int i = 1; i < shots.size(); i++) {
			
			if (foundStart == true && shots.get(i).getTimecode().getTimecodeInMilliseconds() <= end) {
				//Adding because found start shot
				returnedShots.add(shots.get(i));
				
			} else if (foundStart == true && shots.get(i).getTimecode().getTimecodeInMilliseconds() > end) {
				//Adding but now found end
				
				if(shots.get(i).getTimecode().getTimecodeInMilliseconds() - returnedShots.get(0).getTimecode().getTimecodeInMilliseconds() >= SHOT_LENGTH){
					/*Shots length is greater or equal to SHOT_LENGTH we make sure that the segments of video are at least this 
					 * long, with the aim to make a smoother trailer.
					 */
					break;
				} else{ 
					//Shots length is less than SHOT_LENGTH
					
					returnedShots.add(shots.get(i));
					
					
				}
			} else if (foundStart == false && shots.get(i).getTimecode().getTimecodeInMilliseconds() >= start) {
				//Found start
				
				foundStart = true;
				returnedShots.add(shots.get(i-1));
				returnedShots.add(shots.get(i));
			
			}
		}

		return returnedShots;
	}

	//Graph data
	/**
	 * Method to Video shot data in a form that can be plotted on a graph.
	 * 
	 * @return XYSeries object
	 */
	public XYSeries shotData() {
		XYSeries series = new XYSeries("Shot Data");
		int i;

		for (i = 0; i < shots.size() - 1; i++) {
			long shotLength = (shots.get(i + 1).getTimecode().getTimecodeInMilliseconds() - shots.get(i).getTimecode().getTimecodeInMilliseconds()) / 1000;
			double shotTime = TimeUtils.formatMillisIntoSeconds(shots.get(i).getTimecode().getTimecodeInMilliseconds());
			series.add(shotTime, shotLength);
		}
		return series;

	}

	/**
	 * Method to volume data in a form that can be plotted on a graph.
	 * 
	 * @return XYSeries object
	 */
	public XYSeries volumeData() {
		XYSeries series = new XYSeries("Volume Data");
		
		if (allVolumes != null) {
			//allVolumes has been set
			
			for (VolumeSection volumeSection : allVolumes) {
				series.add(TimeUtils.formatMillisIntoSeconds(volumeSection.getStartTime()), volumeSection.getEffectiveSoundPressure()/1000);
			}
		}
		return series;

	}

	/**
	 * Method to people names mentioned data in a form that can be plotted on a graph.
	 * 
	 * @return XYSeries object
	 */
	public ArrayList<XYSeries> mentionedNamesData() {
		ArrayList<XYSeries> returnSeries = new ArrayList<XYSeries>();
		
		if (peopleMentioned != null) {
			//peopleMentioned map has been set.
			
			Iterator<String> peopleIt = peopleMentioned.keySet().iterator();

			while (peopleIt.hasNext()) {
				
				String name = peopleIt.next();
				XYSeries series = new XYSeries(name + " Mentioned");
				ArrayList<Section> appearances = peopleMentioned.get(name);

				for (Section appearance : appearances) {
					for (int i = 0; i < 30; i++) {
						series.add(TimeUtils.formatMillisIntoSeconds(appearance.getStartTime()), i);
					}
				}
				returnSeries.add(series);
			}
		} else {
			//peopleMentioned map not been set.
			
			returnSeries.add(new XYSeries(""));
		}
		return returnSeries;

	}

	/**
	 * Method to location names mentioned data in a form that can be plotted on a graph.
	 * 
	 * @return XYSeries object
	 */
	public ArrayList<XYSeries> mentionedLocationData() {
		ArrayList<XYSeries> returnSeries = new ArrayList<XYSeries>();

		Iterator<String> locationIt = locationsMentioned.keySet().iterator();

		while (locationIt.hasNext()) {
			
			String name = locationIt.next();
			XYSeries series = new XYSeries(name + " Mentioned");
			ArrayList<Section> appearances = locationsMentioned.get(name);

			for (Section appearance : appearances) {
				for (int i = 0; i < 30; i++) {
					series.add(TimeUtils.formatMillisIntoSeconds(appearance.getStartTime()), i);
				}
			}
			returnSeries.add(series);
		}

		return returnSeries;

	}
	
	
	//Character detection methods
	public void setCharacterFinderStarted(){
		characterFinderStarted = true;
	}
	
	public void setCharacterFinderFinished(){
		characterFinderFinished = true;
	}
	
	public void setCharactersAppearances(ArrayList<VideoCharacter> characterAppearances){
		this.characterAppearances = characterAppearances;
	}

		
	public ArrayList<XYSeries> characterAppearances()
	{
		ArrayList<XYSeries> returnSeries = new ArrayList<XYSeries>();
		
		for(int i = 0; i < characterAppearances.size(); i++)
		{
			XYSeries charSeries = new XYSeries(characterAppearances.get(i).getName());
			for(Long timestamp : characterAppearances.get(i).getTimestamps())
			{
				charSeries.add(TimeUtils.formatMillisIntoSeconds(timestamp), i);
			}
			
			returnSeries.add(charSeries);
		}
		
		return returnSeries;
	}

}
