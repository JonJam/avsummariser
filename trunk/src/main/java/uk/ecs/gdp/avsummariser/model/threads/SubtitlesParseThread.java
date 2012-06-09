package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.subtitles.RunSubtitleParser;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Thread class to run subtitle parser subtitles
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SubtitlesParseThread extends Thread{

	private VideoBrowserItem item;
	private AVSummaryModel model;
	private AVSummaryView view;
	
	public SubtitlesParseThread(AVSummaryView view, AVSummaryModel model, VideoBrowserItem item){
		this.view = view;
		this.item = item;
		this.model = model;
	}
	
	public void run() {
		
		view.displayOutcome("Loading subtitle file...");
		
		RunSubtitleParser.runSubtitleParser(view, model, item);
	}
}