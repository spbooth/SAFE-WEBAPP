// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for objects that represent the actual Chart graphic Objects
 * implementing this interface are intended to be the graphic-library internal
 * implementation of chart objects.
 * 
 * @author spb
 * @param <P> Type of Plot object
 * 
 */
public interface ChartData<P extends Plot> {
	/**
	 * send a png file to an OutputStream
	 * 
	 * @param res
	 * @throws IOException 
	 */
	public void createPNG(OutputStream res) throws Exception;

	/**
	 * create a PNG file from the chart
	 * 
	 * @param pngfile
	 * @throws IOException
	 */
	public void createPNG(File pngfile) throws Exception;

	/**
	 * create a SVG file from the chart
	 * 
	 * @param dest
	 * @throws Exception
	 */
	public void writeGraphics(Graphics2D dest) throws Exception;

	/** Get the preferred size of the plot 
	 * 
	 * @return Dimension
	 */
	public Dimension getSize();	

	/** make a dataset
	 * 
	 * @param i  initial size
	 * @return Plot
	 */
	public P makeDataSet(int i)throws Exception;


	public void setQuantityName(String s);

	public String getQuantityName();

	public void setTitle(String t);
	
	public void setGraphical(boolean val);
}