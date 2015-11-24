// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.util.Date;

import org.jfree.data.xy.XYSeries;

import uk.ac.ed.epcc.webapp.charts.InvalidTransformException;
import uk.ac.ed.epcc.webapp.charts.ScatterPeriodPlot;
import uk.ac.ed.epcc.webapp.charts.strategy.RangeMapper;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: JFreeScatterPlot.java,v 1.4 2014/09/15 14:30:13 spb Exp $")
public class JFreeScatterPlot implements ScatterPeriodPlot {

	private final SplitTimePeriod period;
	private final int nsplit;
	private final String label;
	private final int npoints;
	XYSeries series;
	/**
	 * 
	 */
	public JFreeScatterPlot(String label, SplitTimePeriod period, int nsplit) {
		this.label=label;
		this.period=period;
		this.nsplit=nsplit;
		npoints=period.getNsplit()*nsplit;
		series = new XYSeries(label);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPlot#addData(uk.ac.ed.epcc.webapp.charts.strategy.RangeMapper, uk.ac.ed.epcc.webapp.charts.strategy.RangeMapper, java.lang.Object)
	 */
	public <D> void addData(RangeMapper<D> x, RangeMapper<D> y, D object)
			throws InvalidTransformException {
		for(TimePeriod p : period.getSubPeriods()){
			System.out.println("Outer period "+p.toString());
			RegularSplitPeriod p2 = new RegularSplitPeriod(p.getStart(), p.getEnd(), nsplit);
			for(TimePeriod q : p2.getSubPeriods()){
				Date start = q.getStart();
				Date end = q.getEnd();
				System.out.println("inner period "+q);
				if( x.overlapps(object, start, end) && y.overlapps(object, start, end)){
					float fx = x.getOverlapp(object, start, end);
					float fy = y.getOverlapp(object, start, end);
					series.add(fx, fy);
				}
			}
		}
		
	}
	
	public void setLabel(String key){
		series.setKey(key);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.Plot#scale(float)
	 */
	public void scale(float scale) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodPlot#addPoint(float, float)
	 */
	public void addPoint(float x, float y) {
		series.add(x,y);
		
	}

	
}
