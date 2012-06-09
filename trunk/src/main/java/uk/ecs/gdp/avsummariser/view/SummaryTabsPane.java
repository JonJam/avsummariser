package uk.ecs.gdp.avsummariser.view;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import uk.ecs.gdp.avsummariser.controller.AVSummaryController;

/**
 * JTabbedPane class for summary tabs pane.
 * 
 * @author JonJam
 * @version 1
 */
public class SummaryTabsPane extends JTabbedPane {
	
	private SummarySeriesDetailPanel seriesDetailPanel;
	private SummaryCharacterDetailPanel characterDetailPanel;
	private SummaryLocationsDetailPanel locationsDetailPanel;
	
	public SummaryTabsPane(AVSummaryController controller){
		
		seriesDetailPanel = new SummarySeriesDetailPanel(controller.getSummaryTabsController());
		this.add("Overview", seriesDetailPanel);
		
		characterDetailPanel = new SummaryCharacterDetailPanel(controller.getSummaryTabsController());
		this.add("Characters", characterDetailPanel);
		
		locationsDetailPanel = new SummaryLocationsDetailPanel(controller.getSummaryTabsController());
		this.add("Locations", locationsDetailPanel);
	}
	
	/**
	 * Method to get SummarySeriesDetailPanel object
	 * 
	 * @return SummarySeriesDetailPanel object
	 */
	public SummarySeriesDetailPanel getSummarySeriesDetailPanel(){
		return seriesDetailPanel;
	}
	
	/**
	 * Method to get SummaryCharacterDetailPanel object
	 * 
	 * @return SummaryCharacterDetailPanel object
	 */
	public SummaryCharacterDetailPanel getSummaryCharacterDetailPanel(){
		return characterDetailPanel;
	}
	
	/**
	 * Method to get SummaryLocationsDetailPanel object
	 * 
	 * @return SummaryLocationsDetailPanel object
	 */
	public SummaryLocationsDetailPanel getSummaryLocationsDetailPanel(){
		return locationsDetailPanel;
	}
}
