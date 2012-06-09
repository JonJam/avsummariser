package uk.ecs.gdp.avsummariser.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * JPanel class for video shot detail tab.
 * 
 * @author Michael Harris
 * @version 1
 */
public class ShotDetailPanel extends JPanel {
	
	private JScrollPane scrollPane;
	private JPanel container;
	private JPanel itemPanel;
	private int shotCount = 0;

	public ShotDetailPanel() {
		container = new JPanel();
		itemPanel = new JPanel();
		scrollPane = new JScrollPane(itemPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		itemPanel.setLayout(new GridLayout(0, 1));
		itemPanel.addContainerListener(new ContainerListener() {

			public void componentRemoved(ContainerEvent arg0) {
				scrollPane.revalidate();
			}

			public void componentAdded(ContainerEvent arg0) {
				scrollPane.revalidate();
			}
		});
		
		container.add(scrollPane);
		this.add(container);
		
		scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
	}

	/**
	 * Method to add key frames (list of VideoShotItem objects) to the panel.
	 * 
	 * @param arrayList (ArrayList of VideoShotItems objects)
	 */
	public void addKeyFrames(ArrayList<VideoShotItem> arrayList) {
		itemPanel.removeAll();

		for (VideoShotItem shotItem : arrayList) {
			shotCount++;
			
			shotItem.setCount(shotCount);
			itemPanel.add(shotItem);
			scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		}
	}

	/**
	 * Method to add VideoShotItem object to the panel.
	 * 
	 * @param shotItem
	 */
	public void addVideoShotItem(VideoShotItem shotItem) {
		shotCount++;
		shotItem.setCount(shotCount);
		itemPanel.add(shotItem);
		
		scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		this.updateUI();
	}
}
