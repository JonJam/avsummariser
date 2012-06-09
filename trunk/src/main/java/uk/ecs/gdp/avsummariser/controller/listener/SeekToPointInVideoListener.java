package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;

import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

/**
 * Mouse listener class to seek to point in video when using Play links in video and summary tabs.
 * 
 * @author Michael Harris
 * @version 1
 */
public class SeekToPointInVideoListener implements MouseListener{
	
	private AVSummaryView view;
	private VideoFile videoFile;
	private long position;
	
	public SeekToPointInVideoListener(AVSummaryView view, VideoFile videoFile, long position){
		this.view = view;
		this.videoFile = videoFile;
		this.position = position;
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e) {
		try {
			//Get video from model.
			Video<MBFImage> vid = AVSummaryModel.getVideoFromPath(videoFile.getAbsolutePath());
			
			//Play video in GUI.
			view.playVideo(vid);
			
			//Seek to position required.
			view.seekInVideoInMillis(position);
			
		} catch (FileNotPlayableException e1) {
			e1.printStackTrace();
		}		
	}

}
