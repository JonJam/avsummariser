package uk.ecs.gdp.avsummariser.controller.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;

import uk.ecs.gdp.avsummariser.view.plot.ScatterPlotWindow;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowserItem;

/**
 * ActionListener class to plot graph of data for each video.
 *  
 * @author Michael Harris
 * @version 1
 */
public class DataViewActionListener implements ActionListener {
	
	private VideoBrowserItem videoBrowserItem;

	public DataViewActionListener(VideoBrowserItem videoBrowserItem) {
		this.videoBrowserItem = videoBrowserItem;
	}

	public void actionPerformed(ActionEvent e) {

		//Create graph window and plot shot data
		final ScatterPlotWindow scatterPlotWindow = new ScatterPlotWindow("Shot Length Chart", videoBrowserItem.getvFile().shotData(), "Time", "Shot Length (secs)");
		
		//Plot people mentioned data.
		for (XYSeries nameSeries : videoBrowserItem.getvFile().mentionedNamesData()) {
			scatterPlotWindow.addSeries(nameSeries);
		}
		
		//Plot location mentioned data
		for (XYSeries locationSeries : videoBrowserItem.getvFile().mentionedLocationData()) {
			scatterPlotWindow.addSeries(locationSeries);
		}
		
		//Plot loudness data
		scatterPlotWindow.addSeries(videoBrowserItem.getvFile().volumeData());
		
		//Position and display graph window.
		scatterPlotWindow.pack();
		RefineryUtilities.centerFrameOnScreen(scatterPlotWindow);
		scatterPlotWindow.setVisible(true);
	}
}
