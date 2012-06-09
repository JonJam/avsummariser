package uk.ecs.gdp.avsummariser.model.tvdb;

import java.util.List;
import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

/**
 * Class to communicate with TVDB.com via API to get Series and List of Actor objects for a chosen TV Series.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class TVDBSearcher {
	
	/*
	 * Notes about TVDB
	 * 	- Name Searching
	 * 		- Role String may contain:
	 *			- /, | i.e. played multiple different characters
	 *				- SOLUTION: ArrayList created of separate roles
	 *			- Various Roles i.e. played different characters 
	 * 			
	 * 			- "" shortened version of name
	 * 				- SOLUTION: Search on nicknames first and if not such on full names
	 *  		- Genre issues:
	 *				- Documentary / Game Show / News
	 *  				- Use Actor Names when searching for names
	 *  - Images
	 *  	- Animation
	 *  		- Pictures are of Actors so won't match up to cartoon
	 *  		- E.g. Family Guy, The Simpsons
	 *  
	 */
	
	//API Key for TVDBAPI
	private static final String TVDBAPIKEY = "485EF6F976DC65A1";
	//Language using for search
	private static final String LANG = "en";
	
	/**
	 * Get all Series for given programme name
	 * 
	 * @param programmeName
	 * @return results (List of Series Objects matching programmeName searchTerm)
	 */
	public static List<Series> getAllSeries(String programmeName){
		TheTVDB tvDB = new TheTVDB(TVDBAPIKEY);
		List<Series> results = tvDB.searchSeries(programmeName, LANG);
		return results;
	}
	
	/**
	 * Get specific series, given user interaction.
	 * 
	 * @param seriesId
	 * @return selectedSeries (Series Object matching chosen series from dropdown in GUI)
	 */
	public static Series getChosenSeries(String seriesId){
		TheTVDB tvDB = new TheTVDB(TVDBAPIKEY);
		Series selectedSeries = tvDB.getSeries(seriesId, LANG);
		return selectedSeries;
	}
	
	/**
	 * Get Actor objects for given seriesId.
	 * 
	 * @param seriesId
	 * @return actors (List of Actor Objects for given seriesId.
	 */
	public static List<Actor> getSeriesActors(String seriesId){
		TheTVDB tvDB = new TheTVDB(TVDBAPIKEY);
		List<Actor> actors = tvDB.getActors(seriesId);
		return actors;
	}
}
