package uk.ecs.gdp.avsummariser.model.tvdb;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

/**
 * Class to perform series search using TVDBSearcher which updates the view and model as appropriate.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SeriesSearch {

	private static AVSummaryView view;
	private static AVSummaryModel model;
	private static JTextField searchTf;
	private static JButton searchButton;
	
	/**
	 * Method to perform series search and alter view accordingly
	 * 
	 * @param v (AVSummaryView object)
	 * @param m (AVSummaryModel object)
	 */
	public static void searchSeries(AVSummaryView v, AVSummaryModel m){
		view = v;
		model = m;
		
		searchTf = view.getFrame().getSeriesSearchPanel().getSearchTerms();
		searchButton = view.getFrame().getSeriesSearchPanel().getSearchButton();
		
		String searchText = searchTf.getText();
		
		if(!(searchText.equals("") || searchText.equals("Enter name of series..."))){
			//Search terms not empty or default string
			
			//Get results for search terms
			List<Series> results = TVDBSearcher.getAllSeries(searchText);
			
			if(results.isEmpty()){
				//No results
			
				searchTf.setEnabled(true);
				searchButton.setEnabled(true);
				
				view.displayOutcome("No results found.");
			} else{
				//Results isn't empty

				//Convert results so can be displayed in message box
				HashMap<String,String>seriesMap = new HashMap<String,String>();
				for(Series series: results){
					seriesMap.put(series.getSeriesName(),series.getId());
				}
				Object[] names = seriesMap.keySet().toArray();
				Arrays.sort(names);
				
				view.displayOutcome("Results found...");
				
				//Display message box
				String s = (String) JOptionPane.showInputDialog(view.getFrame(), "Please select a series:", "Search Results" , JOptionPane.PLAIN_MESSAGE, null, names,names[0]);
				
				if ((s != null) && (s.length() > 0)) {
					//Series chosen
					
					view.displayOutcome("Processing chosen series...");
					
					//Get Series object for chosen series String.
					chosenSeries(seriesMap.get(s));
				} else{
					//Cancelled search
					
					view.displayOutcome("Series search cancelled.");
					
					searchTf.setEnabled(true);
					searchButton.setEnabled(true);
				}	
			}
		} else{
			//No search terms entered so display messages and exit
			
			view.displayOutcome("No search terms entered.");
			
			searchTf.setEnabled(true);
			searchButton.setEnabled(true);
		}
	}
	
	/**
	 * Populates Overview tab with information from Series Object as well as set data in the model.
	 * 
	 * @param seriesId (String object)
	 */
	private static void chosenSeries(String seriesId){
		
		//Get objects from TVDB.
		Series seriesChosen = TVDBSearcher.getChosenSeries(seriesId);
		List<Actor> seriesActors = TVDBSearcher.getSeriesActors(seriesId);
		
		//Set model objects
		model.setChosenSeries(seriesChosen);
		model.setSeriesActors(seriesActors);
		
		//Get view genre dropdown
		JComboBox genreComboBox = view.getFrame().getGenreComboBox();
		
		//Remove all current items
		genreComboBox.removeAllItems();
		
		//Add new items to genre dropdown
		for(String genre : seriesChosen.getGenres()){
			genreComboBox.addItem(genre);
		}
		
		//Set current genre selected in model.
		model.setGenreSelected(seriesChosen.getGenres().get(0));
		
		//Add action listener to genre dropdown.
		genreComboBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				ItemSelectable is = (ItemSelectable) e.getSource();
				Object selected[] = is.getSelectedObjects();
				model.setGenreSelected(((selected.length == 0) ? "" : (String)selected[0]));
			}
		});
		
		//Enable and repaint
		genreComboBox.setEnabled(true);
		genreComboBox.repaint();
		
		//Display outcome
		view.displayOutcome("Series information loaded.");
		
		//Enable buttons
		searchTf.setEnabled(true);
		searchButton.setEnabled(true);
	}
	
}
