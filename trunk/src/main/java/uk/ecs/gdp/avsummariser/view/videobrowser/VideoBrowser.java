package uk.ecs.gdp.avsummariser.view.videobrowser;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * JPanel class for the video browser
 * 
 * @author Michael Harris
 * @version 1
 */
public class VideoBrowser extends JPanel {

	private AVSummaryModel model;
	private AVSummaryView view;
	
	private JScrollPane scrollPane;
	private JButton addButton;
	private JPanel container;
	private JPanel buttonPanel;
	private JPanel fileViewerPanel;
	private JLabel emptyLabel;
	private JFileChooser fileChooser;
	private ArrayList<VideoBrowserItem> browserItems;
	
	private static final String[] EXTENSIONS_VIDEO = {"mp4"};
	//private static final String[] EXTENSIONS_VIDEO = { "amv", "asf", "avi",
//			"divx", "dv", "flv", "gxf", "iso", "m1v", "m2v", "m2t", "m2ts",
//			"m4v", "mkv", "mov", "mp2", "mp4", "mpeg", "mpeg1", "mpeg2",
//			"mpeg4", "mpg", "mts", "mxf", "nsv", "nuv", "ogg", "ogm", "ogv",
//			"ogx", "ps", "rec", "rm", "rmvb", "tod", "ts", "vob", "vro", "wmv" };

	
	public VideoBrowser(final AVSummaryModel model, AVSummaryView view) {
		this.model = model;
		this.view = view;

		fileViewerPanel = new JPanel();
		fileViewerPanel.setLayout(new BoxLayout(fileViewerPanel,BoxLayout.Y_AXIS));
		
		buttonPanel = new JPanel();
		
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		emptyLabel = new JLabel("No video added");

		scrollPane = new JScrollPane(fileViewerPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(220, 320));
		
		browserItems = new ArrayList<VideoBrowserItem>();

		fileViewerPanel.addContainerListener(new ContainerListener() {

			public void componentRemoved(ContainerEvent arg0) {
				scrollPane.revalidate();
			}

			public void componentAdded(ContainerEvent arg0) {
				scrollPane.revalidate();
			}
		});
		
		addButton = new JButton("+");
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				fileChooser = new JFileChooser();
				fileChooser.setAcceptAllFileFilterUsed(false);
				
				FileFilter videoFilter = new FileNameExtensionFilter("Video file", EXTENSIONS_VIDEO);

				fileChooser.setFileFilter(videoFilter);
				fileChooser.setMultiSelectionEnabled(true);
				int returnVal = fileChooser.showOpenDialog(container);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File[] files = fileChooser.getSelectedFiles();
					for (File file : files) {
						addVideoItem(model.addVideoFile(file));
					}
					
				}
			}
		});

		container.add(scrollPane);
		buttonPanel.add(addButton);
		container.add(buttonPanel);

		this.add(container);

		fileViewerPanel.add(emptyLabel);
	}

	/*
	 * Method to clear all the UI elements and sets the currently selected video in the model to null.
	 */
	public void deselectAll() {
		model.setSelectedVideo(null);
		
		for (VideoBrowserItem browserItem : browserItems) {
			browserItem.deselect();
		}
	}

	/**
	 * Method to add a video item to the video browser
	 * 
	 * @param videoFile (VideoFile object)
	 */
	public void addVideoItem(VideoFile videoFile) {
		if (browserItems.size() == 0) {
			// Get rid of the "no Items" label
			fileViewerPanel.removeAll();
			view.getFrame().getGenerateSummaryButton().setEnabled(true);
		}
		
		VideoBrowserItem item = new VideoBrowserItem(view, model, videoFile);
		browserItems.add(item);
		fileViewerPanel.add(item);
	}

	/**
	 * Method to reload all items in the browser from the list of loaded files in the model.
	 */
	public void refreshList() {

		fileViewerPanel.removeAll();
		ArrayList<VideoFile> vidList = model.getLoadedVideos();
		
		if(vidList.size() == 0){
			view.getFrame().getGenerateSummaryButton().setEnabled(false);
		}
		
		for (VideoFile vFile : vidList) {
			VideoBrowserItem item = new VideoBrowserItem(view, model, vFile);
			browserItems.add(item);
			fileViewerPanel.add(item);
		}
	}
}
