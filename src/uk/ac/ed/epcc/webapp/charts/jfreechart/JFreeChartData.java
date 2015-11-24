// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import uk.ac.ed.epcc.webapp.charts.Chart;
import uk.ac.ed.epcc.webapp.charts.ChartData;
import uk.ac.ed.epcc.webapp.charts.Plot;
import uk.ac.ed.epcc.webapp.charts.SetPlot;

public abstract class JFreeChartData<P extends Plot> implements ChartData<P> {
	



	/**
	 * 
	 */
	private static final int DEFAULT_Y_SIZE = 400;

	/**
	 * 
	 */
	private static final int DEFAULT_X_SIZE = 800;

	//String labels[];

	


	String quantity;
	String title;


	public void createPNG(OutputStream res) throws IOException {
		ChartUtilities.writeChartAsPNG(res,
				getCustomisedJFreeChart(), DEFAULT_X_SIZE, DEFAULT_Y_SIZE);
	}

	public void createPNG(File pngfile) throws IOException {
		ChartUtilities.saveChartAsPNG(pngfile,
				getCustomisedJFreeChart(), DEFAULT_X_SIZE, DEFAULT_Y_SIZE);
	}
	public void createPNG(String pngfile) throws IOException {
		createPNG(new File(pngfile + ".png"));
	}

	public void writeGraphics(Graphics2D g) throws Exception {
		getCustomisedJFreeChart().draw(g, new Rectangle(DEFAULT_X_SIZE, DEFAULT_Y_SIZE));
		//throw new UnsupportedOperation("JFreeChart does not support SVG");
	}
	public abstract JFreeChart getJFreeChart();



	private JFreeChart getCustomisedJFreeChart(){
		JFreeChart chart = getJFreeChart();
		chart.getPlot().setBackgroundPaint(new GradientPaint(0.0f, 0.0F, new Color(0.75F, 0.75F, 1.0F), 0.0F, 100.0F, Color.white, false));
		if( title != null && title.trim().length() > 0){
			chart.setTitle(title);
		}
		return chart;
	}


	

	public void setQuantityName(String s) {
		quantity = s;

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ChartData#getQuantityName()
	 */
	public String getQuantityName() {
		return quantity;
	}
    public void setTitle(String t){
    	this.title=title;
    }

	public Dimension getSize() {
		
		return new Dimension(DEFAULT_X_SIZE,DEFAULT_Y_SIZE);
	}

	public void setGraphical(boolean val) {
		// nothin needed
		
	}
}