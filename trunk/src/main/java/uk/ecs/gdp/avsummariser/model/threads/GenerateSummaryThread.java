package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.summary.GenerateSummary;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Thread class to generate a Summary object for all video and subtitle files loaded.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class GenerateSummaryThread extends Thread{

	private AVSummaryModel model;
	private AVSummaryView view;
	
	public GenerateSummaryThread(AVSummaryModel model, AVSummaryView view){
		this.model = model;
		this.view = view;
	}
	
	public void run() {
		view.displayOutcome("Generating summary...");
		
		//Call summarise method
		GenerateSummary.summarise(model);
		
		//Update summary tabs
		view.getFrame().getSummarySeriesDetailPanel().update();
		view.getFrame().getSummaryCharactersDetailPanel().update();
		view.getFrame().getSummaryLocationsDetailPanel().update();
		
		view.displayOutcome("Summary generation complete.");
		
		//Enable buttons now that Summary object generated.
		view.getFrame().getGenerateSummaryButton().setEnabled(true);
		view.getFrame().getPlaySummaryButton().setEnabled(true);
		view.getFrame().getExportSummaryButton().setEnabled(true);
	}
}
