package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.audio.VolumeDetector;

/**
 * Thread class to run volume detection.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class VolumeDetectorThread extends Thread {

	private VideoFile video;

	public VolumeDetectorThread(VideoFile video) {
		this.video = video;
	}

	public void run() {

		//Calculate effective sound pressure for all audio samples.
		VolumeDetector.findAllVolumesInVideo(video);
		//Find loud sections in video
		VolumeDetector.findLoudSectionsInVideo(video);

		//Set volume detector as finished.
		video.setVolumeDetectorFinished();
	}
}
