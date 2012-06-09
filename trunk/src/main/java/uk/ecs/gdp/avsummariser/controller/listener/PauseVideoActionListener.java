package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Action listener class to pause currently playing video.
 * 
 * @author Michael Harris
 * @version 1
 */
public class PauseVideoActionListener implements ActionListener {
	
	private AVSummaryView view;

	public PauseVideoActionListener(AVSummaryView view) {
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		//Pause video playing.
		view.pauseVideo();
	}
}
