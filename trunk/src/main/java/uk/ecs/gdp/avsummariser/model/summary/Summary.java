package uk.ecs.gdp.avsummariser.model.summary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.Section;
import uk.ecs.gdp.avsummariser.model.section.VolumeSection;

import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

/**
 * Class to contain all data produced from processing and summarising a set of Videos.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class Summary {
	
	//List of videoFiles used to produce trailer.
	private ArrayList<VideoFile> videoFiles;
	//String describing genre selected (Maybe be "")
	private String genre; 
	//String describing type of trailer
	private String trailerType;

	//Series object for TV series from TVDB.com (Maybe null)
	private Series series;
	//Actors list for TV series from TVDB.com (Maybe null)
	private List<Actor> actors;
	
	//Merged people mentioned in subtitles.
	private Map<String,ArrayList<Section>> mergedPeopleMentioned;
	//Merged locations mentioned in subtitles.
	private Map<String,ArrayList<Section>> mergedLocationsMentioned;
	//Merged loud sections
	private ArrayList<VolumeSection> mergedLoudSections;
	
	//TODO ADD FACE DETECTION
	
	//File which is trailer produced by system.
	private File trailer;
		
	public Summary(ArrayList<VideoFile> videoFiles, String genre, String trailerType, Series series, List<Actor> actors, Map<String,ArrayList<Section>> mergedPeopleMentioned, Map<String,ArrayList<Section>> mergedLocationMentioned, ArrayList<VolumeSection> mergedLoudSections, File trailer){
		this.videoFiles = videoFiles;
		this.genre = genre;
		this.trailerType = trailerType;
		
		this.series = series;
		
		this.actors = actors;
		
		this.mergedPeopleMentioned = mergedPeopleMentioned;
		this.mergedLocationsMentioned = mergedLocationMentioned;
		this.mergedLoudSections = mergedLoudSections;
		
		this.trailer = trailer;
	}
	
	/**
	 * Method to get list of VideoFile objects used in Summary.
	 * 
	 * @return videoFiles (ArrayList of VideoFile objects)
	 */
	public ArrayList<VideoFile> getVideoFiles(){
		return videoFiles;
	}
	
	/**
	 * Method to get genre used in producing Summary.
	 * 
	 * @return genre (String object)
	 */
	public String getGenre(){
		return genre;
	}
	
	/**
	 * Method to get trailer type used to construct Trailer.
	 * 
	 * @return trailerType (String object)
	 */
	public String getTrailerType(){
		return trailerType;
	}
	
	/**
	 * Method to get Series object used in constructing Summary.
	 * 
	 * @return series (Series object)
	 */
	public Series getSeries(){
		return series;
	}

	/**
	 * Method to get list of Actor objects used in constructing Summary.
	 * 
	 * @return actors (List of Actor objects)
	 */
	public List<Actor> getActors(){
		return actors;
	}
	
	/**
	 * Method to get Map of merged people mentioned.
	 * 
	 * @return mergedPeopleMentioned (Map of String to Section objects).
	 */
	public Map<String,ArrayList<Section>> getMergedPeopleMentioned(){
		return mergedPeopleMentioned;
	}
	
	/**
	 * Method to get Map of merged locations mentioned.
	 * 
-	 * @return mergedLocationsMentioned (Map of String to Section objects)
	 */
	public Map<String,ArrayList<Section>> getMergedLocationMentioned(){
		return mergedLocationsMentioned;
	}
	
	/**
	 * Method to get ArrayList of merged loud sections
	 * 
	 * @return mergedLoudSections (ArrayList of Volume objects)
	 */
	public ArrayList<VolumeSection> getMergedLoudSections(){
		return mergedLoudSections;
	}
	
	/**
	 * Method to get Trailer produced.
	 * 
	 * @return trailer (File object)
	 */
	public File getTrailer(){
		return trailer;
	}
	
	/**
	 * Method to set Trailer object.
	 * 
	 * @param file (File object)
	 */
	public void setTrailer(File file){
		trailer = file;
	}
}
