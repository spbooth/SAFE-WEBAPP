// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.charts.chart2D.PieChart2DChartData;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.InvalidArgument;
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
		PieChart ptc = new PieChart(c);
		Class<? extends PieChartData> clazz = c.getPropertyClass(PieChartData.class, PieChart2DChartData.class, "PieChartData");
		
		try {
			ptc.setChartData(c.makeObject(clazz));
		} catch (Exception e) {
			c.error(e,"Error making TimeChartData");
		}
		
		return ptc;
	}
}