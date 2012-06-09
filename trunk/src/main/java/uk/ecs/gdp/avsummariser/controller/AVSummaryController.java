package uk.ecs.gdp.avsummariser.controller;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * The main controller class for the project.
 * 
 * @author Michael Harris
 * @author Jonathan Harrison
 * @version 1
 */
public class AVSummaryController {
	
	private AVSummaryView view;
	private AVSummaryModel model;
	
	private SummaryTabsController summaryTabsController;
	private VideoTabsController videoTabsController;
	private TrailerTypePaneController trailerTypePaneController;

	public AVSummaryController(AVSummaryModel model) {
		this.model = model;
		
		videoTabsController = new VideoTabsController(model);
		
		summaryTabsController = new SummaryTabsController(model);
		trailerTypePaneController = new TrailerTypePaneController(model);
	}
	
	/**
	 * Method to register view with all controllers.
	 * 
	 * @param view (AVSummaryView object)
	 */
	public void registerView(AVSummaryView view){
		this.view = view;
		
		videoTabsController.registerView(view);
		
		summaryTabsController.registerView(view);
	}
	
	/**
	 * Method to get SummaryTabsController.
	 * 
	 * @return SummaryTabsController object
	 */
	public SummaryTabsController getSummaryTabsController() {
		return summaryTabsController;
	}
		
	/**
	 * Method to get VideoTabsController
	 * 
	 * @return VideoTabsController object
	 */
	public VideoTabsController getVideoTabsController(){
		return videoTabsController;
	}
	
	/**
	 * Method to get TrailerTypePaneController
	 * 
	 * @return TrailerTypePaneController object
	 */
	public TrailerTypePaneController getTrailerTypePaneController(){
		return trailerTypePaneController;
	}
}

