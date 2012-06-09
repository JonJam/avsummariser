package uk.ecs.gdp.avsummariser;

import uk.ecs.gdp.avsummariser.controller.AVSummaryController;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Class to run AVSummariser
 * 
 * @author Jonathan Harrison
 * @author Michael Harris
 */
public class Start {

	public static void main(String[] args) {

		//Set up MVC components
		AVSummaryModel model = new AVSummaryModel();
		AVSummaryController controller = new AVSummaryController(model);
		AVSummaryView view = new AVSummaryView(model,controller);
		
		controller.registerView(view);
	}
}
