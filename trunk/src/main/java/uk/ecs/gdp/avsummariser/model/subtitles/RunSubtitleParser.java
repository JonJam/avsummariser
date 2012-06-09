package uk.ecs.gdp.avsummariser.model.subtitles;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.view.AVSummaryView;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * Class to display file chooser, update the display and run the SubtitleParser as appropriate.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class RunSubtitleParser {

	public static void runSubtitleParser(AVSummaryView view, AVSummaryModel model, VideoBrowserItem item){
		
		//Set up file chooser
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileFilter xmlFilter = new FileNameExtensionFilter("XML file", "xml", "XML");
		fileChooser.setFileFilter(xmlFilter);
		fileChooser.setMultiSelectionEnabled(false);
		
		int returnVal = fileChooser.showOpenDialog(item);
		VideoFile video = item.getvFile();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			//File selected
			
			//Get subtitles file
			File subtitleFile = fileChooser.getSelectedFile();
		
			//Parse subtitle file into objects.
			ArrayList<Subtitle> subtitles = SubtitleParser.parseSubtitleFile(subtitleFile);
			
			if(subtitles != null){
				//Subtitles not null
				
				video.setSubtitleFilePath(subtitleFile.getAbsolutePath());
				video.setSubtitles(subtitles);
				
				item.setSubtitleLoaded();
				
				if(item.isSelected()){
					//Video is selected so run people and location finders
					
					view.getFrame().getSubtitlePanel().update();
					
					model.runPeopleFinder(view, video);
					model.runLocationFinder(view, video);
				}
				
				view.displayOutcome("DVB Subtitle file loaded.");
			} else{
				//Subtitles are null meaning incorrect file format.
				
				video.setSubtitleFilePath("");
				video.setSubtitles(null);
				
				item.setSubtitleNotLoaded();
				
				view.displayOutcome("Error loading in DVB subtitles file - Please check file is in the correct format.");
			}		
		}
	}
}
