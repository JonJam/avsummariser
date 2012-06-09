package uk.ecs.gdp.avsummariser.view;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import uk.ecs.gdp.avsummariser.controller.listener.PauseVideoActionListener;
import uk.ecs.gdp.avsummariser.controller.listener.PositionSliderChangeListener;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;

/**
 * JPanel class for video player panel.
 * 
 * @author Michael Harris
 * @version 1
 */
public class VideoPlayerPanel extends JPanel {
	
	private JSlider positionSlider;
	private JButton pause;
	private JPanel videoDock;
	private JPanel buttonContainer;

	public VideoPlayerPanel(final AVSummaryModel model, final AVSummaryView view) {
		
		this.setLayout(new BorderLayout());
		
		videoDock = new JPanel();
		this.add(videoDock, BorderLayout.CENTER);
		
		buttonContainer = new JPanel();
		
		positionSlider = new JSlider();
		positionSlider.setValue(0);
		
		pause = new JButton("Pause");
		
		buttonContainer.add(pause);
		buttonContainer.add(positionSlider);
		
		this.add(buttonContainer, BorderLayout.SOUTH);

		positionSlider.addMouseListener(new PositionSliderChangeListener(positionSlider, view));
		pause.addActionListener(new PauseVideoActionListener(view));			
	}
	
	/**
	 * Method to get video dock JPanel object.
	 * 
	 * @return JPanel object
	 */
	public JPanel getVideoDock() {
		return videoDock;
	}
	
	/**
	 * Method to get video position slider object.
	 * 
	 * @return JSlider object
	 */
	public JSlider getPositionSlider(){
		return positionSlider;
	}
}
