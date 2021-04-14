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

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import uk.ac.ed.epcc.webapp.charts.strategy.QueryMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.SetRangeMapper;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.Period;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** A SplitSetPlot is a {@link PeriodSequencePlot} where the data is split over a series of time periods
 * The time periods are divided into Major/Minor sub-divisions called
 * Catagories and Items.
 * 
 * @author spb
 *
 */
public abstract class SplitSetPlot implements PeriodSequencePlot {
	
	private static final int SHORT_DATE_CUTOFF = 8;

	private static final int LONG_DATE_CUTOFF = 4;
	
	private static final int LOG_THRESH = 8;
	protected String lab[];
	public abstract int getNumCats();

	public abstract int getNumItems();

	public abstract float get(int set, int cat, int item);

	public abstract void set(int nset, int cat, int item, float value);

	public abstract void add(int nset, int cat, int item, float value);

	@Override
	public String[] getLegends() {
		return lab;
	}

	@Override
	public void setLegends(String[] leg) {
		this.lab=leg;
	}
	@Override
	public boolean hasLegends(){
		return lab != null;
	}
	
	@Override
	public void permSets(int new_nset, int[] perm) {
		int nset = perm.length;
		float temp[] = new float[nset];
		
		for (int i = 0; i < getNumCats(); i++) {
			for (int j = 0; j < getNumItems(); j++) {
				for (int k = 0; k < nset; k++) {
					temp[k] = 0.0F;
				}
				for (int k = 0; k < nset; k++) {
					temp[perm[k]] += get(k, i, j);
				}
				for (int k = 0; k < nset; k++) {
					set(k, i, j, temp[k]);
				}
			}
		}
		setSize(new_nset, getNumCats(), getNumItems());
		if( lab != null ){
			String new_lab[] = new String[new_nset];
			for (int i = 0; i < new_nset; i++) {
				new_lab[i]="";
			}
			for (int k = 0; k < nset; k++) {
				
				int new_pos = perm[k];
				if( new_pos >= 0 && new_pos < new_lab.length){
					new_lab[new_pos]=lab[k];
				}
			}
			setLegends(new_lab);
		}
	
	}
	public void setUsingLogSearch(boolean usingLogSearch) {
		this.usingLogSearch = usingLogSearch;
	}
	@Override
	public void scale(float scale) {
		for (int s = 0; s < getNumSets(); s++) {
			for (int i = 0; i < getNumCats(); i++) {
				for (int j = 0; j < getNumItems(); j++) {
					float val = scale * get(s, i, j);
					set(s, i, j, val);
				}
			}
		}
	}
	@Override
	public void setNumSets(int nset){
		setSize(nset,getNumCats(),getNumItems());
	}

	protected abstract void setSize(int new_nset, int numCats, int numItems);

	@Override
	public abstract void doConvertToStacked();

	private String getMajorRange(int i) {
		DateFormat df = getDateFormat();
		return df.format(start_bound[i][0]) + " "
				+ df.format(end_bound[i][getNumItems() - 1]);
	}
	
	/** Does this plot represent a cummulative plot.
	 * 
	 * This return value controls how items are combined in 
	 * {@link #getCatCounts(boolean)}
	 * 
	 * @return
	 */
	public abstract boolean isCummulative();
	
	/**
	 * create a table from a Plot for this Timechart
	 * @param t 
	 * 
	 * @param ds
	 *            Plot to convert
	 * @param lab
	 *            Labels for the catagories
	 * @param start
	 * 			  index of first label in this set
	 * @return number of labels used;
	 */
	@Override
	public Table getTable(String quantity) {
		
		Table result = new Table();
		
		double counts[][] = getCatCounts();

		if (counts.length < 1) {
			// No data to show
			return result;
		}
		if( lab != null){
			int count = lab.length;
			if (count > counts[0].length) {
				count = counts[0].length;
			}
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < getNumCats(); j++) {
					String col = getMajorRange(j);
					result.put(col, lab[i], new Double(counts[j][i]));
				}
			}
		}else{
			int count = counts[0].length;
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < getNumCats(); j++) {
					String col = getMajorRange(j);
					result.put(col, i, new Double(counts[j][i]));
				}
			}
		}
		return result;
	}
	// We assume that the bounds are ordered
	private Date start_bound[][];

	private Date end_bound[][];

	private boolean usingLogSearch = true;
	public boolean isUsingLogSearch() {
		return usingLogSearch;
	}
	
	/**
	 * Sum the contents of the dataset and return a 2D array by Catagory and set
	 * 
	 * @return double[cat][set]
	 */
	public final double[][] getCatCounts() {
		boolean cummulative = isCummulative();
		int nset = getNumSets();
		int ncat = getNumCats();
		double count[][] = new double[ncat][nset];
		// log.debug("other_frac "+other_frac);
		for (int i = 0; i < getNumCats(); i++) {
			if( cummulative){
				int j = getNumItems()-1;
				for (int k = 0; k < nset; k++) {
					float value = get(k, i, j);

					count[i][k] += value;
				}
			}else{
				// Sum the items
				for (int j = 0; j < getNumItems(); j++) {
					for (int k = 0; k < nset; k++) {
						float value = get(k, i, j);

						count[i][k] += value;
					}
				}
			}
		}
		return count;
	}

	@Override
	public final double[] getCounts() {
		int nset = getNumSets();
		double count[] = new double[nset];
		// log.debug("other_frac "+other_frac);
		for (int i = 0; i < getNumCats(); i++) {
			for (int j = 0; j < getNumItems(); j++) {
				for (int k = 0; k < nset; k++) {
					float value = get(k, i, j);

					count[k] += value;
				}
			}
		}
		return count;
	}
	/**
	 * add data from an object to the dataset using strategy transform
	 * 
	 * @param ds
	 * @param t
	 * @param o
	 * @throws Exception 
	 */
	@Override
	public <D> void   addData(SetRangeMapper<D> t, D o)
			throws Exception {
		int set = t.getSet(o);
		int nMajor=getNumCats();
		int nMinor=getNumItems();
		// Note that datasets can grow the number of sets
		if (set < 0) {
			throw new InvalidTransformException("Set out of range");
		}
		if (set >= getNumSets()) {
			setSize(set + 1, getNumCats(), getNumItems());
		}
		
		if (isUsingLogSearch()) {
			boolean seen = false;

			// logarithmic search
			// either half search distance or advance start until start of
			// period located.
			
			int m_start = 0;
			int m_search = nMajor / 2;
			if (m_search > LOG_THRESH) {
				while (m_search > 0 && m_start < nMajor) {
					int m_top = m_start + m_search;
					if (m_top > nMajor - 1) {
						m_top = nMajor - 1;
					}
					if (t.overlapps(o, start_bound[m_start][0],
							end_bound[m_top][nMinor - 1])) {
						m_search = m_search / 2;
					} else {
						m_start += m_search;
					}
				}
			}
			// now iterate linearly from located start
			for (int i = m_start; i < nMajor; i++) {
				if (t.overlapps(o, start_bound[i][0], end_bound[i][nMinor - 1])) {
					// same logarithmic search for minor
					int n_start = 0;
					int n_search = nMinor / 2;
					if (n_search > LOG_THRESH) {
						while (n_search > 0 && n_start < nMinor) {
							int n_top = n_start + n_search;
							if (n_top > nMinor - 1) {
								n_top = nMinor - 1;
							}
							if (t.overlapps(o, start_bound[i][n_start],
									end_bound[i][n_top])) {
								n_search = n_search / 2;
							} else {
								n_start += n_search;
							}
						}
					}
					for (int j = n_start; j < nMinor; j++) {
						if (t.overlapps(o, start_bound[i][j], end_bound[i][j])) {
							float val = t.getOverlapp(o, start_bound[i][j],
									end_bound[i][j]);
							// this won't work with min/max
							set(set, i, j, get(set, i, j) + val);
							seen = true;
						} else {
							// once we have seen the object once then we can
							// return once past it
							if (seen)
								return;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < nMajor; i++) {
				if (t.overlapps(o, start_bound[i][0], end_bound[i][nMinor - 1])) {
					for (int j = 0; j < nMinor; j++) {
						if (t.overlapps(o, start_bound[i][j], end_bound[i][j])) {

							float val = t.getOverlapp(o, start_bound[i][j],
									end_bound[i][j]);
							// this wont work with min max
							set(set, i, j, get(set, i, j) + val);
						}
					}
				}

			}

		}
	}
	@Override
	public <F> boolean addMapData(QueryMapper<F> t, F o)
			throws InvalidTransformException {
		boolean added=false;
		int nMajor=getNumCats();
		int nMinor=getNumItems();
		for (int i = 0; i < nMajor; i++) {
				for (int j = 0; j < nMinor; j++) {
					Map<Integer,Number> dat = t.getOverlapMap(o, start_bound[i][j], end_bound[i][j]);
					for(Integer key : dat.keySet()){
						added=true;
						int set = key.intValue();
						float val = dat.get(key).floatValue();
						
						// Note that datasets can grow the number of sets
						if (set < 0) {
							throw new InvalidTransformException("Set out of range");
						}
						if (set >= getNumSets()) {
							setSize(set + 1, nMajor, nMinor);
						}
						// this won't work with min max if called more then once
						set(set, i, j, get(set, i, j) + val);
					}
				}
		}
		return added;
	}
	 public class PeriodIterator implements Iterator<TimePeriod>{
	        private int i=0;
	        private int j=0;
			@Override
			public boolean hasNext() {
				return i<getNumCats();
			}

			@Override
			public TimePeriod next() {
				TimePeriod p = new Period(start_bound[i][j],end_bound[i][j]);
				j++;
				if( j >= getNumItems()){
					j=0;
					i++;
				}
				return p;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
	    	
	    }
	 @Override
	public Iterator<TimePeriod> getSubPeriods(){
	    	return new PeriodIterator();
	    }
	 public void setSplits(int nset,SplitTimePeriod p, int minor,boolean need_multi) throws InvalidArgument {
			
			
			TimePeriod splits[] = p.getSubPeriods();

			int nMajor = splits.length;
			int nMinor = minor;
			

			if( nMajor < 2 && need_multi){
				// must have at least two major periods
				nMajor=2;
				splits = new TimePeriod[2];
				Date start = p.getStart();
				Date end = p.getEnd();
				long step = (end.getTime()-start.getTime())/2L;
				
				Date mid = new Date(p.getStart().getTime()+step);
				splits[0] = new Period(start,mid);
				splits[1] = new Period(mid,end);
			}
			if ( nMinor < 1) {
				nMinor=1;
			}
			
			start_bound = new Date[nMajor][nMinor];
			end_bound = new Date[nMajor][nMinor];
			// setup region boundaries
			for (int i = 0; i < nMajor; i++) {
				Date start = splits[i].getStart();
				Date end = splits[i].getEnd();
				start_bound[i][0] = start;
				
				end_bound[i][nMinor - 1] = end;
				long step = (end.getTime() - start.getTime()) / nMinor;
				if (step <= 0) {
					throw new InvalidArgument("negative step-size " + step
							+ " for step number " + i);
				}
				for (int j = 1; j < nMinor; j++) {
					start_bound[i][j] = new Date(start_bound[i][j - 1].getTime()
							+ step);
					end_bound[i][j - 1] = start_bound[i][j];
				}
			}
			// now consistency check
			for (int i = 0; i < nMajor; i++) {
				for (int j = 0; j < nMinor; j++) {
					if (!end_bound[i][j].after(start_bound[i][j])) {
						throw new InvalidArgument("step " + i + " " + j
								+ " bounds don't increase");
					}
				}
			}
			setSize(nset, nMajor, nMinor);
		}
	 /** get a set of labels for the major intervals.
	  * 
	  * @return
	  */
	 public String[] getLabels(){
		// Now the labels
					String labels[] = new String[getNumCats()];
					
					
					DateFormat df = getDateFormat();

					for (int i = 0; i < getNumCats(); i++) {
						labels[i] = df.format(start_bound[i][0]);
					}
					return labels;
	 }
	 /**
		 * produce a DateFormat object suitable for formatting the time labels of
		 * the TimeChart
		 * 
		 * @return DateFormat
		 */
		private DateFormat getDateFormat() {
			DateFormat df;
			int format = DateFormat.DEFAULT;
			if (getNumCats() > SHORT_DATE_CUTOFF) {
				format = DateFormat.SHORT;
			}
			if (getNumCats() < LONG_DATE_CUTOFF) {
				format = DateFormat.LONG;
			}
			
			Date start = start_bound[0][0];
			Date end = end_bound[getNumCats()-1][getNumItems()-1];
			if (hourLength(start, end) < 24) {
				df = DateFormat.getTimeInstance(format);
			} else if (hourLength(start, end_bound[0][getNumItems()-1]) < 24) {
				// major split less than a day
				df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
						DateFormat.SHORT,Locale.UK);
			} else {
				df = DateFormat.getDateInstance(format);
			}

			return df;
		}

		/**
		 * number of hours difference between two times
		 * 
		 * @param start
		 *            Date first time
		 * @param end
		 *            Date second time
		 * @return long
		 */
		private long hourLength(Date start, Date end) {
			long diff = end.getTime() - start.getTime();
			// round to nearest
			diff += 1000 * 30 * 60;
			return diff / (1000 * 60 * 60);
		}
		
		/**
		 * rescale a dataset by constant divided by number of miliseconds
		 * 
		 * @param ds
		 * @param scale
		 */
		@Override
		public void rateScale(double scale) {
			for (int i = 0; i < getNumCats(); i++) {
				for (int j = 0; j < getNumItems(); j++) {
					double mul = scale
							/ (end_bound[i][j].getTime() - start_bound[i][j]
									.getTime());
					for (int k = 0; k < getNumSets(); k++) {
						double new_val = (mul * get(k, i, j));
						set(k, i, j, (float) new_val);
					}
				}
			}
		}
		/**
		 * rescale a dataset by constant divided by the value in a different dataset
		 * The normalisation dataset is assumed to have a single Set.
		 * @param ds
		 * @param scale
		 * @param norm
		 */
		@Override
		public void datasetScale(double scale, PeriodSequencePlot nm) {
			SplitSetPlot norm=(SplitSetPlot)nm;
			for (int i = 0; i < getNumCats(); i++) {
				for (int j = 0; j < getNumItems(); j++) {
					float n = norm.get(0, i, j);
					if( n == 0.0 ){
						// don't divide by zero but
						// don't show wrong value either
						for (int k = 0; k < getNumSets(); k++) {
							set(k, i, j, 0.0f);
						}
					}else{
					double mul = scale
							/ n;
					for (int k = 0; k < getNumSets(); k++) {
						double new_val = (mul * get(k, i, j));
						set(k, i, j, (float) new_val);
					}
					}
				}
			}
		}
		/**
		 * rescale one set of a dataset and store result in a different set.
		 * 
		 * @param ds
		 * @param scale
		 * @param src
		 * @param dest
		 */
		@Override
		public void scaleCopy(double scale, int src, int dest) {
			for (int i = 0; i < getNumCats(); i++) {
				for (int j = 0; j < getNumItems(); j++) {
					double new_val = (scale * get(src, i, j));
					set(dest, i, j, (float) new_val);
				}
			}
		}
		
		@Override
		public void scaleCumulative(double scale, double initial[]) {
		
			for (int i = 0; i < getNumCats(); i++) {
				for (int j = 0; j < getNumItems(); j++) {
					for (int k = 0; k < getNumSets(); k++) {
						initial[k]  += get(k, i, j);
						set(k, i, j, (float) (initial[k]*scale));
					}
				}
			}
		}
}