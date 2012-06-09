package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Mouse listener called when video selected to load all content and if necessary run modules.
 * 
 * @author Michael Harris
 * @author Jonathan Harrison
 * @version 1
 */
public class SelectVideoActionListener implements MouseListener {
	
	private AVSummaryView view;
	private AVSummaryModel model;
	private VideoBrowserItem videoBrowserItem;

	public SelectVideoActionListener(AVSummaryView view, AVSummaryModel model, VideoBrowserItem videoBrowserItem) {
		this.view = view;
		this.model = model;
		this.videoBrowserItem = videoBrowserItem;
	}

	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mousePressed(MouseEvent arg0){}
	public void mouseReleased(MouseEvent arg0){}
	
	public void mouseClicked(MouseEvent arg0) {
		//Get selected video.
		VideoFile videoFile = videoBrowserItem.getvFile();

		if (!videoBrowserItem.isSelected()) {
			//New video selected
			
			//Deselect all other videos.
			view.getFrame().getVideoBrowser().deselectAll();
			
			//Set selected video in model.
			model.setSelectedVideo(videoFile);
			
			//Select video in video.
			videoBrowserItem.select();
			
			
			onVideoItemSelection();
		}
	}
	
	/**
	 * Method called when a new video is selected in the video browser. Populates tabs
	 * and runs modules if necessary.
	 * 
	 * @param browserItem (VideoBrowserItem object)
	 */
	private void onVideoItemSelection() {
		//Get current video
		VideoFile video = videoBrowserItem.getvFile();
		
		if (videoBrowserItem.getvFile().getSubtitleFilePath() != null) {
			//Subtitles been chosen.
			
			//Display subtitles in panel
			view.getFrame().getSubtitlePanel().update();
			
			//Display names mentioned in file and runs name parsing if need to.
			model.runPeopleFinder(view, video);
			
			//Display locations mentioned in file and runs location finding if necessary
			model.runLocationFinder(view, video);
		}
		
		//Run shot detection
		try {
			ShotDetectedListenerImpl shotDetectedListener = model.runShotDetection(video);
			shotDetectedListener.setShotDetailPanel(view.getFrame().getShotPanel(video));
		} catch (FileNotPlayableException e) {
			e.printStackTrace();
		}
		
		//Run volume detection
		model.runVolumeDetector(video);
		
//		Run frequency detection
//		model.runFrequencyDetector(video);
		
		view.displayOutcome("Video selected.");
	}

	
}
