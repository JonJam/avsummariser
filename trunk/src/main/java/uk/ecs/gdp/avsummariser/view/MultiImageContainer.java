package uk.ecs.gdp.avsummariser.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

/**
 * Class which holds multiple image containers to display them in the character gui.
 * 
 * @author Samantha Kanza
 * @version 1
 */

public class MultiImageContainer extends JPanel{
	
	ArrayList<BufferedImage> images;
	
	//allows multiple images to be stored in the grid for each character
	public MultiImageContainer(ArrayList<BufferedImage> charImages)
	{
		this.images = charImages;
		int row = (charImages.size()/4 * 60) + 60;
		this.setLayout(new GridLayout(3,14));
		addChars();
		this.setVisible(true);
		
	}
	
	public void addChars()
	{
		for(BufferedImage myImage: images)
		{
			this.add(new ImageContainer(myImage, 50));
		}
		
		this.updateUI();
	}

}
