package uk.ecs.gdp.avsummariser.view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ecs.gdp.avsummariser.controller.VideoTabsController;

/**
 * JPanel class for subtitle detail tab.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SubtitleDetailPanel extends JPanel {
	
	private JScrollPane scrollPane = null;
	private VideoTabsController controller;
	
	public SubtitleDetailPanel(VideoTabsController controller){
		this.controller = controller;
	}
	
	/**
	 * Method to update the content of the tab.
	 */
	public void update(){
		
		if(scrollPane != null){
			//If not null then remove previous content.
			this.remove(scrollPane);
			this.updateUI();
		}
		
		//Get new content
		scrollPane = controller.getSubtitlesContent(this);
		
		//Update display
		this.add(scrollPane);
		this.updateUI();
	}
}
