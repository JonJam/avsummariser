package uk.ecs.gdp.avsummariser.view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ecs.gdp.avsummariser.controller.SummaryTabsController;

/**
 * JPanel class for summary character information tab.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SummaryCharacterDetailPanel extends JPanel{
	
	private JScrollPane scrollPane = null;
	private SummaryTabsController controller;
	
	public SummaryCharacterDetailPanel(SummaryTabsController controller){
		this.controller = controller;
	}

	/**
	 * Method to update the content of the tab.
	 */
	public void update(){
		
		if(scrollPane != null){
			//If not null then remove previous content
			this.remove(scrollPane);
			this.repaint();
		}
		
		//Get new content
		scrollPane = controller.getCharacterDetailContent(this);
		
		//Update display
		this.add(scrollPane);
		this.repaint();
	}
}
