// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.chart2D;

import uk.ac.ed.epcc.webapp.charts.SplitSetPlot;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
@uk.ac.ed.epcc.webapp.Version("$Id: Chart2DSplitSetPlot.java,v 1.8 2014/09/15 14:30:13 spb Exp $")


public class Chart2DSplitSetPlot extends SplitSetPlot {
	

	private net.sourceforge.chart2d.Dataset ds;
	private final Chart2DTimeChartData chart_data;

	public Chart2DSplitSetPlot(Chart2DTimeChartData chart_data,int nset,SplitTimePeriod p,int nminor,boolean need_multi) throws InvalidArgument {
		ds = new net.sourceforge.chart2d.Dataset(nset,p.getNsplit(),nminor);
		setSplits(nset, p, nminor,need_multi);
		this.chart_data=chart_data;
	}

	@Override
	public void doConvertToStacked() {
		ds.doConvertToStacked();
   
	}

	@Override
	public float get(int k, int i, int j) {
		return ds.get(k, i, j);
	}

	protected net.sourceforge.chart2d.Dataset getDataset() {

		return ds;
	}

	@Override
	public int getNumCats() {
		return ds.getNumCats();
	}

	@Override
	public int getNumItems() {
		return ds.getNumItems();
	}

	public int getNumSets() {

		return ds.getNumSets();
	}

	
	@Override
	public void set(int k, int i, int j, float f) {
		ds.set(k, i, j, f);

	}

	@Override
	protected void setSize(int new_nset, int numCats, int numItems) {
		if( new_nset < 1){
			// The dataset does not remember numCats if set nset=0
			new_nset=1;
		}
		ds.setSize(new_nset, numCats, numItems);

	}

	@Override
	public void add(int nset, int cat, int item, float value) {
		float val = get(nset,cat,item) + value;
		set(nset,cat,item,val);
	}

	@Override
	public void setLegends(String[] leg) {
		super.setLegends(leg);
		chart_data.updateLegends();
	}
}