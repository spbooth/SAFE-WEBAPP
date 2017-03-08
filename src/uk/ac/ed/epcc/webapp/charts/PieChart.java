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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.InvalidArgument;
import uk.ac.ed.epcc.webapp.content.Table;
/** A basic piechart where the data comes from a Table or Map
 * 
 * @author spb
 *
 */

public class PieChart extends Chart {
	
	protected PieChart(AppContext conn) {
		super(conn);
	}
	public static double other_frac=0.03;
	
	 /** create a piechart from a column of a table.
	  * Results are sorted and cut.
	  * 
	  * @param t
	  * @param col_key
	 * @return boolean  true if more than one segment
	  * @throws InvalidArgument
	  */
	   public boolean getData(Table t, String col_key) throws InvalidArgument{
		   if( ! t.hasCol(col_key)){
			   return false;
		   }
	       SetPlot ds = getData(t.getHashtable(col_key));
	       sortSets(ds,other_frac);
	       return hasSignificantData(ds);
	   }
	   
	   public boolean hasSignificantData(SetPlot ds){
		   return ds.getNumSets() > 1;
	   }
	   /** create a PieChart from a column of a table labeled by the values of a different column.
	    * 
	    * @param t
	    * @param label_key
	    * @param col_key
	 * @return true if more than one segment
	    * @throws InvalidArgument
	    */
	   public boolean getData(Table t, String label_key,String col_key) throws InvalidArgument{
	       SetPlot ds = getData(t.getHashtable(col_key,label_key));
	       sortSets(ds,other_frac);
	       return hasSignificantData(ds);
	   }
	   /** Generate a Plot from a Map setting legends to the key values
	    * 
	    * @param data
	    * @return generated Plot
	    */
	   public SingleValueSetPlot getData(Map<String, Number> data) {
		  SingleValueSetPlot pc = addPieChart(data.size());
		  List<String> aul = new ArrayList<String>(data.keySet()); Collections.sort(aul);
			String[] cats = new String[aul.size()];
			String key = null;
			// Loop over the list to build up totals etc:
			for (int i = 0; i < aul.size(); i++ ) {
			    key = aul.get(i);
			    cats[i] = new String(key);
			    pc.set(i, (data.get(key)).floatValue() );
			}
            pc.setLegends(cats);
            return pc;
	}
	
	private SingleValueSetPlot addPieChart(int nset) {
		PieChartData chart = (PieChartData) getChartData();
		return chart.addPieChart(nset);
	}
	public static  PieChart getInstance(AppContext c) {
		return c.getService(GraphService.class).getPieChart();
	}
}