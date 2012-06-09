package uk.ecs.gdp.avsummariser.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import uk.ecs.gdp.avsummariser.model.facialdetection.VideoCharacter;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Class which shows all the character detais per video.
 * 
 * @author Samantha Kanza
 * @version 1
 */

public class CharacterDetailPanel extends JPanel {
	JScrollPane scrollpane;
	JPanel charDetailPanel;
	ArrayList<ImageContainer> images = new ArrayList<ImageContainer>();
	int counter = 0;

	public CharacterDetailPanel() {
		charDetailPanel = new JPanel();
		this.setLayout(new BorderLayout());
		charDetailPanel.setLayout(new BoxLayout(charDetailPanel,BoxLayout.Y_AXIS));
		//charDetailPanel.setLayout(new GridLayout(5,6));
		scrollpane = new JScrollPane(charDetailPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setPreferredSize(new Dimension(this.getHeight(), this
				.getHeight()));
		this.add(scrollpane, BorderLayout.CENTER);
	}

	public void addCharacter(VideoCharacter myChar) {

		charDetailPanel.add(new CharacterInfoPanel(myChar));
		//charDetailPanel.add(new ImageContainer(ImageUtilities.createBufferedImage(myChar.getCharImage()), 100));
		charDetailPanel.updateUI();
		scrollpane.revalidate();
	}
}
