package uk.ecs.gdp.avsummariser.view.videobrowser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * MouseAdapter class for the VideoBrowserItem right click menu.
 * 
 * @author Michael Harris
 * @version 1
 */
public class RightClickPopupListener extends MouseAdapter {
	
	private VideoBrowserItem videoBrowserItem;
	private AVSummaryModel model;
	private AVSummaryView view;

	public RightClickPopupListener(VideoBrowserItem videoBrowserItem, AVSummaryModel model, AVSummaryView view) {
		this.videoBrowserItem = videoBrowserItem;
		this.model = model;
		this.view = view;
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()){
			doPop(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()){
			doPop(e);
		}
	}

	/**
	 * Method to create right click menu on selected video.
	 * 
	 * @param e (MouseEvent object)
	 */
	private void doPop(MouseEvent e) {
		boolean selected = false;
		
		if (model.getSelectedVideo() == videoBrowserItem.getvFile()) {
			selected = true;
			RightClickMenu menu = new RightClickMenu(view, videoBrowserItem, model, selected);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}