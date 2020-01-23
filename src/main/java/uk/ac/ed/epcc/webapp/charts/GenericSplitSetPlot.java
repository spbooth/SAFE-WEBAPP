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

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/**
 * generic version of plot for use where the underlying datamodel of the
 * graphics class is radically different
 * 
 * @author spb
 * 
 */

public class GenericSplitSetPlot extends SplitSetPlot {
	int nset = 0, ncat = 0, nitem = 0;

	float data[][][];
	private final TimeChartData chart_data;

	public GenericSplitSetPlot(TimeChartData chart){
		chart_data=chart;
	}
	public GenericSplitSetPlot(TimeChartData chart,int i, int j, int k) {
		this(chart);
		setSize(i, j, k);
	}

	@Override
	public void doConvertToStacked() {
		// assumes sets are plotted front to back
		for (int i = 1; i < nset; i++) {
			for (int j = 0; j < ncat; j++) {
				for (int k = 0; k < nitem; k++) {
					data[i][j][k] += data[i - 1][j][k];
				}
			}
		}
		notifyChange();
	}
	public float getMaximum() {
		if( nset == 0 || ncat == 0 || nitem == 0){
			// There is no data
			return 0.0F;
		}
		float max = data[0][0][0];
		// assumes sets are plotted front to back
		for (int i = 0; i < nset; i++) {
			for (int j = 0; j < ncat; j++) {
				for (int k = 0; k < nitem; k++) {
					if( data[i][j][k] > max ){
						max=data[i][j][k];
					}
				}
			}
		}
		return max;

	}

	@Override
	public final float get(int i, int j, int k) {
		if (i >= nset || j >= ncat || k >= nitem) {
			return 0;
		}
		return data[i][j][k];
	}

	@Override
	public final int getNumCats() {
		return ncat;
	}

	@Override
	public final int getNumItems() {
		return nitem;
	}

	public final int getNumSets() {
		return nset;
	}

	private void grow(int i, int j, int k) {
		if (nset > i)
			i = nset;
		if (ncat > j)
			j = ncat;
		if (nitem > k)
			k = nitem;
		setSize(i, j, k);

	}

	

	@Override
	public final void set(int i, int j, int k, float f) {
		grow(i + 1, j + 1, k + 1);
		data[i][j][k] = f;
		notifyChange();
	}

	@Override
	protected void setSize(int new_nset, int new_numCats, int new_numItems) {
		
		if (new_nset == nset && new_numCats == ncat
				&& new_numItems == nitem) {
			return;
		}
		
		int ns, nc, ni;
		if (new_nset > nset) {
			ns = nset;
		} else {
			ns = new_nset;
		}
		if (new_numCats > ncat) {
			nc = ncat;
		} else {
			nc = new_numCats;
		}
		if (new_numItems > nitem) {
			ni = nitem;
		} else {
			ni = new_numItems;
		}
		float new_data[][][]; 
		if( new_numItems == nitem ) {
			if( new_numCats == ncat) {
				new_data = new float[new_nset][][];
				for (int i = 0; i < ns; i++) {
					new_data[i] = data[i];
				}
				for(int i=ns ; i< new_nset ; i++) {
					new_data[i] = new float[new_numCats][new_numItems];
				}
			}else {
				new_data=new float[new_nset][new_numCats][];
				for (int i = 0; i < ns; i++) {
					for (int j = 0; j < nc; j++) {
						new_data[i][j] = data[i][j];
					}
					for(int j=nc; j< new_numCats; j++) {
						new_data[i][j] = new float[new_numItems];
					}
				}
				for(int i=ns ; i< new_nset ; i++) {
					new_data[i] = new float[new_numCats][new_numItems];
				}
			}
		}else {
			new_data = new float[new_nset][new_numCats][new_numItems];
			for (int i = 0; i < ns; i++) {
				for (int j = 0; j < nc; j++) {
					for (int k = 0; k < ni; k++) {
						new_data[i][j][k] = data[i][j][k];
					}
				}
			}
		}
		data = new_data;
		nset = new_nset;
		ncat = new_numCats;
		nitem = new_numItems;

	}

	@Override
	public final void add(int nset, int cat, int item, float value) {
		set(nset,cat,item,get(nset,cat,item)+value);
		
	}

	protected void notifyChange() {
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.SplitSetPlot#isCummulative()
	 */
	@Override
	public boolean isCummulative() {
		return chart_data.isCumulative();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.Plot#addData(uk.ac.ed.epcc.webapp.charts.Plot)
	 */
	@Override
	public void addData(Plot plot) {
		if( plot == null) {
			return;
		}
		if( !( plot instanceof GenericSplitSetPlot)) {
			throw new ConsistencyError("Unexpected plot type "+plot.getClass().getCanonicalName()+" expecting GenericSplitSetPlot");
		}
		GenericSplitSetPlot p = (GenericSplitSetPlot)plot;
		grow(p.getNumSets(),p.getNumCats(),p.getNumItems());
		for(int i=0 ; i< p.getNumSets(); i++) {
			for(int j=0 ; j < p.getNumCats(); j++) {
				for(int k=0; k< p.getNumItems(); k++) {
					add(i,j,k,p.get(i, j, k));
				}
			}
		}
	
		
	}
	@Override
	public void permSets(int new_nset, int[] perm) {
		
		float temp[][][] = new float[new_nset][][];
		for (int k = 0; k < nset; k++) {
			int dest = perm[k];
			// out of range dest are a supressed "others" category
			// these can be supressed when value is zero
			if( dest < new_nset) {
				if( temp[dest] == null) {
					temp[dest] = data[k]; 
				}else {
					for (int i = 0; i < getNumCats(); i++) {
						for (int j = 0; j < getNumItems(); j++) {
							temp[dest][i][j] += data[k][i][j];
						}
					}
				}
			}
		}
		
		
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
		nset = new_nset;
		
		
		data = temp;
	
		
	}
	
}