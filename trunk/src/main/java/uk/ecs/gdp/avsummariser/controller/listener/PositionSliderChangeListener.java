package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSlider;

import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Mouse listener class to control video position from slider position.
 * 
 * @author Michael Harris
 * @version 1
 */
public class PositionSliderChangeListener implements MouseListener {
	
	private AVSummaryView view;
	private JSlider positionSlider;

	public PositionSliderChangeListener(JSlider positionSlider, AVSummaryView view) {
		this.positionSlider = positionSlider;
		this.view = view;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {
		//Seek to time in video represented by slider position.
		view.seekInVideo(positionSlider.getValue());
	}
}
