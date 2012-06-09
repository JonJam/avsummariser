package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.subtitles.RunNameFinder;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Thread class to find location mentioned in a subtitle file for a video.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class LocationNameFinderThread extends Thread{

	private AVSummaryView view;
	private VideoFile video;
	
	public LocationNameFinderThread(AVSummaryView view, VideoFile video){
		this.view = view;
		this.video = video;
	}
	
	public void run() {		
		
		//Checking subtitle parsers not in use already
		while(RunNameFinder.isInUse()){
			//If so then sleep
			try {
				//Sleep for 1 second.
				Thread.sleep(1000); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Run location name finder.
		RunNameFinder.runNameFinder(video, false, false, null, null);
		
		//Setting boolean in video file so it has been run
		video.setLocationNameFinderFinished();
		
		if(view != null){
			//If not null then update GUI for video.
			view.getFrame().getLocationNamesDetailPanel().update();
		}
	}
}
