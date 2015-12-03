//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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