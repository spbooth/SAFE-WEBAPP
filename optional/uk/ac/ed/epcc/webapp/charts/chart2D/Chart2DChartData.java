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
package uk.ac.ed.epcc.webapp.charts.chart2D;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.sourceforge.chart2d.Chart2D;
import net.sourceforge.chart2d.LegendProperties;
import uk.ac.ed.epcc.webapp.charts.Chart;
import uk.ac.ed.epcc.webapp.charts.ChartData;
import uk.ac.ed.epcc.webapp.charts.SetPlot;

public abstract class Chart2DChartData<P extends SetPlot> implements ChartData<P> {
	Chart2D c2d;


	

	public void createPNG(OutputStream res) throws IOException, Chart2DException {
		if( ! c2d.validate(true)){
			throw new Chart2DException("Error validating chart");
		}
		// Create an image to save
		RenderedImage rendImage = c2d.getImage();
		
		ImageIO.write(rendImage, "png", res);
		
	}

	public void createPNG(File file) throws IOException, Chart2DException {
		if( ! c2d.validate(false)){
			throw new Chart2DException("Error validating chart");
		}
		
		// Create an image to save
		RenderedImage rendImage = c2d.getImage();
		// Save as PNG Only works on 1.4+
		
		ImageIO.write(rendImage, "png", file);
	}

	public void writeGraphics(Graphics2D g){
		c2d.paint(g);
	}

	
	public String[] getLegends() {
		LegendProperties lprop = c2d.getLegendProperties();
		if (lprop.getLegendExistence()) {
			return lprop.getLegendLabelsTexts();
		}
		return null;
	}

	protected void setChart2D(Chart2D c) {
		c2d = c;
	}

	

	public void setLegends(String leg[]) {
		LegendProperties lprop = c2d.getLegendProperties();
		if( leg == null || leg.length == 0){
			lprop.setLegendExistence(false);
		}else{
			lprop.setLegendExistence(true);
			lprop.setLegendLabelsTexts(leg);
		}
	}

	
    public void setTitle(String t){
    	c2d.getObject2DProperties().setObjectTitleText(t);
    }
	public Dimension getSize() {
		c2d.getImage(); // this ensures the internal state is fully up to date.
		return c2d.getSize();
	}
}