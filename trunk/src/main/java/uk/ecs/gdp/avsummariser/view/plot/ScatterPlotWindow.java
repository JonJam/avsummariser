package uk.ecs.gdp.avsummariser.view.plot;

import java.awt.Color;
import java.awt.Shape;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

/**
 * JFrame class to draw graph for all data.
 * 
 * @author Michael Harris
 * @version 1
 */
public class ScatterPlotWindow extends JFrame {
	
	private XYSeriesCollection col;

	public ScatterPlotWindow(String title, XYSeries series, String xLabel, String yLabel) {
		
		super(title);

		col = new XYSeriesCollection();
		col.addSeries(series);

		final NumberAxis domainAxis = new NumberAxis(xLabel);
		JFreeChart jfreechart = ChartFactory.createScatterPlot(title, xLabel, yLabel, col, PlotOrientation.VERTICAL, true, true, false);
		Shape cross = ShapeUtilities.createDiagonalCross(3, 1);

		XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);
		XYItemRenderer renderer = xyPlot.getRenderer();
		renderer.setSeriesShape(0, cross);

		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.blue);
		renderer.setSeriesPaint(2, Color.green);
		renderer.setSeriesPaint(3, Color.YELLOW);
		
		final ChartPanel panel = new ChartPanel(jfreechart);

		panel.setPreferredSize(new java.awt.Dimension(700, 570));
		panel.setMinimumDrawHeight(10);
		panel.setMaximumDrawHeight(2000);
		panel.setMinimumDrawWidth(20);
		panel.setMaximumDrawWidth(2000);

		setContentPane(panel);
	}

	/**
	 * Method to add a data series to the graph
	 * 
	 * @param series (XYSeries object)
	 */
	public void addSeries(XYSeries series) {
		if (series != null) {
			col.addSeries(series);
		}
	}
}
