package uk.ecs.gdp.avsummariser.model.subtitles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.Section;
/**
 * Class to find location names in Subtitle Objects using OpenNLP processing.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class LocationNameFinder extends NameFinder{
	
	/**
	 * Function to find and associate location names with a Video. 
	 * 
	 * @param video (VideoFile video)
	 */
	public static void findLocationsInSubtitles(VideoFile video){
		
		setLocationNameModel();
		
		ArrayList<Subtitle> subtitles = video.getSubtitles();
		ArrayList<String> mergedSubtitles = mergeSubtitleLinesAccordingToColour(subtitles);
		ArrayList<String> allLocationNamesFound = getAllLocationNames(mergedSubtitles);
		ArrayList<String> uniqueLocationsFound = getUniqueLocations(allLocationNamesFound);
		ArrayList<String> mainLocations = getMainLocations(subtitles, allLocationNamesFound, uniqueLocationsFound);
		
		associateLocationsWithAppearances(video,mainLocations);
	}
	
	/**
	 * Method to set the TokenNameFinderModel for location names.
	 */
	private static void setLocationNameModel(){
		InputStream modelIn = null;
		
		try {
			modelIn = new FileInputStream("resources/en-ner-location.bin");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			nameModel = new TokenNameFinderModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Method to get all location names mentioned in merged Subtitle objects.
	 * 
	 * @param mergedLines (ArrayList of Strings which are produced from mergeSubtitle... method)
	 * @return ArrayList of Strings contains all Location names found.
	 */
	private static ArrayList<String> getAllLocationNames(ArrayList<String> mergedLines){
		ArrayList<String> allLocationsFound = new ArrayList<String>();	
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		NameFinderME nameFinder = new NameFinderME(nameModel);
		
		for(String mergedLine : mergedLines){		

			//Split up String into sentences.
			String[]sentences = sentenceDetector.sentDetect(mergedLine);
			
			for(int i = 0; i < sentences.length; i++){		
							
				//Split sentences into tokens.
				String[] sentenceTokens = tokenizer.tokenize(sentences[i]);		
				
				//Remove Actions and punctuation from tokens. (Can move round to vary locations found).
				sentenceTokens = cleanTokens(sentenceTokens);
				
				//Find locations in tokens.
				Span locationSpans[] = nameFinder.find(sentenceTokens);
				
				for(Span location : locationSpans){
					String locationToAdd = "";
					
					//Merge tokens that make up location into single String object.
					for(int j = location.getStart(); j < location.getEnd(); j++){
					
						locationToAdd += sentenceTokens[j];
						
						if(!(j + 1 >= location.getEnd())){
							locationToAdd += " ";
						}
						
						//Clears data learnt so far (Can move round to vary locations found)
						nameFinder.clearAdaptiveData();
					}
					
					//Add name to ArrayList
					allLocationsFound.add(locationToAdd.trim());
					
				}
				
			}
		}
		
		return allLocationsFound;
	}
	
		
	/**
	 * Method to find unique locations in allLocationNames ArrayList.
	 * 
	 * @param locations (ArrayList of String Objects containing all locations found)
	 * @return ArrayList of Strings (Containing all unique location names)
	 */
	private static ArrayList<String> getUniqueLocations(ArrayList<String> locations){
		ArrayList<String> uniqueLocations = new ArrayList<String>();
		
		for(String location : locations){
			if(!uniqueLocations.contains(location)){
				uniqueLocations.add(location);
			}
		}
		
		return uniqueLocations;
	}
		
	/**
	 * Method to return main locations.
	 * 
	 * @param subtitles (ArrayList of Subtitle objects)
	 * @param allLocationsNamesFound (ArrayList produced from getAllLocationNames)
	 * @param uniqueLocationsFound (ArrayList produced from getUniqueLocations)
	 * @return
	 */
	private static ArrayList<String> getMainLocations(ArrayList<Subtitle> subtitles, ArrayList<String> allLocationNamesFound, ArrayList<String> uniqueLocationsFound){
		ArrayList<NameScore> nameScores = new ArrayList<NameScore>();
		ArrayList<String> mainLocations = new ArrayList<String>();
		
		for(String l : uniqueLocationsFound){
			
			String[] parts = l.split(" ");
			
			int counter = 0;
			for(Subtitle s : subtitles){
				
				//Removing all punctuation from speech
				String speech = s.getSpeech().replaceAll("[^A-Za-z ]","");
				
				for(String p : parts){
					
					if(speech.contains(p)){
						//speech contains p
						
						//Start index of p within speech.
						int sI = speech.indexOf(p);
						//Char after p within speech.
						char aN;
						
						try{
							aN = speech.charAt(sI + p.length());
						} catch(StringIndexOutOfBoundsException e){
							aN = " ".toCharArray()[0];
						}
						
						if(aN == " ".toCharArray()[0]){
							//Matched name so add to count.
							counter++;
							break;
						}
					}
				}
			}
			
			//Create new NameScore object for name.
			nameScores.add(new NameScore(l, counter));
		}
		
		//Sort NameObjects in descending order.
		Collections.sort(nameScores, new NameScoreComparator());
		
		//Work out number of names to pick from NameScore objects.
		int numberToPick = (int) Math.round(allLocationNamesFound.size() * (MAIN / 100));
		
		for(int i = 0; i < numberToPick; i++){
			mainLocations.add(nameScores.get(i).getName());
		}		
		
		return mainLocations;	
	}
	
	/**
	 * Method to set locationsMentioned variable in each VideoFile with Map of location names mentioned to ArrayList of Section objects.
	 * 
	 * @param video (VideoFile object)
	 * @param mainLocations (ArrayList of String Objects)
	 */
	private static void associateLocationsWithAppearances(VideoFile video, ArrayList<String> mainLocations){
		ArrayList<Subtitle> subtitles = video.getSubtitles();
		Map<String,ArrayList<Section>> locationsMentioned = new HashMap<String,ArrayList<Section>>();
		
		for(Subtitle s : subtitles){
			
			String speech = s.getSpeech().replaceAll("[^A-Za-z ]","");
			
			for(String l : mainLocations){
				if(speech.contains(l)){
					addToMap(locationsMentioned, l, video, s);
				}
			}
		}
		
		video.setLocationMentioned(locationsMentioned);
	}
}
