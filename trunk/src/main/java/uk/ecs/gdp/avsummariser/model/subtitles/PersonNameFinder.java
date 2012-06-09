package uk.ecs.gdp.avsummariser.model.subtitles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.Section;

import com.moviejukebox.thetvdb.model.Actor;

/**
 * Class to find person names in Subtitle Objects using either OpenNLP processing or TVDB data.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class PersonNameFinder extends NameFinder{
		
	/**
	 * Function to find and associate character names with videoFile object using TVDB.
	 * 
	 * @param video (VideoFile object)
	 * @param actors (List of Actor Objects)
	 * @param genre (Selected genre from list of genres in Series Object)
	 */
	public static void findPersonNamesInSubtitlesUsingTVDB(VideoFile video, List<Actor> actors, String genre){		
		ArrayList<Subtitle> subtitles = video.getSubtitles();
		Map<String,ArrayList<Section>> peopleMentioned = new HashMap<String,ArrayList<Section>>();
		
		for(Subtitle s : subtitles){
			
			//Speech with punctuation removed.
			String speech = s.getSpeech().replaceAll("[^A-Za-z ]","");
					
			for(Actor a : actors){
				//Actor name
				String name = a.getName();
				
				//Character name
				String role = a.getRole();
				
				//Character names
				ArrayList<String> roles = new ArrayList<String>();
				
				//Multiple roles contained in role String.
				if(role.contains("|")){
					//Split on |
					String[] temp = role.split("|");
					for(String r : temp){
						roles.add(r);
					}
				} else if(role.contains("/")){
					//Split on /
					String[] temp = role.split("/");
					for(String r : temp){
						roles.add(r);
					}
				}

				//Genre if
				if(genre.equals("Documentary") || genre.equals("Game Show") || genre.equals("News")){
					//Actual actor names used.
					if(containsName(name, speech)){
						//Contains name
						addToMap(peopleMentioned,name,video,s);
					}
				} else{
					//Default use role names.
		
					if(roles.isEmpty()){
						//roles ArrayList not used.
						if(containsRole(role,speech)){
							//Contains role
							addToMap(peopleMentioned,role,video,s);
						}
						
					} else{
						//roles ArrayList used.
						for(String r : roles){
							if(containsRole(r, speech)){
								addToMap(peopleMentioned,r,video,s);
							}
						}
					}
				}
			}
		}
		
		video.setPeopleMentioned(peopleMentioned);
	}
	
	/**
	 * Function to find and associate person names with VideoFile object using OpenNLP. 
	 * 
	 * @param video (VideoFile object)
	 */
	public static void findPersonNamesInSubtitlesUsingOpenNLP(VideoFile video){
		
		setPersonNameModel();
		
		ArrayList<Subtitle> subtitles = video.getSubtitles();	
		ArrayList<String> mergedSubtitles = mergeSubtitleLinesAccordingToColour(subtitles);
		ArrayList<String> allPersonNamesFound = getAllPersonNames(mergedSubtitles);
		ArrayList<String> uniquePersonNamesFound = getUniquePersonNames(allPersonNamesFound);
		ArrayList<String> mainPersonNames = getMainPersonNames(subtitles, allPersonNamesFound, uniquePersonNamesFound);

		associatePersonNamesWithVideo(video,mainPersonNames);
	}
		
	/**
	 * Method to set the TokenNameFinderModel for person names.
	 */
	private static void setPersonNameModel(){
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream("resources/en-ner-person.bin");
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
	 * Method to determine whether role has nickname and uses combination of nickname or full name to call containsName.
	 * 
	 * @param role (String Object)
	 * @param speech (String Object)
	 * @return boolean (boolean)
	 */
	private static boolean containsRole(String role, String speech){
		
		//Nickname detection
		String nickName = "";
		int nickNameStart = role.indexOf("\"");
		
		if(nickNameStart != -1){
			//Nickname found so set.
			nickName = role.substring(nickNameStart + 1, role.lastIndexOf("\""));
		}
		
		if(nickName != ""){
			//Has nickname
			boolean contains = containsName(nickName, speech);
			
			if(contains == true){
				//Nickname used so add.
				return true;
			} else{
				//Nickname not used so try full name (role with nickname removed)
				return containsName(role.replace("\"" + nickName + "\" ",""), speech);
			}
		} else{
			//Doesn't have nickname.
			return containsName(role, speech);
		}
	}
	
	/**
	 * Method to determine whether any parts of a name are contained in speech.
	 * 
	 * 
	 * @param name (String Object)
	 * @param speech (String Object)
	 * @return boolean (boolean)
	 */
	private static boolean containsName(String name, String speech){
		String[] parts = name.split(" ");
		
		//Loop to check if part of name used e.g just firstname, lastname
		for(int i = 0; i < parts.length; i++){
			
			if(parts[i].equalsIgnoreCase("the")){
				//Ignore word the used in name (for example, The Stig, The Doctor) to avoid unnecessary matches.
				continue;
			}
			
			if(speech.contains(parts[i])){
				int sI = speech.indexOf(parts[i]);
				char aN;
				
				try{
					aN = speech.charAt(sI + parts[i].length());
				} catch(StringIndexOutOfBoundsException e){
					aN = " ".toCharArray()[0];
				}
				
				if(aN == " ".toCharArray()[0]){
					return true;
				}
			}
		}	
		return false;
	}

	/**
	 * Method to get all person names mentioned in merged Subtitle objects.
	 * 
	 * @param mergedLines (ArrayList of Strings which are produced from mergeSubtitle... method)
	 * @return ArrayList of Strings contains all Person names found.
	 */
	private static ArrayList<String> getAllPersonNames(ArrayList<String> mergedLines){
		ArrayList<String> allPersonNamesFound = new ArrayList<String>();		
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
				Span nameSpans[] = nameFinder.find(sentenceTokens);
				
				for(Span name : nameSpans){
					String nameToAdd = "";
					
					//Merge tokens that make up name into single String object.
					for(int j = name.getStart(); j < name.getEnd(); j++){
					
						nameToAdd += sentenceTokens[j];
						
						if(!(j + 1 >= name.getEnd())){
							nameToAdd += " ";
						}
					}
					
					//Add name to ArrayList
					allPersonNamesFound.add(nameToAdd.trim());
					
				}
				
				//Clears data learnt so far (Can move round to vary locations found)
				nameFinder.clearAdaptiveData();
			}
		}
		
		return allPersonNamesFound;
	}
		
	/**
	 * Method to find unique person names in allPersonNames ArrayList.
	 * 
	 * @param personNames (ArrayList of String Objects containing all person names found)
	 * @return ArrayList of Strings (Containing all unique person names)
	 */
	private static ArrayList<String> getUniquePersonNames(ArrayList<String> personNames){
		ArrayList<String> uniquePersonNames = new ArrayList<String>();
		
		for(String personName : personNames){
			//boolean whether already added to uniqueNames ArrayList
			boolean added = false;
			
			
			//Start index of a name String in another String
			int sI;
			//Char that exists after name in another String.
			char aN;
				  
			for(int i = 0 ; i < uniquePersonNames.size() ; i++){
				String n = uniquePersonNames.get(i);
				
				if(n.equals(personName)){
					//n already added
					added = true;
					break;
				} else if(n.contains(personName)){
					//n contains personName
					sI = n.indexOf(personName);
										
					try{
						aN = n.charAt(sI + personName.length());
					} catch(StringIndexOutOfBoundsException e){
						aN = " ".toCharArray()[0];
					}
					
					if(aN == " ".toCharArray()[0]){
						if(n.length() > personName.length()){
							//n is longer than personName (meaning n could be full name and personName is shorter version) so already added.
							added = true;
							break;
						}
					}
				} else if(personName.contains(n)){
					sI = personName.indexOf(n);
					
					try{
						aN = personName.charAt(sI + n.length());
					} catch(StringIndexOutOfBoundsException e){
						aN = " ".toCharArray()[0];
					}
					
					if(aN == " ".toCharArray()[0]){
						if(n.length() < personName.length()){
							//personName is longer than n (meaning personname could be fullname of n) so needs to be replaced.
							uniquePersonNames.set(i, personName);
							added = true;
							break;
						}
					}
				}
			}
				
			if(!added){
				//personName hasn't been added so add to uniquePersonName ArrayList.
				uniquePersonNames.add(personName);
			}
		}
		
		return uniquePersonNames;
	}
		
	/**
	 * Method to return main person names.
	 * 
	 * @param subtitles (ArrayList of Subtitle objects)
	 * @param uniquePersonNamesFound (ArrayList produced from getUniquePersonNames)
	 * @param allPersonNamesFound (ArrayList produced from getAllPersonNames)
	 * @return ArrayList of String Objects
	 */
	private static ArrayList<String> getMainPersonNames(ArrayList<Subtitle> subtitles, ArrayList<String> allPeopleNamesFound, ArrayList<String> uniquePersonNamesFound){
		
		ArrayList<NameScore> nameScores = new ArrayList<NameScore>();
		ArrayList<String> mainPersonNames = new ArrayList<String>();
		
		for(String n : uniquePersonNamesFound){
			String[] parts = n.split(" ");
			
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
			nameScores.add(new NameScore(n, counter));
		}
		
		//Sort NameObjects in descending order.
		Collections.sort(nameScores, new NameScoreComparator());
		
		//Work out number of names to pick from NameScore objects.
		int numberToPick = (int) Math.round(allPeopleNamesFound.size() * (MAIN / 100));
		
		for(int i = 0; i < numberToPick; i++){
			mainPersonNames.add(nameScores.get(i).getName());
		}		
		
		return mainPersonNames;	
	}
	
	/**
	 * Method to set mainPersonNamesMentioned variable in VideoFile object with Map of person names mentioned to ArrayList of Appearance objects.
	 * 
	 * @param video (VideoFile object)
	 * @param mainPersonNames (ArrayList of String Objects)
	 */
	private static void associatePersonNamesWithVideo(VideoFile video, ArrayList<String> mainPersonNames){
		ArrayList<Subtitle> subtitles = video.getSubtitles();
		Map<String,ArrayList<Section>> peopleMentioned = new HashMap<String,ArrayList<Section>>();
		
		for(Subtitle s : subtitles){
			
			//Removing all punctuation from speech
			String speech = s.getSpeech().replaceAll("[^A-Za-z ]","");
			
			for(String n : mainPersonNames){
				
				String[] parts = n.split(" ");
				
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
							addToMap(peopleMentioned, n, video, s);
							break;
						}
					}
				}
			}
		}
		video.setPeopleMentioned(peopleMentioned);
	}
}
