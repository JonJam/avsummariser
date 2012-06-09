package uk.ecs.gdp.avsummariser.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import uk.ecs.gdp.avsummariser.controller.listener.SeekToPointInVideoListener;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.section.Section;
import uk.ecs.gdp.avsummariser.model.subtitles.Subtitle;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.LocationNamesMentionedDetailPanel;
import uk.ecs.gdp.avsummariser.view.PersonNamesMentionedDetailPanel;
import uk.ecs.gdp.avsummariser.view.SubtitleDetailPanel;

/**
 * Class to control tabs for video.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class VideoTabsController {

	private AVSummaryView view;
	private AVSummaryModel model;
	
	public VideoTabsController(AVSummaryModel model){
		this.model = model;
	}
	
	/**
	 * Method to register view object with the controller.
	 * 
	 * @param view (AVSummaryView object)
	 */
	public void registerView(AVSummaryView view){
		this.view = view;
	}
	
	/**
	 * Method to get new content for Subtitles tab.
	 * 
	 * @param panel (SubtitleDetailPanel object)
	 * @return JScrollPane object
	 */
	public JScrollPane getSubtitlesContent(SubtitleDetailPanel panel){
		
		//Get ArrayList of Subtitle objects for current video.
		ArrayList<Subtitle> subtitles = model.getSelectedVideo().getSubtitles();
		
		//Create new 
		JTextPane area = new JTextPane();
		area.setMargin(new Insets(10,10,10,30));
		area.setEditable(false);
		
		StyledDocument doc = (StyledDocument)area.getDocument();
        
		//Loop to insert String into pane for each Subtitle object
		for(Subtitle s : subtitles){
			try {
				 Style boldStyle = doc.addStyle("boldStyle", null);
				 StyleConstants.setBold(boldStyle, true);
				 doc.insertString(doc.getLength(), "Begin: " + s.getBeginTime() + " End: " + s.getEndTime() + "\n", boldStyle);
				 
				 Style speechStyle = doc.addStyle("speechStyle", null);
				 Color speechColour = Color.decode(s.getColour());
				 speechColour = speechColour.darker();
				 StyleConstants.setForeground(speechStyle, speechColour);
				 doc.insertString(doc.getLength(), s.getSpeech() + "\n", speechStyle);
				 
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
        
		//Create new scroll pane object
		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(),panel.getHeight()));
		
		return scrollPane;
	}
	
	/**
	 * Method to get new content for People Mentioned tab.
	 * 
	 * @param panel (PersonNamesMentionedDetailPanel object)
	 * @return JScrollPane object
	 */
	public JScrollPane getPersonMentionedContent(PersonNamesMentionedDetailPanel panel){
		
		//Get Map for people names mentioned for current video.
		Map<String,ArrayList<Section>> personNamesMentioned = model.getSelectedVideo().getPeopleMentioned();
	
		//Create text pane with content 
		JTextPane pane = produceTextPaneWithContent(personNamesMentioned);
        
		//Create new scroll pane
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(),panel.getHeight()));
		
		return scrollPane;
	}
	
	/**
	 * Method to get new content for Location Mentioned tab.
	 * 
	 * @param panel (LocationNamesMentionedDetailPanel object)
	 * @return JScrollPane object)
	 */
	public JScrollPane getLocationMentionedContent(LocationNamesMentionedDetailPanel panel){
		
		//Get current video's location mentioned map
		Map<String,ArrayList<Section>> locationsMentioned = model.getSelectedVideo().getLocationsMentioned();
		
		//Create text pane with content
		JTextPane pane = produceTextPaneWithContent(locationsMentioned);
        
		//Create new scroll pane
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(),panel.getHeight()));
		
		return scrollPane;
	}
	
	/**
	 * Method to produce a JTextPane object with content from a Map.
	 * 
	 * @param mentioned (Map of String object to ArrayList of Section objects)
	 * @return JTextPane object
	 */
	private JTextPane produceTextPaneWithContent(Map<String,ArrayList<Section>> mentioned){
		
		//Create new text pane
		JTextPane pane = new JTextPane();
		pane.setMargin(new Insets(10,10,10,30));
		pane.setEditable(false);
		
		StyledDocument doc = (StyledDocument)pane.getDocument();
        
		//Loop to insert string for each key
		for(String k : mentioned.keySet()){
			try {
				 Style nameStyle = doc.addStyle("timeFrameStyle", null);
				 StyleConstants.setBold(nameStyle, true);
				 doc.insertString(doc.getLength(),k + "\n", nameStyle);
				 
				 doc.insertString(doc.getLength(),"\tMentioned between: \n", null);
				 for(Section s : mentioned.get(k)){
					 insertVideoTimeStamp(doc, pane, s);
				 }
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		return pane;
	}
	
	/**
	 * Method to insert a video time stamp String into text pane for each Section object)
	 * 
	 * @param doc (Document object)
 	 * @param pane (JTextPane object)
	 * @param s (Section object)
	 */
	private void insertVideoTimeStamp(Document doc, JTextPane pane, Section s){
		try{
			doc.insertString(doc.getLength(), "\t\tBegin: " + s.getStartTimeString() + " End: " + s.getEndTimeString(), null);
			
			//Adding links to seek to times in video.
			Style labelStyle = pane.getStyle(StyleContext.DEFAULT_STYLE);
			JLabel skipToLink = new JLabel("Play");
			skipToLink.addMouseListener(new SeekToPointInVideoListener(view, s.getVideo(), s.getStartTime()));
			StyleConstants.setComponent(labelStyle, skipToLink);
			doc.insertString(doc.getLength(), "Ignored", labelStyle);
			
			//Insert new line character
			doc.insertString(doc.getLength(), "\n", null);
		} catch(BadLocationException e){
			e.printStackTrace();
		}
	}
}
