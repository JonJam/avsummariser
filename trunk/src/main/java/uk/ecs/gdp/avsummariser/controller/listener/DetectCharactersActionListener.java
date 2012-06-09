package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.ecs.gdp.avsummariser.error.FileNotPlayableException;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.facialdetection.CharacterDetectedObserver;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;

public class DetectCharactersActionListener implements ActionListener{

	AVSummaryView view;
	AVSummaryModel model;
	
	public DetectCharactersActionListener(AVSummaryView view, AVSummaryModel model)
	{
		this.view = view;
		this.model = model;
	}
	
	public void actionPerformed(ActionEvent arg0)
	{
		long initialTime = System.currentTimeMillis();
		CharacterDetectedObserver cda;
		try {
			VideoFile selectedVideo = model.getSelectedVideo();
			
			if(selectedVideo != null)
			{
				cda = model.getCharactersInVideo(model.getSelectedVideo());
				cda.setView(view);
			}
		} catch (FileNotPlayableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("time = " + ((System.currentTimeMillis() - initialTime)/60000) + " minutes");
	}

	
	
}
