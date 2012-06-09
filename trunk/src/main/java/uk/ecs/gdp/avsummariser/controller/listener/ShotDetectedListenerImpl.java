package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.processing.shotdetector.ShotBoundary;
import org.openimaj.video.processing.shotdetector.ShotDetectedListener;
import org.openimaj.video.processing.shotdetector.VideoKeyframe;
import org.openimaj.video.timecode.VideoTimecode;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.VideoShot;
import uk.ecs.gdp.avsummariser.view.ShotDetailPanel;
import uk.ecs.gdp.avsummariser.view.VideoShotItem;

/**
 * Class to add shots to Shots Video Tab Panel as a video is processed.
 * 
 * @author Michael Harris
 * @version 1
 */
public class ShotDetectedListenerImpl implements ShotDetectedListener<MBFImage> {

	private ShotDetailPanel shotDetailPanel;
	private VideoFile videoFile;

	public ShotDetectedListenerImpl(VideoFile currentVFile) {
		this.videoFile = currentVFile;
	}

	public void differentialCalculated(VideoTimecode arg0, double arg1, MBFImage arg2) {}

	/**
	 * Method called when a video shot is detected.
	 * 
	 * @param arg0 (ShotBoundary object)
	 * @param frame (VideoKeyFrame object)
	 */
	public void shotDetected(ShotBoundary arg0, VideoKeyframe<MBFImage> frame) {

		//Create image for shot
		final BufferedImage bimg = ImageUtilities.createBufferedImageForDisplay(frame.getImage());
		BufferedImage scaledImage = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		AffineTransform xform = AffineTransform.getScaleInstance(0.1, 0.1);

		//Draw image
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(bimg, xform, null);
		graphics2D.dispose();

		//Create new VideoShot object and add to VideoFile object
		VideoShot videoShot = new VideoShot(frame.getTimecode(), scaledImage);
		videoFile.getShots().add(videoShot);
		
		//Create GUI object for shot to display
		VideoShotItem shotItem = new VideoShotItem(videoShot);
		
		if (shotDetailPanel != null) {
			//Add new shot GUI item to panel
			shotDetailPanel.addVideoShotItem(shotItem);
		}

	}

	/**
	 * Method to set Shot Detail Panel
	 * 
	 * @param shotDetailPanel (ShotDetailPanel object)
	 */
	public void setShotDetailPanel(ShotDetailPanel shotDetailPanel) {

		this.shotDetailPanel = shotDetailPanel;
	}

}
