package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Action listener class to remove select video from system
 * 
 * @author Michael Harris
 * @version 1
 */
public class RemoveVideoActionListener implements ActionListener {
	
	private AVSummaryView view;
	private AVSummaryModel model;
	private VideoBrowserItem videoBrowserItem;

	public RemoveVideoActionListener(AVSummaryModel model, AVSummaryView view, VideoBrowserItem videoBrowserItem) {
		this.view = view;
		this.model = model;
		this.videoBrowserItem = videoBrowserItem;
	}

	public void actionPerformed(ActionEvent e) {
		//Stop video playing if it is.
		view.stopVideoPlayBack();
		
		//Remove video from model.
		model.removeVideoItem(videoBrowserItem.getvFile());
		
		//Refresh video browser list.
		view.refreshVideoList();
	}
}
