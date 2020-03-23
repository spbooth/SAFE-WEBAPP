//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import uk.ac.ed.epcc.webapp.AppContext;

/**
 * @author spb
 *
 */
public class JFreeSetup {
    private static StandardChartTheme theme=null;
	public synchronized static void setup(AppContext conn){
		if( theme == null ) {
			// ChartFactory stores these in static variable so 
			// make sure we don't serialise this
			theme = new StandardChartTheme("webapp");
			String font_family = "Arial";
			if( conn != null ) {
				font_family = conn.getInitParameter("webapp.jfreechart.font_family", font_family);
			}
			theme.setSmallFont(new Font(font_family, Font.PLAIN, 10));
			theme.setRegularFont(new Font(font_family, Font.PLAIN, 12));
			theme.setLargeFont(new Font(font_family, Font.PLAIN, 14));
			theme.setExtraLargeFont(new Font(font_family, Font.PLAIN, 20));

			ChartFactory.setChartTheme(theme);
		}
	}
}
