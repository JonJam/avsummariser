package uk.ecs.gdp.avsummariser.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Class which draws each character image
 * 
 * @author Samantha Kanza
 * @version 1
 */

public class ImageContainer extends JPanel{

	BufferedImage image;
	int size;
	
	public ImageContainer(BufferedImage image, int size){
		this.image = image;
		this.size = size;
		this.setPreferredSize(new Dimension(size,size));
		this.setMaximumSize(new Dimension(size,size));
		
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, size, size, this);
		this.updateUI();
	}
}
