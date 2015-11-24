// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.util.HashSet;
import java.util.Set;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.IntervalXYDataset;

import uk.ac.ed.epcc.webapp.charts.GenericSplitSetPlot;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** A {@link GenericSplitSetPlot} that implements the appropriate JFrrChart 
 * interfaces. We implement {@link IntervalXYDataset} to represent the actual time periods
 * so the data can be plotted as bar-chart and return the middle of the period as the data point.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TimeChartDataSet.java,v 1.3 2014/09/15 14:30:13 spb Exp $")
public class TimeChartDataSet extends GenericSplitSetPlot implements IntervalXYDataset {

	DatasetGroup group;
	Set<DatasetChangeListener> listeners;
	double times[];
	double starts[];
	double ends[];
	int dataset_id;
	
	/**
	 * @throws InvalidArgument 
	 * 
	 */
	public TimeChartDataSet(int nset, SplitTimePeriod period, int nsplit) throws InvalidArgument {
		super();
		setSplits(nset, period, nsplit,false);
		int nitem = period.getNsplit()*nsplit;
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
	public int getSeriesCount() {
		return getNumSets();
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#getSeriesKey(int)
	 */
	public Comparable getSeriesKey(int arg0) {
		if( lab != null ){
			return lab[arg0];
		}
		return Integer.toString(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#indexOf(java.lang.Comparable)
	 */
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
	public void addChangeListener(DatasetChangeListener arg0) {
		if( listeners == null){
			listeners=new HashSet<DatasetChangeListener>();
		}
		listeners.add(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#getGroup()
	 */
	public DatasetGroup getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#removeChangeListener(org.jfree.data.general.DatasetChangeListener)
	 */
	public void removeChangeListener(DatasetChangeListener arg0) {
		if(listeners != null){
			listeners.add(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.Dataset#setGroup(org.jfree.data.general.DatasetGroup)
	 */
	public void setGroup(DatasetGroup arg0) {
		this.group=arg0;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getDomainOrder()
	 */
	public DomainOrder getDomainOrder() {
		return DomainOrder.ASCENDING;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getItemCount(int)
	 */
	public int getItemCount(int arg0) {
		return getNumCats()*getNumItems();
	}
	

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getX(int, int)
	 */
	public Number getX(int set, int item) {
		return times[item];
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getXValue(int, int)
	 */
	public double getXValue(int set, int item) {
		return times[item];
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getY(int, int)
	 */
	public Number getY(int set, int item) {
		return get(set,item/getNumItems(),item%getNumItems());
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getYValue(int, int)
	 */
	public double getYValue(int set, int item) {
		return get(set,item/getNumItems(),item%getNumItems());
	}


	@Override
	public void set(int i, int j, int k, float f) {
		super.set(i, j, k, f);
		notifyChange();
	}


	private void notifyChange() {
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
	public Number getEndX(int arg0, int arg1){
		return ends[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndXValue(int, int)
	 */
	public double getEndXValue(int arg0, int arg1) {
		return ends[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndY(int, int)
	 */
	public Number getEndY(int arg0, int arg1) {
		return null;
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getEndYValue(int, int)
	 */
	public double getEndYValue(int arg0, int arg1) {
		return getYValue(arg0, arg1);
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartX(int, int)
	 */
	public Number getStartX(int arg0, int arg1) {
		return starts[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartXValue(int, int)
	 */
	public double getStartXValue(int arg0, int arg1) {
		return starts[arg1];
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartY(int, int)
	 */
	public Number getStartY(int arg0, int arg1) {
		return null;
	}


	/* (non-Javadoc)
	 * @see org.jfree.data.xy.IntervalXYDataset#getStartYValue(int, int)
	 */
	public double getStartYValue(int arg0, int arg1) {
		return getYValue(arg0, arg1);
	}

}
