package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.tvdb.SeriesSearch;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Thread class to run series search
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SeriesSearchThread extends Thread{

	private AVSummaryView view;
	private AVSummaryModel model;	

	public SeriesSearchThread(AVSummaryView view, AVSummaryModel model){
		this.view = view;
		this.model = model;
	}
	
	public void run() {
		//Begin search
		view.displayOutcome("Searching...");
		
		//Perform series search
		SeriesSearch.searchSeries(view,model);
	}
}
