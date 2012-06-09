package uk.ecs.gdp.avsummariser.view.videobrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.ecs.gdp.avsummariser.controller.listener.SelectVideoActionListener;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.threads.SubtitlesParseThread;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * JPanel class to represent a video browser item.
 * 
 * @author Michael Harris
 * @author Jonathan Harrison
 * @version 1
 */
public class VideoBrowserItem extends JPanel {

	private JLabel title;
	private JButton subtitleBtn;
	private JLabel middle;
	private boolean isSelected = false;
	private final VideoFile vFile;

	public VideoBrowserItem(final AVSummaryView view,final AVSummaryModel model, final VideoFile vFile) {

		this.vFile = vFile;
		this.setPreferredSize(new Dimension(200, 100));
		this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
		this.setMaximumSize(new Dimension(200, 100));
		this.setLayout(new BorderLayout());
		
		title = new JLabel(vFile.getFriendlyName());
		middle = new JLabel();
		subtitleBtn = new JButton();
		
		if (vFile.getSubtitles() == null) {
			setSubtitleNotLoaded();
		} else {
			setSubtitleLoaded();
		}
		
		subtitleBtn.setBackground(Color.DARK_GRAY);
		subtitleBtn.setForeground(Color.LIGHT_GRAY);
		title.setForeground(Color.LIGHT_GRAY);
		
		this.add(title, BorderLayout.NORTH);
		this.add(subtitleBtn, BorderLayout.SOUTH);
		this.add(middle, BorderLayout.CENTER);
		this.setBackground(Color.BLACK);

		final VideoBrowserItem thisClass = this;
		
		subtitleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.runSubtitleParser(view, thisClass);
			}
		});

		this.addMouseListener(new RightClickPopupListener(this, model, view));
		this.addMouseListener(new SelectVideoActionListener(view, model, this));
	}

	/**
	 * Method to get VideoFile object.
	 * 
	 * @return VideoFile object
	 */
	public VideoFile getvFile() {
		return vFile;
	}

	/**
	 * Method to set label when subtitle file loaded.
	 */
	public void setSubtitleLoaded() {
		subtitleBtn.setText("Subs loaded.");
	}

	/**
	 * Method to set label when subtitle not loaded.
	 */
	public void setSubtitleNotLoaded() {
		subtitleBtn.setText("No subs available.");
	}
	
	/**
	 * Method to determine whether this VideoBrowserItem is selected.
	 * 
	 * @return boolean value
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Method to select this VideoBrowserItem
	 */
	public void select() {
		this.setBackground(Color.GRAY);
		isSelected = true;
	}

	/*
	 * Method to deselect this VideoBrowserItem
	 */
	public void deselect() {
		isSelected = false;
		this.setBackground(Color.BLACK);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ImageIcon icon = new ImageIcon("img/clapper-board.png");
		g.drawImage(icon.getImage(), 70, 30, null);
		this.updateUI();
	}
}
