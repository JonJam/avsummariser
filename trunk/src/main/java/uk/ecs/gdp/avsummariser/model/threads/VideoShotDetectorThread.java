package uk.ecs.gdp.avsummariser.model.threads;

import org.openimaj.image.MBFImage;
import org.openimaj.video.processing.shotdetector.VideoShotDetector;

import uk.ecs.gdp.avsummariser.controller.listener.ShotDetectedListenerImpl;
import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;

/**
 * Thread class to run video shot detection
 * 
 * @author Michael Harris
 * @version 1
 */
public class VideoShotDetectorThread extends Thread implements Runnable {
	
	private VideoShotDetector<MBFImage> shotDetector;
	private VideoFile vFile;
	
	public VideoShotDetectorThread(VideoFile currentVFile, ShotDetectedListenerImpl shotDetectorListenerImpl) throws FileNotPlayableException {
		
		this.vFile = currentVFile;
		shotDetector = new VideoShotDetector<MBFImage>(AVSummaryModel.getVideoFromPath(currentVFile.getAbsolutePath()), false);
		shotDetector.setThreshold(60000);
		shotDetector.addShotDetectedListener(shotDetectorListenerImpl);
	}

	public void run() {
		//Run shot detector
		shotDetector.process();
		
		//Set shot detection as finished.
		vFile.setVideoShotDetectionFinished();
		
	}
}