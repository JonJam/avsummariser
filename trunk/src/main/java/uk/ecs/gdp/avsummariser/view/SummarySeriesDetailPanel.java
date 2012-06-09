package uk.ecs.gdp.avsummariser.view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ecs.gdp.avsummariser.controller.SummaryTabsController;

/**
 * JPanel class for summary series information tab.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SummarySeriesDetailPanel extends JPanel{
	
	private JScrollPane scrollPane = null;
	private SummaryTabsController controller;
	
	public SummarySeriesDetailPanel(SummaryTabsController controller){
		this.controller = controller;
	}

	/**
	 * Method to update content of the tab.
	 */
	public void update(){
		
		if(scrollPane != null){
			//If not null then remove previous content.
			this.remove(scrollPane);
			this.repaint();
		}
		
		//Get new content.
		scrollPane = controller.getSeriesDetailContent(this);
		
		//Update display
		this.add(scrollPane);
		this.repaint();
	}
}
