package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.subtitles.RunNameFinder;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Thread class to find person names mentioned in a subtitle file for a video.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class PersonNameFinderThread extends Thread{

	private AVSummaryModel model;
	private AVSummaryView view;
	private VideoFile video;
	
	public PersonNameFinderThread(AVSummaryModel model, AVSummaryView view, VideoFile video){
		this.model = model;
		this.view = view;
		this.video = video;
	}
	
	public void run() {
		
		//Checking subtitle parsers not in use already
		while(RunNameFinder.isInUse()){
			//If so then sleep
			try {
				//Sleep for a 1 second.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(model.getChosenSeries() == null && model.getSeriesActors() == null){
			//Series not chosen so don't use TVDB information.
			RunNameFinder.runNameFinder(video, true, false, null, null);
		} else{
			//Series been chosen from TVDB.
			RunNameFinder.runNameFinder(video, true, true, model.getSeriesActors(), model.getGenreSelected());
		}
		
		//Setting boolean in video file so it has been run
		video.setPersonNameFinderFinished();

		if(view != null){
			//If not null then update GUI for video.
			view.getFrame().getPersonNamesDetailPanel().update();
		}		
	}
}
