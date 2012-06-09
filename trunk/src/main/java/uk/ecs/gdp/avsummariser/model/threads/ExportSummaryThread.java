package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.summary.ExportSummary;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Thread class to export a Summary object.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class ExportSummaryThread  extends Thread{
	
	private AVSummaryModel model;
	private AVSummaryView view;
	
	public ExportSummaryThread(AVSummaryModel model, AVSummaryView view){
		this.model = model;
		this.view = view;
	}
	
	public void run() {
		view.displayOutcome("Exporting summary...");
		
		//Call export method
		ExportSummary.exportSummary(view, model);
		
		view.displayOutcome("Exporting summary complete.");
		
		//Enable buttons now Summary object generated.
		view.getFrame().getGenerateSummaryButton().setEnabled(true);
		view.getFrame().getPlaySummaryButton().setEnabled(true);
		view.getFrame().getExportSummaryButton().setEnabled(true);
	}
}
