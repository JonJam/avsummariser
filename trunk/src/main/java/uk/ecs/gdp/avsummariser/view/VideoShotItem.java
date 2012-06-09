package uk.ecs.gdp.avsummariser.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openimaj.video.timecode.VideoTimecode;

import uk.ecs.gdp.avsummariser.model.VideoShot;

/**
 * JPanel class which represents a shot and the related subtitles.
 * 
 * @author Michael Harris
 * @version 1
 */
public class VideoShotItem extends JPanel {

	private int count = 0;
	private JLabel countLabel;

	public VideoShotItem(VideoShot videoShot) {
		super(new GridLayout());
		
		BufferedImage thumbnail = videoShot.getThumbnail();
		VideoTimecode videoTimecode = videoShot.getTimecode();
		
		this.setPreferredSize(new Dimension(thumbnail.getWidth() + 100, thumbnail.getHeight()));
		
		countLabel = new JLabel(" Shot Count " + count);
		
		this.add(new JLabel(" ----- " + "  "));
		this.add(countLabel);
		this.add(new JLabel(" at " + (videoTimecode.getTimecodeInMilliseconds() / 1000) + "s"));
		this.add(new VideoThumbnail(thumbnail));
		
		this.updateUI();
	}

	/**
	 * Method to set count
	 * 
	 * @param count (int value)
	 */
	public void setCount(int count) {
		this.count = count;
		
		countLabel.setText(" Shot Count " + count);
	}
	
	/**
	 * JPanel class to represent shot thumbnail image
	 * 
	 * @author Michael Harris
	 * @version 1
	 */
	class VideoThumbnail extends JPanel {
		
		private BufferedImage thumbnail = null;

		public VideoThumbnail(BufferedImage thumbnail) {
			this.setPreferredSize(new Dimension(100, 100));
			this.thumbnail = thumbnail;
			this.updateUI();
		}

		@Override
		protected void paintComponent(Graphics g) {

			super.paintComponent(g);
			ImageIcon icon = new ImageIcon(thumbnail);
			g.drawImage(icon.getImage(), 0, 0, null);
			this.updateUI();
		}
	}
}