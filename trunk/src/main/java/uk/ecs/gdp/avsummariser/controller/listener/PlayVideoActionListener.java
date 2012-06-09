package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Action listener class to play currently selected video.
 * 
 * @author Michael Harris
 * @version 1
 */
public class PlayVideoActionListener implements ActionListener {
	
	private AVSummaryView view;
	private VideoBrowserItem videoBrowserItem;
	
	public PlayVideoActionListener(AVSummaryView view, VideoBrowserItem videoBrowserItem){
		this.view = view;
		this.videoBrowserItem = videoBrowserItem;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		try {
			//Play video selected
			view.playVideo(AVSummaryModel.getVideoFromPath(videoBrowserItem.getvFile().getAbsolutePath()));
			
		} catch (FileNotPlayableException e1) {
			e1.printStackTrace();
		}
	}
}
