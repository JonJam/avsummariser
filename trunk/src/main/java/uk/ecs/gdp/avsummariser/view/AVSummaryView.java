package uk.ecs.gdp.avsummariser.view;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;

import uk.ecs.gdp.avsummariser.controller.AVSummaryController;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;

/**
 * Main view class for the system.
 * 
 * @author Michael Harris
 * @author Jonathan Harrison
 * @version 1
 */
public class AVSummaryView {
	
	private GUIFrame guiFrame;
	private VideoDisplay<MBFImage> display = null;

	public AVSummaryView(AVSummaryModel model, AVSummaryController controller) {
		guiFrame = new GUIFrame(model, controller, this);
	}

	/**
	 * Method to refresh the video browser list.
	 */
	public void refreshVideoList() {
		guiFrame.getVideoBrowser().refreshList();
		guiFrame.getVideoBrowser().updateUI();
	}

	/**
	 * Method to get the video panel
	 * 
	 * @return JPanel object
	 */
	public JPanel getVideoPanel() {
		return guiFrame.getVideoPlayerPanel().getVideoDock();
	}

	/**
	 * Method to play a video in the view and adds a position listener.
	 * 
	 * @param videoFromPath (Video object)
	 */
	public void playVideo(Video<MBFImage> videoFromPath) {
		
		//Stop current video playing
		stopVideoPlayBack();

		//Set slider to 0.
		guiFrame.getVideoPlayerPanel().getPositionSlider().setValue(0);
		
		//Create new video display
		display = VideoDisplay.createVideoDisplay(videoFromPath, guiFrame.getVideoPlayerPanel().getVideoDock());
		
		//Get number of video frames
		final long videoFrames = display.getVideo().countFrames();

		// A listener to keep the position of the slider up-to-date as the video plays.
		display.addVideoListener(new VideoDisplayListener<MBFImage>() {

			public void beforeUpdate(MBFImage arg0) {}

			public void afterUpdate(VideoDisplay<MBFImage> arg0) {
				
				double currFrame = (arg0.getVideo().getTimeStamp() / 1000)* display.getVideo().getFPS();
				int position = (int) (currFrame * 100 / videoFrames);
								
				JSlider slider = guiFrame.getVideoPlayerPanel().getPositionSlider();
				
				if (!slider.getValueIsAdjusting()) {
						
					/* The the video is playing and the difference between what
					 * the slider is and what it is calculated at is one.
				     * Therefore we can increment the slider.
				     */
					slider.setValue(position);
				}
			}
		});
	}

	/**
	 * Method to seek to position in a video from a percentage.
	 * 
	 * @param position (int value) -  an int out of 100 (percentage of the video to seek through)
	 */
	public void seekInVideo(int position) {

		if (display != null) {
			//Display is not null
			
			long frames = display.getVideo().countFrames();
			double seconds = frames / display.getVideo().getFPS();
			display.seek((long) ((seconds / 100) * position));
		}
	}

	/**
	 * Method to seek to a position in a video from a time in milliseconds.
	 * 
	 * @param millis (lone value)
	 */
	public void seekInVideoInMillis(long millis) {
		
		if (display != null) {
			//Display is not null
			
			display.seek(millis/1000);
			long frames = display.getVideo().countFrames();
			double currFrame = (millis / 1000)* display.getVideo().getFPS();
			int position = (int) (currFrame/frames *100);
			
			JSlider slider = guiFrame.getVideoPlayerPanel().getPositionSlider();
			slider.setValue(position);
		}
	}

	/**
	 * Method to pause a video
	 */
	public void pauseVideo() {
		
		if (display != null) {
			//Display is not null
			display.togglePause();
		}
	}

	/**
	 * Method to display a message in the GUI.
	 * 
	 * @param message (String object)
	 */
	public void displayOutcome(String message) {
		guiFrame.setStatusLabel(message);
	}

	/**
	 * Method to stop a video playing.
	 */
	public void stopVideoPlayBack() {
		if (display != null) {
			
			//Close video
			display.close();
			
			display = null;
			
			guiFrame.getVideoPlayerPanel().getVideoDock().removeAll();
			guiFrame.getVideoPlayerPanel().getVideoDock().updateUI();
		}
	}

	/**
	 * Method to get the display.
	 * 
	 * @return VideoDisplay object
	 */
	public VideoDisplay<MBFImage> getDisplay() {
		return display;
	}

	/**
	 * Method to get the GUIFrame object
	 * 
	 * @return GUIFrame object
	 */
	public GUIFrame getFrame() {
		return guiFrame;
	}

	/**
	 * Method to add a VideoShotItem object to the Shot Panel
	 * 
	 * @param videoFile (VideoFile Object)
	 * @param shotItem (VideoShotItem object)
	 */
	public void addVideoShotItem(VideoFile videoFile, VideoShotItem shotItem) {
		ShotDetailPanel shotPanel = guiFrame.getShotPanel(videoFile);

		shotPanel.addVideoShotItem(shotItem);
		shotPanel.updateUI();
	}
}
