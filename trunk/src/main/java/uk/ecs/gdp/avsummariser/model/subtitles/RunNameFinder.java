package uk.ecs.gdp.avsummariser.model.subtitles;

import java.util.List;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import com.moviejukebox.thetvdb.model.Actor;

/**
 * Class to run name finders either person name or location. Created to overcome non thread safe Objects in OpenNLP.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class RunNameFinder {

	//Boolean to determine whether name finder in use.
	private static boolean inUse = false;
	
	/**
	 * Method to run either person or location name finder depending on parameters.
	 * 
	 * @param video (VideoFile object)
	 * @param personNames (boolean value)
	 * @param useTVDB (boolean value)
	 * @param actors (List of Actor objects)
	 * @param genre (String object)
	 */
	public synchronized static void runNameFinder(VideoFile video, boolean personNames, boolean useTVDB, List<Actor> actors, String genre){
		
		inUse = true;
		
		if(personNames){
			//If true means find people names
			
			if(useTVDB){
				//If true means use TVDB information.
				PersonNameFinder.findPersonNamesInSubtitlesUsingTVDB(video, actors, genre);
			} else{
				//Else use OpenNLP.
				PersonNameFinder.findPersonNamesInSubtitlesUsingOpenNLP(video);
			}
		} else{
			//Else find location names.
			LocationNameFinder.findLocationsInSubtitles(video);
		}
		
		inUse = false;
	}
	
	/**
	 * Method to determine whether name fidner is being run on subtitles.
	 * 
	 * @return inUse (boolean value)
	 */
	public synchronized static boolean isInUse(){
		return inUse;
	}
}
