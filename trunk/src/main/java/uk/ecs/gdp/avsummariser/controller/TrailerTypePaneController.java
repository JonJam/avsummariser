package uk.ecs.gdp.avsummariser.controller;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;

/**
 * Controller for trailer type pane.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class TrailerTypePaneController {

	private AVSummaryModel model;
	
	public TrailerTypePaneController(AVSummaryModel model){
		this.model = model;
	}
	
	/**
	 * Method to set trailer type in model according to user input.
	 * 
	 * @param type (String object)
	 */
	public void setTrailerType(String type){
		
		model.setTrailerTypeSelected(type);
	}
}
