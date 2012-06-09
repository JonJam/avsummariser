package uk.ecs.gdp.avsummariser.view.videobrowser;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import uk.ecs.gdp.avsummariser.controller.listener.DataViewActionListener;
import uk.ecs.gdp.avsummariser.controller.listener.PlayVideoActionListener;
import uk.ecs.gdp.avsummariser.controller.listener.RemoveVideoActionListener;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * JPopupMenu class for right click menu on each VideoBrowserItem
 * 
 * @author Michael Harris
 * @version 1
 */
public class RightClickMenu extends JPopupMenu {
	
	private JMenuItem playItem;
	private JMenuItem removeItem;
	private JMenuItem dataItem;

	public RightClickMenu(final AVSummaryView view, final VideoBrowserItem videoBrowserItem, final AVSummaryModel model, boolean selected) {

		playItem = new JMenuItem("Play");
		removeItem = new JMenuItem("Remove");
		dataItem = new JMenuItem("Show Data");
		
		playItem.addActionListener(new PlayVideoActionListener(view, videoBrowserItem));
		removeItem.addActionListener(new RemoveVideoActionListener(model, view,videoBrowserItem));
		dataItem.addActionListener(new DataViewActionListener(videoBrowserItem));

		this.add(playItem);
		this.add(removeItem);
		this.add(dataItem);
	}
}
