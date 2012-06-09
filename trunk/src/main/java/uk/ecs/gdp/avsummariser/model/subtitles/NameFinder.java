package uk.ecs.gdp.avsummariser.model.subtitles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.Section;

/**
 * Parent class for NameFinder classes. It contains common variables and methods.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class NameFinder {
	
	//Percentage of names to choose as main names.
	protected static final double MAIN = 5;
	protected static SentenceModel sentenceModel = null;
	protected static TokenizerModel tokenModel = null;
	protected static TokenNameFinderModel nameModel = null;
		
	/**
	 * Method to set up sentence and token models.
	 */
	public static void setUpSentenceAndTokenModels(){
		InputStream modelIn1 = null;
		InputStream modelIn2 = null;
		
		try {
			modelIn1 = new FileInputStream("resources/en-sent.bin");
			modelIn2 = new FileInputStream("resources/en-token.bin");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			sentenceModel = new SentenceModel(modelIn1);
			tokenModel = new TokenizerModel(modelIn2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to merge Subtitle objects if colour is same.
	 * 
	 * @param subtitles (ArrayList of Subtitle Objects)
	 * @return ArrayList of Strings (Merged subtitles)
	 */
	protected static ArrayList<String> mergeSubtitleLinesAccordingToColour(ArrayList<Subtitle> subtitles){
		
		String previousColour = "";
		String previousSpeech = "";
		Iterator<Subtitle> loop = subtitles.iterator();
		ArrayList<String> mergedLines = new ArrayList<String>();
		
		while(loop.hasNext()){
			Subtitle s = loop.next();
			String speech = s.getSpeech();
			String colour = s.getColour();
			
			if(previousColour.equals("")){
				//First Subtitle object processed.
				previousColour = colour;
				previousSpeech = speech;
				
			} else if(previousColour.equals(colour)){
				//Colour of Subtitle object is the same as the previous
				previousSpeech += " " + speech;
			} else{
				//Colour of Subtitle object is different to previous so add to ArrayList and reset for new colour.
				
				//Add previous colour
				mergedLines.add(previousSpeech);
				
				previousColour = colour;
				previousSpeech = speech;
			}
			
			if(loop.hasNext() == false){
				//No next Subtitle object so add to ArrayList.
				mergedLines.add(previousSpeech);
				
				previousSpeech = "";
				previousColour = "";
			}
		}
		
		return mergedLines;
	}
	
	/**
	 * Method to remove actions and punctuation from tokens.
	 * 
	 * @param tokens (String array of tokens produced by TokenizerME
	 * @return String array of clean tokens 
	 */
	protected static String[] cleanTokens(String[] tokens){
			
		for(int t = 0; t < tokens.length; t++){
			String toTest = tokens[t];
			tokens[t] = cleanString(toTest);
		}	
		return tokens;
	}

	/**
	 * Method to clean a String of actions and punctuation.
	 * 
	 * @param s (String object representing a token)
	 * @return String (Clean String Object)
	 */
	protected static String cleanString(String s){
		if ((s.toUpperCase()).equals(s) || s.matches("[^A-Za-z]")){
			s = "";
		}
		return s;
	}
	
	/**
	 * Creates Section objects and adds them to the passed Map object.
	 * 
	 * @param map (Map object to add to)
	 * @param name (Name String to use a key)
	 * @param video (VideoFile object which name occured in)
	 * @param s (Subtitle object which name occured in)
	 */
	protected static void addToMap(Map<String,ArrayList<Section>> map, String name, VideoFile video, Subtitle s){
		if(map.containsKey(name)){
			//Map contains key
			ArrayList<Section> appearances = map.get(name);
			appearances.add(new Section(video,s.getBeginTime(),s.getEndTime()));
			map.put(name,appearances);
		} else{
			//Map doesn't contain key
			ArrayList<Section> appearances = new ArrayList<Section>();
			appearances.add(new Section(video,s.getBeginTime(),s.getEndTime()));
			map.put(name,appearances);
		}
	}
}
