package uk.ecs.gdp.avsummariser.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.xuggle.XuggleVideo;
import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

import uk.ecs.gdp.avsummariser.controller.listener.ShotDetectedListenerImpl;
import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.facialdetection.CharacterDetectedObserver;
import uk.ecs.gdp.avsummariser.model.facialdetection.CharacterFinder;
import uk.ecs.gdp.avsummariser.model.facialdetection.VideoCharacter;
import uk.ecs.gdp.avsummariser.model.subtitles.NameFinder;
import uk.ecs.gdp.avsummariser.model.summary.Summary;
import uk.ecs.gdp.avsummariser.model.threads.ExportSummaryThread;
import uk.ecs.gdp.avsummariser.model.threads.FrequencyDetectorThread;
import uk.ecs.gdp.avsummariser.model.threads.GenerateSummaryThread;
import uk.ecs.gdp.avsummariser.model.threads.LocationNameFinderThread;
import uk.ecs.gdp.avsummariser.model.threads.PersonNameFinderThread;
import uk.ecs.gdp.avsummariser.model.threads.SeriesSearchThread;
import uk.ecs.gdp.avsummariser.model.threads.SubtitlesParseThread;
import uk.ecs.gdp.avsummariser.model.threads.VideoShotDetectorThread;
import uk.ecs.gdp.avsummariser.model.threads.VolumeDetectorThread;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Class which represents the Model for this application and stores all information / state of the system.
 * 
 * @author Jonathan Harrison
 * @author Michael Harris
 * @author Samantha Kanza
 * @version 1
 */
public class AVSummaryModel {

	//ArrayList of loaded videos.
	private ArrayList<VideoFile> loadedVideos;
	//Currently selected VideoFile object
	private VideoFile currentVFile;
	//Display for playing video
	private VideoDisplay<MBFImage> display;

	//Stores objects from TVDB.com
	private Series chosenSeries;
	private List<Actor> seriesActors;
	
	//Stores values from GUI
	private String genreSelected;
	private String trailerDuration;
	private String trailerTypeSelected;
	
	//Stores Summary object produced by system.
	private Summary summary;

	private ArrayList<VideoCharacter> characters;
	
	public AVSummaryModel() {
		//Set default values for variables
		loadedVideos = new ArrayList<VideoFile>();
		currentVFile = null;
		display = null;
		
		characters = new ArrayList<VideoCharacter>();
		chosenSeries = null;
		seriesActors = null;
		
		genreSelected = "";
		trailerDuration = "10:00";
		trailerTypeSelected = "Default";
		
		summary = null;
		
		//Set up sentence and token models to be used in 
		NameFinder.setUpSentenceAndTokenModels();
	}

	//TVDB objects
	/**
	 * Method to set chosen Series object.
	 * 
	 * @param series (Series object)
	 */
	public void setChosenSeries(Series series) {
		chosenSeries = series;
	}

	/**
	 * Method to get chosen Series object.
	 * 
	 * @return series (Series object)
	 */
	public Series getChosenSeries() {
		return chosenSeries;
	}
	
	/**
	 * Method to set chosen series Actor List.
	 * 
	 * @param actors (List of Actor objects)
	 */
	public void setSeriesActors(List<Actor> actors) {
		seriesActors = actors;
	}

	/**
	 * Method to get chosen series Actor List.
	 * 
	 * @return seriesActors (List of Actor objects)
	 */
	public List<Actor> getSeriesActors() {
		return seriesActors;
	}
	//
	
	//Genre
	/**
	 * Method to set genre selected,
	 * 
	 * @param selected (String object)
	 */
	public void setGenreSelected(String selected){
		genreSelected = selected;
	}
	
	/**
	 * Method to get genre selected.
	 * 
	 * @return genreSelected (String object)
	 */
	public String getGenreSelected(){
		return genreSelected;
	}
	//
	
	//Trailer duration
	/**
	 * Method to set trailer duration
	 * 
	 * @param duration (String object)
	 */
	public void setTrailerDuration(String duration){
		trailerDuration = duration;
	}
	
	/**
	 * Method to get trailer duration
	 * 
	 * @return trailerDuration (String object)
	 */
	public String getTrailerDuration(){
		return trailerDuration;
	}
	//
	
	//Trailer type
	/**
	 * Method to set trailer type selected
	 * 
	 * @param selected (String object)
	 */
	public void setTrailerTypeSelected(String selected){
		trailerTypeSelected = selected;
	}
	
	/**
	 * Method to get trailer type selected
	 * 
	 * @return trailerTypeSelected (String object)
	 */
	public String getTrailerTypeSelected(){
		return trailerTypeSelected;
	}
	//

	//Summary
	/**
	 * Method to set Summary
	 * 
	 * @param summary (Summary Object)
	 */
	public void setSummary(Summary summary){
		this.summary = summary;
	}
	
	/**
	 * Method to get Summary object.
	 * 
	 * @return summary (Summary object)
	 */
	public Summary getSummary(){
		return summary;
	}

	/**
	 * Method to generate a Summary object using data in system.
	 * 
	 * @param view (AVSummaryView object)
	 */
	public void generateSummary(AVSummaryView view) {
		Thread thread = new GenerateSummaryThread(this,view);
		thread.start();
	}

	/**
	 * Method to play Summary trailer in view
	 * 
	 * @param view (AVSummaryView object)
	 */
	public void playSummary(AVSummaryView view) {
		try {
			view.playVideo(AVSummaryModel.getVideoFromPath(summary.getTrailer().getAbsolutePath()));
		} catch (FileNotPlayableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to export Summary object
	 * 
	 * @param view (AVSummaryView object)
	 */
	public void exportSummary(AVSummaryView view){
		Thread thread = new ExportSummaryThread(this,view);
		thread.start();
	}
	//
	
	//Videos
	/**
	 * Method to get list of loaded videos. 
	 * 
	 * @return ArrayList of VideoFile objects
	 */
	public ArrayList<VideoFile> getLoadedVideos() {
		return loadedVideos;
	}

	/**
	 * Method to create a new VideoFile object from a file and add it to the loaded videos list.
	 * 
	 * @param inputFile (File object)
	 * @return VideoFile object
	 */
	public VideoFile addVideoFile(File inputFile) {
		VideoFile videoFile = new VideoFile(inputFile.getName(),inputFile.getName(), inputFile.getAbsolutePath());
		loadedVideos.add(videoFile);
		
		return videoFile;
	}

	/**
	 * Method to get Video object from a file path.
	 * 
	 * @param path (String object)
	 * @return Video object
	 * @throws FileNotPlayableException
	 */
	public static Video<MBFImage> getVideoFromPath(String path)throws FileNotPlayableException {
		try {
			Video<MBFImage> video = new XuggleVideo(new File(path));
			return video;
		} catch (Exception e) {
			throw new FileNotPlayableException();
		}
	}

	/**
	 * Method to set selected video.
	 * 
	 * @param vFile (VideoFile object)
	 */
	public void setSelectedVideo(VideoFile vFile) {
		currentVFile = vFile;
	}

	/**
	 * Method to get the selected video.
	 * 
	 * @return VideoFile object
	 */
	public VideoFile getSelectedVideo() {
		return currentVFile;
	}

	/**
	 * Method to remove a video from the system.
	 * 
	 * @param videoFile (VideoFile object)
	 */
	public void removeVideoItem(VideoFile videoFile) {
		loadedVideos.remove(videoFile);
	}
	//
		
	public CharacterDetectedObserver getCharactersInVideo(VideoFile videoFile) throws FileNotPlayableException	{
		
		Video<MBFImage> video = getVideoFromPath(videoFile
				.getAbsolutePath());
		CharacterDetectedObserver observer = new CharacterDetectedObserver();
		
		if(video != null && !seriesActors.isEmpty())
		{
			videoFile.setCharacterFinderStarted();
			CharacterFinder finder = new CharacterFinder(video, videoFile, 5, seriesActors, genreSelected);
			finder.addObserver(observer);
			Thread thread = new Thread(finder);
			thread.start();
		}
		else
		{
			System.out.println("In order to find the characters in a video you need to specify the video and the series");
		}

		return observer;
	}
	
	//Run methods
	/**
	 * Method to run subtitle parser.
	 * 
	 * @param view (AVSummaryView object)
	 * @param item (VideoBrowserItem object)
	 */
	public void runSubtitleParser(AVSummaryView view, VideoBrowserItem item){
		Thread thread = new SubtitlesParseThread(view, this, item);
		thread.start();
	}
	
	/**
	 * Method to run series search
	 * 
	 * @param view (AVSummaryView object)
	 */
	public void runSeriesSearch(AVSummaryView view){
		Thread thread = new SeriesSearchThread(view,this);
		thread.start();
	}
	
	/**
	 * Method to run PeopleFinder on VideoFile object.
	 * 
	 * @param view (AVSummaryView object)
	 * @param video (VideoFile object)
	 */
	public void runPeopleFinder(AVSummaryView view, VideoFile video){

		if(video.isPersonNameFinderFinished() == false){
			//Person name finder not finished
		
			if(video.isPersonNameFinderStarted() == false){
				//Not been started already.
				
				//Set person name finder started.
				video.setPersonNameFinderStarted();
				
				Thread personThread = new PersonNameFinderThread(this,view,video);
				personThread.start();
			}
		} else{
			//Person name finder been run.
			
			if(view != null){
				//If view is null then don't update view.
				view.getFrame().getPersonNamesDetailPanel().update();
			}
		}
	}
	
	/**
	 * Method to run LocationFinder on VideoFile object.
	 * 
	 * @param view (AVSummaryView object)
	 * @param video (VideoFile object)
	 */
	public void runLocationFinder(AVSummaryView view, VideoFile video){

		if(video.isLocationNameFinderFinished() == false){
			//Location name finder not finished
		
			if(video.isLocationNameFinderStarted() == false){
				//Not been started already.
				
				//Set location name finder started.
				video.setLocationNameFinderStarted();
				
				Thread locationThread = new LocationNameFinderThread(view,video);
				locationThread.start();
			}
		} else{
			//Location name finder been run.
			
			if(view != null){
				//If view is null then don't update view.
				view.getFrame().getLocationNamesDetailPanel().update();
			}
		}
	}
	
	/**
	 * Method to run ShotDetector on VideoFile object.
	 * 
	 * @param video (VideoFile object)
	 */
	public ShotDetectedListenerImpl runShotDetection(VideoFile video)throws FileNotPlayableException {
		ShotDetectedListenerImpl shotDetectorListenerImpl = new ShotDetectedListenerImpl(video);
		
		if(video.isVideoShotDetectionFinished() == false){
			//Video shot detection not finished
			
			if(video.isVideoShotDetectionStarted() == false){
				//Video shot detection not started already
				
				//Set shot detection started.
				video.setVideoShotDetectionStarted();
				
				VideoShotDetectorThread videoShotDetectorThread = new VideoShotDetectorThread(video, shotDetectorListenerImpl);
				videoShotDetectorThread.start();
			}
		}
		
		return shotDetectorListenerImpl;
	}
	
	/**
	 * Method to run VolumeDetector on VideoFile object.
	 * 
	 * @param video (VideoFile object)
	 */
	public void runVolumeDetector(VideoFile video){
		if(video.isVolumeDetectorFinished() == false){
			//Volume detector not finished
		
			if(video.isVolumeDetectorStarted() == false){
				//Not been started already.
				
				//Set volume detector started to true
				video.setVolumeDetectorStarted();
				
				Thread volumeDetector = new VolumeDetectorThread(video);
				volumeDetector.start();
			}
		} 
	}
	
	/**
	 * Method to run FrequencyDetector on VideoFile object.
	 * 
	 * @param video (VideoFile object)
	 */
	public void runFrequencyDetector(VideoFile video){
		if(video.isFrequencyDetectorFinished() == false){
			//Frequency detector not finished
		
			if(video.isFrequencyDetectorStarted() == false){
				//Not been started already.
				
				//Set frequency detector started to true
				video.setFrequencyDetectorStarted();
				
				Thread frequencyDetector = new FrequencyDetectorThread(video);
				frequencyDetector.start();
			}
		} 
	}
	//	
	
}
