package uk.ecs.gdp.avsummariser.model.threads;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.audio.FrequencyDetector;

/**
 * Thread class to perform Frequency detection on a video.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class FrequencyDetectorThread extends Thread{

	private VideoFile video;
	
	public FrequencyDetectorThread(VideoFile video) {
		this.video = video;
	}

	public void run() {
		//Calculate frequency and intensity for all audio samples in video
		FrequencyDetector.findAllFrequenciesInVideo(video);
		
		//Set Frequency Detector boolean as finished
		video.setFrequencyDetectorFinished();
	}
}
