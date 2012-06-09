package uk.ecs.gdp.avsummariser.model.summary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.VideoShot;
import uk.ecs.gdp.avsummariser.model.section.Section;
import uk.ecs.gdp.avsummariser.model.section.VolumeSection;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Class to export a Summary object into 3 files.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class ExportSummary {

	//File name of XML file containing summary data
	private static final String SUMMARY_XML_FILENAME = "summary_xml";
	//File name of XML file containing all data.
	private static final String DATA_XML_FILENAME = "data_xml";
	//Export directory
	private static File exportDirectory;
	//Summary object
	private static Summary summary;
	
	
	/**
	 * Method to export summary object.
	 * 
	 * @param view (AVSummaryView object)
	 * @param model (AVSummaryModel object)
	 */
	public static void exportSummary(AVSummaryView view, AVSummaryModel model){
		
		//Get summary
		summary = model.getSummary();
		
		//Set up file chooser so user can choose export directory.
		JFileChooser directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int returnVal = directoryChooser.showOpenDialog(view.getFrame());
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			exportDirectory = directoryChooser.getSelectedFile();
			exportTrailer();
			exportSummaryXML();
			exportDataXML();
		}
	}
	
	/**
	 * Method to export the trailer file.
	 * 
	 * @return File object
	 */
	private static void exportTrailer(){
		File trailer = summary.getTrailer();
		
		//Get filename
		String fileName = trailer.getName();
		
		//Get file extension
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		
		File newName = new File(exportDirectory.getAbsoluteFile() + System.getProperty("file.separator") + "trailer." + ext);
		
		//Move file to new directory
		boolean success = trailer.renameTo(newName);
		
		if(success){
			//Due to file move of summary trailer need to update objects
			summary.setTrailer(newName);
		} 
	}
	
	/**
	 * Method to export Summary data in an XML file.
	 * 
	 */
	private static void exportSummaryXML(){
		Document doc = new Document();
		
		Element root = new Element("summary");
		doc.setRootElement(root);
		
		
		Element genreUsed = new Element("genre-used");
		genreUsed.setText(summary.getGenre());
		root.addContent(genreUsed);
		
		
		Element trailerElement = new Element("trailer");
		root.addContent(trailerElement);
		
		
		Element trailerType = new Element("trailer-type-used");
		trailerType.setText(summary.getTrailerType());
		trailerElement.addContent(trailerType);
		
		
		Element trailerFile = new Element("trailer-file");
		trailerFile.setText(summary.getTrailer().getAbsolutePath());
		trailerElement.addContent(trailerFile);
		
		
		Series series = summary.getSeries();
		List<Actor> actorsList = summary.getActors();
		
		if(series != null && actorsList != null){
			//If not null then produce XML elements for Series and List of Actor objects
			
			Element seriesObject = new Element("series-object");
		
			Element seriesName = new Element("series-name");
			seriesName.setText(series.getSeriesName());
			seriesObject.addContent(seriesName);
		
			Element overview = new Element("series-overview");
			overview.setText(series.getOverview());
			seriesObject.addContent(overview);
		
			Element imdbId = new Element("imdb-id");
			imdbId.setText(series.getImdbId());
			seriesObject.addContent(imdbId);
		
			Element tvdbId = new Element("tvdb-id");
			tvdbId.setText(series.getId());
			seriesObject.addContent(tvdbId);
		
			Element genres = new Element("genres");
		
			//Loop to produce genre Elements for each genre this TV series comes under.
			for(String g : series.getGenres()){
				Element genre = new Element("genre");
				genre.setText(g);
			
				genres.addContent(genre);
			}
			
			seriesObject.addContent(genres);
			root.addContent(seriesObject);

			
			Element actors = new Element("series-actors");
			
			//Loop to produce actor Elements for each actor in this TV series.
			for(Actor a : actorsList){
				Element actor = new Element("actor");
				
				Element actorName = new Element("actor-name");
				actorName.setText(a.getName());
				actor.addContent(actorName);
				
				Element roleName = new Element("role-name");
				roleName.setText(a.getRole());
				actor.addContent(roleName);
				
				Element actorImage = new Element("actor-image");
				actorImage.setText(a.getImage());
				actor.addContent(actorImage);
				
				actors.addContent(actor);
			}
		
			root.addContent(actors);
		}
		
		
		Element filesUsed = new Element("files-used");
		
		//Loop to produce video-object elements for each VideoFile Object used in producing the summary
		for(VideoFile v : summary.getVideoFiles()){
			Element videoObject = new Element("video-object");
			
			Element video = new Element("video-file");
			video.setText(v.getAbsolutePath());
			videoObject.addContent(video);
			
			Element subtitle = new Element("subtitle-file");
			subtitle.setText(v.getSubtitleFilePath());
			videoObject.addContent(subtitle);
			
			filesUsed.addContent(videoObject);
		}
		root.addContent(filesUsed);
		
		
		Map<String, ArrayList<Section>> peopleMentionedMap = summary.getMergedPeopleMentioned();
		root.addContent(produceElementForMap(peopleMentionedMap, "people"));
		
		
		Map<String, ArrayList<Section>> locationMentionedMap = summary.getMergedLocationMentioned();
		root.addContent(produceElementForMap(locationMentionedMap, "location"));
		
		
		ArrayList<VolumeSection> loudSectionObjects = summary.getMergedLoudSections();
		root.addContent(produceElementForVolumeSection(loudSectionObjects, "loud-section"));
			
		outputXMLFile(doc, SUMMARY_XML_FILENAME);
	}
	
	/**
	 * Method to export data in a XML file
	 * 
	 */
	private static void exportDataXML(){
		ArrayList<VideoFile> videos = summary.getVideoFiles();
		
		Document doc = new Document();
		
		Element root = new Element("data");
		doc.setRootElement(root);
		
		Element videoObjects = new Element("video-objects");
		
		//Loop through VideoFile objects and create elements
		for(VideoFile v : videos){
			Element videoObject = new Element("video-object");
			
			Element videoFile = new Element("video-file");
			videoFile.setText(v.getAbsolutePath());
			videoObject.addContent(videoFile);
			
			
			Element subtitle = new Element("subtitle-file");
			subtitle.setText(v.getSubtitleFilePath());
			videoObject.addContent(subtitle);
			
			
			Map<String, ArrayList<Section>> peopleMentionedMap = v.getPeopleMentioned();
			videoObject.addContent(produceElementForMap(peopleMentionedMap, "people"));
			
			
			Map<String, ArrayList<Section>> locationMentionedMap = v.getLocationsMentioned();
			videoObject.addContent(produceElementForMap(locationMentionedMap, "location"));
			
		
			ArrayList<VolumeSection> volumeSectionObjects = v.getAllVolumes();
			videoObject.addContent(produceElementForVolumeSection(volumeSectionObjects, "volume-section"));
			
			
			ArrayList<VideoShot> videoShotObjects = v.getShots();
			Element videoShots = new Element("video-shots");
			
			//Loop to produce elements for video shots
			for(VideoShot vS : videoShotObjects){
				Element videoShot = new Element("video-shot");
				
				Element startTime = new Element("start-time");
				startTime.setText(Long.toString(vS.getTimecode().getTimecodeInMilliseconds()));
				videoShot.addContent(startTime);
				
				videoShots.addContent(videoShot);
			}
			videoObject.addContent(videoShots);
			
			
			videoObjects.addContent(videoObject);
		}
			
		root.addContent(videoObjects);
		
		outputXMLFile(doc,DATA_XML_FILENAME);
	}
	
	/**
	 * Method to produce Element object for Maps.
	 * 
	 * @param map (Map of String to ArrayList of Section objects)
	 * @param module (String object)
	 * @return Element object
	 */
	private static Element produceElementForMap(Map<String, ArrayList<Section>> map, String module){
		Element mentioned = new Element(module + "-mentioned");
		
		if(map != null){
			//Map is not null
			
			Set<String> keySet = map.keySet();
			
			if(keySet.size() != 0){
				//Keyset is not empty
				
				Iterator<String> it = keySet.iterator();
				
				//Loop to produce elements for Section
				while (it.hasNext()) {
					String key = it.next();
					
					Element type = new Element(module);
					
					Element name = new Element("name");
					name.setText(key);
					type.addContent(name);
					
					Element mentions = new Element("mentions");
					type.addContent(mentions);
					
					for(Section s : map.get(key)){
						Element mention = new Element("mention");
						
						Element video = new Element("video-file");
						video.setText(s.getVideo().getAbsolutePath());
						mention.addContent(video);
						
						Element startTime = new Element("start-time");
						startTime.setText(Long.toString(s.getStartTime()));
						mention.addContent(startTime);
						
						Element endTime = new Element("end-time");
						endTime.setText(Long.toString(s.getEndTime()));
						mention.addContent(endTime);
						
						mentions.addContent(mention);
					}
					
					mentioned.addContent(type);
				}
			}
		}
		
		return mentioned;
	}
	
	/**
	 * Method to produce Element object for an ArrayList of VolumeSection objects
	 * 
	 * @param volumes (ArrayList of VolumeSection objects)
	 * @param name (String object)
	 * @return Element object
	 */
	private static Element produceElementForVolumeSection(ArrayList<VolumeSection> volumes, String name){
		Element volumeSections = new Element(name + "s");
		
		//Loop to produce elements for VolumeSections.
		for(VolumeSection v : volumes){
			Element volumeSection = new Element(name);
			
			Element video = new Element("video-file");
			video.setText(v.getVideo().getAbsolutePath());
			volumeSection.addContent(video);
			
			Element startTime = new Element("start-time");
			startTime.setText(Long.toString(v.getStartTime()));
			volumeSection.addContent(startTime);
			
			Element endTime = new Element("end-time");
			endTime.setText(Long.toString(v.getEndTime()));
			volumeSection.addContent(endTime);
			
			Element eP = new Element("effective-sound-pressure");
			eP.setText(Double.toString(v.getEffectiveSoundPressure()));
			volumeSection.addContent(eP);
			
			volumeSections.addContent(volumeSection);
		}
		
		return volumeSections;
	}
	
	/**
	 * Method to write XML Document object received to a file.
	 * 
	 * @param doc (Document object)
	 * @param fileName (File object)
	 */
	private static void outputXMLFile(Document doc, String fileName){
		
		XMLOutputter outputter = new XMLOutputter();
	    try {
	      outputter.output(doc, new BufferedWriter(new FileWriter(exportDirectory.getAbsoluteFile() + System.getProperty("file.separator") + fileName + ".xml")));       
	    }
	    catch (IOException e) {
	      System.err.println(e);
	    }
	}
}
