//| Copyright - The University of Edinburgh 2014                            |
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDomainInfo;
import org.jfree.data.xy.XYRangeInfo;

import uk.ac.ed.epcc.webapp.charts.DataRange;
import uk.ac.ed.epcc.webapp.charts.GenericSplitSetPlot;
import uk.ac.ed.epcc.webapp.charts.TimeChartData;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** A {@link GenericSplitSetPlot} that implements the appropriate JFreeChart 
 * interfaces. We implement {@link IntervalXYDataset} to represent the actual time periods
 * so the data can be plotted as bar-chart and return the middle of the period as the data point.
 * @author spb
 *
 */

public class TimeChartDataSet extends GenericSplitSetPlot implements IntervalXYDataset,TableXYDataset, XYDomainInfo, XYRangeInfo {

	DatasetGroup group;
	Set<DatasetChangeListener> listeners;
	private final double times[];
	private final double starts[];
	private final double ends[];
	int dataset_id;
	private final int nitem;
	
	/**
	 * @throws InvalidArgument 
	 * 
	 */
	public TimeChartDataSet(TimeChartData chart,int nset, SplitTimePeriod period, int nsplit) throws InvalidArgument {
		super(chart);
		setSplits(nset, period, nsplit,false);
		nitem = period.getNsplit()*nsplit;
		times = new double[nitem];
		starts = new double[nitem];
		ends = new double[nitem];
		TimePeriod subs[] = period.getSubPeriods();
		int pos=0;
		for(TimePeriod p : subs){
			long start = p.getStart().getTime();
			long end = p.getEnd().getTime();
			long step = (end-start)/nsplit;
			long off=step/2;
			//long off=0L; // align with start
			for(int i=0;i<nsplit;i++){
				starts[pos]=start;
				times[pos]=start+off;
				ends[pos]=start+step;
				start += step;
				pos++;
			}
			ends[pos-1]=end;
		}
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#getSeriesCount()
	 */
	@Override
	public int getSeriesCount() {
		return getNumSets();
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#getSeriesKey(int)
	 */
	@Override
	public Comparable getSeriesKey(int arg0) {
		if( lab != null ){
			return lab[arg0];
		}
		return Integer.toString(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#indexOf(java.lang.Comparable)
	 */
	@Override
	public int indexOf(Comparable arg0) {
		if( lab != null){
			int i=0;
			for(String s : lab){
				if(s.equals(arg0)){
					return i;
				}
				i++;
			}
			return -1;
		}
		int i = Integer.parseInt(((String)arg0));
		if( i < 0 || i >= getNumSets()){
			return -1;
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#addChangeListener(org.jfree.data.general.DatasetChangeListener)
	 */
	@Override
	public void addChangeListener(DatasetChangeListener arg0) {
		if( listeners == null){
			listeners=new HashSet<>();
		}
		listeners.add(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#getGroup()
	 */
	@Override
	public DatasetGroup getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#removeChangeListener(org.jfree.data.general.DatasetChangeListener)
	 */
	@Override
	public void removeChangeListener(DatasetChangeListener arg0) {
		if(listeners != null){
			listeners.add(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#setGroup(org.jfree.data.general.DatasetGroup)
	 */
	@Override
	public void setGroup(DatasetGroup arg0) {
		this.group=arg0;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getDomainOrder()
	 */
	@Override
	public DomainOrder getDomainOrder() {
		return DomainOrder.ASCENDING;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getItemCount(int)
	 */
	@Override
	public int getItemCount(int arg0) {
		if( arg0 < 0 || arg0 >= getNumSets()){
			throw new IllegalArgumentException("Series out of range ");
		}
		return getItemCount();
		
	}
	

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getX(int, int)
	 */
	@Override
	public Number getX(int set, int item) {
		return times[item];
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getXValue(int, int)
	 */
	@Override
	public double getXValue(int set, int item) {
		return times[item];
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getY(int, int)
	 */
	@Override
	public Number getY(int set, int item) {
		return get(set,item/getNumItems(),item%getNumItems());
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getYValue(int, int)
	 */
	@Override
	public double getYValue(int set, int item) {
		return get(set,item/getNumItems(),item%getNumItems());
	}


	


	@Override
	protected void notifyChange() {
		if( listeners != null){
			for(DatasetChangeListener l : listeners){
				l.datasetChanged(new DatasetChangeEvent(this, this));
			}
		}
	}


	public int getDatasetId() {
		return dataset_id;
	}


	public void setDatasetId(int dataset_id) {
		this.dataset_id = dataset_id;
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndX(int, int)
	 */
	@Override
	public Number getEndX(int arg0, int arg1){
		return ends[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndXValue(int, int)
	 */
	@Override
	public double getEndXValue(int arg0, int arg1) {
		return ends[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndY(int, int)
	 */
	@Override
	public Number getEndY(int arg0, int arg1) {
		return getYValue(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndYValue(int, int)
	 */
	@Override
	public double getEndYValue(int arg0, int arg1) {
		return getYValue(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartX(int, int)
	 */
	@Override
	public Number getStartX(int arg0, int arg1) {
		return starts[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartXValue(int, int)
	 */
	@Override
	public double getStartXValue(int arg0, int arg1) {
		return starts[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartY(int, int)
	 */
	@Override
	public Number getStartY(int arg0, int arg1) {
		return getYValue(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartYValue(int, int)
	 */
	@Override
	public double getStartYValue(int arg0, int arg1) {
		return getYValue(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.TableXYDataset#getItemCount()
	 */
	@Override
	public int getItemCount() {
		return getNumCats()*getNumItems();
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDomainInfo#getDomainBounds(java.util.List, boolean)
	 */
	@Override
	public Range getDomainBounds(List arg0, boolean arg1) {
		// Ignore arguments as all series have the same Xx range
		return new Range(starts[0],ends[nitem-1]);
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYRangeInfo#getRangeBounds(java.util.List, org.jfree.data.Range, boolean)
	 */
	@Override
	public Range getRangeBounds(List visibleSeriesKeys, Range xrange, boolean include_int) {
		// start with just global range
		DataRange r = getRange();
		Number min = r.getMin();
		Number max = r.getMax();
		if( min == null && max == null) {
			// auto range
			min=getMinimum();
			max=getMaximum();
		}else if ( min == null ) {
			// auto mimimum cap at fixed max
			min = getMinimum();
			if( min.doubleValue() > max.doubleValue()) {
				min = max;
			}
		}else if ( max == null ) {
			// auto maximum cap at fixed min.
			max = getMaximum();
			if( max.doubleValue() < min.doubleValue()) {
				max = min;
			}
		}
		return new Range( min.doubleValue() ,max.doubleValue());
	}

}