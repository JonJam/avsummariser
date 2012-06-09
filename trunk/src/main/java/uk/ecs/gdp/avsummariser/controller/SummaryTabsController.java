package uk.ecs.gdp.avsummariser.controller;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.*;

import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

import uk.ecs.gdp.avsummariser.controller.listener.SeekToPointInVideoListener;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.section.Section;
import uk.ecs.gdp.avsummariser.model.summary.Summary;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.SummaryCharacterDetailPanel;
import uk.ecs.gdp.avsummariser.view.SummaryLocationsDetailPanel;
import uk.ecs.gdp.avsummariser.view.SummarySeriesDetailPanel;

/**
 * Class to produce new content for Summary tabs.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SummaryTabsController {
	
	private AVSummaryView view;
	private AVSummaryModel model;
	
	public SummaryTabsController(AVSummaryModel model){
		this.model = model;
	}
	
	/**
	 * Method to register view object with controller.
	 * 
	 * @param view (AVSummaryView view)
	 */
	public void registerView(AVSummaryView view){
		this.view = view;
	}
	
	/**
	 * Method to create a new JScrollPane object which contains the Series data to update the Overview tab.
	 * 
	 * @param panel (SummarySeriesDetailPanel object)
	 * @return JScrollPane object
	 */
	public JScrollPane getSeriesDetailContent(SummarySeriesDetailPanel panel){
		
		//Get summary object
		Summary summary = model.getSummary();
		//Get Series object from summary
		final Series seriesChosen = summary.getSeries();
		
		//Create new text pane
		JTextPane pane = new JTextPane();
		pane.setMargin(new Insets(10,10,10,30));
		pane.setEditable(false);
			
		if(seriesChosen != null){
			//Series object set so add content
		
			StyledDocument doc = (StyledDocument)pane.getDocument();
			Style labelStyle = pane.getStyle(StyleContext.DEFAULT_STYLE);
	        
			try {			
				Style boldStyle = doc.addStyle("boldStyle", null);
				StyleConstants.setBold(boldStyle, true);
				
				//Insert series name
				doc.insertString(doc.getLength(), seriesChosen.getSeriesName() + "\n\n", boldStyle);
				
				//Insert series overview
				doc.insertString(doc.getLength(), seriesChosen.getOverview() + "\n\n", null);
				  
				//Insert link to IMDB profile
				JLabel imdbLink = new JLabel("IMDB Link");
				StyleConstants.setComponent(labelStyle, imdbLink);
				doc.insertString(doc.getLength(), "Ignored", labelStyle);
				imdbLink.addMouseListener(new MouseListener() {
					
					public void mouseReleased(MouseEvent arg0) {}
					public void mousePressed(MouseEvent arg0) {}
					public void mouseExited(MouseEvent arg0) {}
					public void mouseEntered(MouseEvent arg0) {}
					
					public void mouseClicked(MouseEvent arg0) {
						Desktop desktop = Desktop.getDesktop();
						try {
							desktop.browse(new URI("http://www.imdb.com/title/" + seriesChosen.getImdbId()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				doc.insertString(doc.getLength(), "\n\n", null);
				 
				//Insert link to TVDB profile
				JLabel tvdbLink = new JLabel("TVDB Link");
				StyleConstants.setComponent(labelStyle, tvdbLink);
				doc.insertString(doc.getLength(), "Ignored", labelStyle);
				tvdbLink.addMouseListener(new MouseListener() {
					
					public void mouseReleased(MouseEvent arg0) {}
					public void mousePressed(MouseEvent arg0) {}
					public void mouseExited(MouseEvent arg0) {}
					public void mouseEntered(MouseEvent arg0) {}
					
					public void mouseClicked(MouseEvent arg0) {
						Desktop desktop = Desktop.getDesktop();
						try {
							desktop.browse(new URI("http://thetvdb.com/?tab=series&id=" + seriesChosen.getId()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				doc.insertString(doc.getLength(), "\n\n", null); 
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		//Create new scroll pane
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(),panel.getHeight()));

		return scrollPane;
	}

	/**
	 * Method to create a new JScrollPane object which contains Character information to update the People tab.
	 * 
	 * TODO ADD FACE DETECTION STUFF HERE
	 * 
	 * @param panel (SummaryCharacterDetailPanel panel)
	 * @return JScrollPane object
	 */
	public JScrollPane getCharacterDetailContent(SummaryCharacterDetailPanel panel){
		
		//Get summary object
		Summary summary = model.getSummary();
		
		//Get list of Actor objects.
		List<Actor> actors = summary.getActors();
		
		//Get genre used in summary production.
		String genre = summary.getGenre();
		
		//Get merged people mentioned map from summary.
		Map<String,ArrayList<Section>> peopleMentioned = summary.getMergedPeopleMentioned();
		
		//Create text pane for content
		JTextPane pane = new JTextPane();
		pane.setMargin(new Insets(10,10,10,30));
		pane.setEditable(false);
		
		StyledDocument doc = (StyledDocument)pane.getDocument();
		Style boldStyle = pane.addStyle("boldStyle", null);
		
		StyleConstants.setBold(boldStyle, true);
		
		try {
			if(actors != null){
				//Actor object is not null using TVDB.
				
				//Loop through actors and add content
				for(Actor actor : actors){
					 
					String name = actor.getName();
			 		String role = actor.getRole();
					 
					if(role.contains("|")){
						//Multiple roles contained in role String. Split on |
						
						String[] temp = role.split("|");
						for(String r : temp){
							insertContent(pane, r, name, true, boldStyle, peopleMentioned.get(r));
						}
					} else if(role.contains("/")){
						//Multiple roles contained in role String. Split on /
						
						String[] temp = role.split("/");
						for(String r : temp){
							insertContent(pane, r, name, true, boldStyle, peopleMentioned.get(r));
						}
					} else{
						//Not multiple roles
						
						if(genre != null && (genre.equals("Documentary")) || (genre.equals("Game Show")) || (genre.equals("News"))){
							//Genre means have to use name not role
							
							insertContent(pane, role, name, false, boldStyle, peopleMentioned.get(name));
						} else{
							
							insertContent(pane, role, name, true, boldStyle, peopleMentioned.get(role));
						}			 		
					}
					doc.insertString(doc.getLength(),"\n", null); 
				 }
			} else{
				//Actors object is null i.e. not used TVDB.
				
				//Loop through keys and add content
				for(String n : peopleMentioned.keySet()){
			 		insertContent(pane, n, null, false, boldStyle, peopleMentioned.get(n));
			 		doc.insertString(doc.getLength(),"\n", null); 
			 	}
			}
		 	
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		//Create scroll pane to contain text pane
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(),panel.getHeight()));

		return scrollPane;
	}
	
	/**
	 * Method to insert strings into text pane based on parameters passed.
	 * 
	 * @param pane (JTextPane object)
	 * @param role (String object)
	 * @param name (String object)
	 * @param printOutRole (boolean value)
	 * @param boldStyle (Style object)
	 * @param appearances (ArrayList of Section objects)
	 */
	private void insertContent(JTextPane pane, String role, String name, boolean printOutRole, Style boldStyle, ArrayList<Section> appearances){
		StyledDocument doc = (StyledDocument)pane.getDocument();
		
		if(appearances != null){
			//If character makes no appearances then don't print.

			try {
				if(name != null && printOutRole == true){
					//Using TVDB and genre requires use of role names.
					
					doc.insertString(doc.getLength(), role + "\n", boldStyle);
					doc.insertString(doc.getLength(), "Actor: " + name + "\n", boldStyle);
					
				} else if(name != null && printOutRole == false){
					//Using TVDB and Genre using calls for Actor name to be used.
					
					doc.insertString(doc.getLength(), name + "\n", boldStyle);
				} else{
					//Else print out just the name
					
					doc.insertString(doc.getLength(), role + "\n", boldStyle);
				}
				
				//Loop to insert strings for each mention
				for(Section a : appearances){
		 			insertVideoTimeStamp(doc,pane,a);
		 		}
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method to create a new JScrollPane object which contains locations mentioned information to update the Location tab.
	 * 
	 * @param panel (SummaryLocationsDetailPanel object)
	 * @return JScrollPane object
	 */
	public JScrollPane getLocationsDetailContent(SummaryLocationsDetailPanel panel){
		
		//Get summary object
		Summary summary = model.getSummary();
		
		//Get map for locations mentioned
		Map<String,ArrayList<Section>> locationsMentioned = summary.getMergedLocationMentioned();
		
		//Create new JPane
		JTextPane pane = new JTextPane();
		pane.setMargin(new Insets(10,10,10,30));
		pane.setEditable(false);
		
		StyledDocument doc = (StyledDocument)pane.getDocument();
		Style boldStyle = doc.addStyle("boldStyle", null);
		StyleConstants.setBold(boldStyle, true);
        
		try {
			//Loop to insert content into pane for each location
			for(String l : locationsMentioned.keySet()){
				doc.insertString(doc.getLength(), l + "\n", boldStyle); 
		 		
				//Loop to insert content for each mention to the pane
		 		for(Section a : locationsMentioned.get(l)){
		 			insertVideoTimeStamp(doc,pane,a);
		 		}
		 		
		 		doc.insertString(doc.getLength(),"\n", null); 
		 	}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		//Create new scrollPane
		JScrollPane scrollPane = new JScrollPane(pane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(panel.getWidth(),panel.getHeight()));

		return scrollPane;
	}
	
	/**
	 * Method to insert a String into the pane for a mention.
	 * 
	 * @param doc (Document object)
	 * @param pane (JTextPane object)
	 * @param a (Section object)
	 */
	private void insertVideoTimeStamp(Document doc, JTextPane pane, Section a){
		try {
			//Insert video time stamp string
			doc.insertString(doc.getLength(), "\t\tVideo File: " + a.getVideo().getFileName() + " ST: " + a.getStartTimeString() + " ET: " + a.getEndTimeString() + " ", null);
		
			//Adding links to seek to times in video.
			Style labelStyle = pane.getStyle(StyleContext.DEFAULT_STYLE);
			JLabel skipToLink = new JLabel("Play");
			skipToLink.addMouseListener(new SeekToPointInVideoListener(view, a.getVideo(), a.getStartTime()));
			StyleConstants.setComponent(labelStyle, skipToLink);
			doc.insertString(doc.getLength(), "Ignored", labelStyle);
			
			//Insert new line character
			doc.insertString(doc.getLength(), "\n", null);
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
