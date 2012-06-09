package uk.ecs.gdp.avsummariser.model.subtitles;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

/**
 * Class to parse a DVB subtitles file into Subtitle Objects.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SubtitleParser {

	//Root element type of DVB subtitles file.
	private static final String ROOT_ELEMENT = "tt";
	//Root element namespace of DVB subtitles file.
	private static final String ROOT_NS = "http://www.w3.org/2006/10/ttaf1";
	//Namespace for colour attribute used in spans in DVB subtitles file.
	private static final String COLOUR_NS = "http://www.w3.org/2006/10/ttaf1#style";
	
	/** 
	 * Method to parse a correct format XML file into Subtitle Objects, otherwise returns null;
	 * 
	 * @param subtitleFile (File object containing Subtitles file)
	 * @return ArrayList of Subtitle Objects
	 */
	public static ArrayList<Subtitle> parseSubtitleFile(File subtitleFile){
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		ElementFilter pFilter = new ElementFilter("p");
		ElementFilter spanFilter = new ElementFilter("span");
		ArrayList<Subtitle> subtitles = new ArrayList<Subtitle>();

		try {
			doc = builder.build(subtitleFile);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element root = doc.getRootElement();

		if(root.getName().compareTo(ROOT_ELEMENT) == 0 && root.getNamespaceURI().compareTo(ROOT_NS) == 0){
			//Correct XML file format.
			
			@SuppressWarnings("unchecked")
			Iterator<Element> pElements = doc.getDescendants(pFilter);

			while (pElements.hasNext()) {
				//While paragraph elements to process.
				
				Element currentP = (Element) pElements.next();
				
				//Get begin and end attribute values
				String beginTime = currentP.getAttributeValue("begin");
				String endTime = currentP.getAttributeValue("end");

				@SuppressWarnings("unchecked")
				Iterator<Element> spanElements = currentP.getDescendants(spanFilter);

				//Below combines spans in the same timestamp if the colour (i.e. character speaking is the same).
				String previousColour = "";
				String previousSpeech = "";
				while (spanElements.hasNext()) {
					Element currentSpan = (Element) spanElements.next();
					Subtitle subtitle;

					String colour = currentSpan.getAttributeValue("color",Namespace.getNamespace(COLOUR_NS));
					String speech = currentSpan.getTextTrim();

					if(previousColour.equals("")){
						//First span processed.
						previousColour = colour;
						previousSpeech = speech;
						
					} else if(previousColour.equals(colour)){
						//Colour of span is same as previous so merge.
						previousSpeech += " " + speech;
					} else{
						//Colour of span is different to previous so create new Subtitle object and reset for new colour.
						
						//Add previous colour subtitle.
						subtitle = new Subtitle(beginTime, endTime, previousColour, previousSpeech);
						subtitles.add(subtitle);
						
						previousColour = colour;
						previousSpeech = speech;
					}
					
					if(spanElements.hasNext() == false){
						//No next span so create new Subtitle object.
						subtitle = new Subtitle(beginTime, endTime, previousColour, previousSpeech);
						subtitles.add(subtitle);
						previousColour = "";
						previousSpeech = "";
					} 
									
				}
			}
			
			return subtitles;
		} else{
			//Incorrect XML file format.
			
			return null;
		}
	}
}
