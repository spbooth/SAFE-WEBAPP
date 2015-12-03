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

import uk.ac.ed.epcc.webapp.content.Table;

/** This is a abstract superclass for {@link SingleValueSetPlot} that implements the methods from {@link SetPlot} via the additional methods in {@link SingleValueSetPlot}.
 * 
 * @author spb
 *
 */
public abstract class AbstractSingleValueSetPlot implements SingleValueSetPlot {


	protected String labels[];
	

	public Table getTable(String quantity) {
		String data_col=quantity; 
		if( data_col == null ){
			data_col="Data";
		}
		Table t = new Table();
		if( labels != null ){
			for(int i=0; i< labels.length ; i++){
				t.put(data_col, labels[i], get(i));
			}
		}else{
			for(int i=0; i< getNumSets() ; i++){
				t.put(data_col, i, get(i));
			}
		}
		t.addPercentCol(data_col, "Percentage");
		return t;
	}

	public final void permSets(int new_nset, int[] perm) {
		int nset = perm.length;
		float temp[] = new float[nset];
       
		for (int k = 0; k < nset; k++) {
			temp[k] = 0.0F;
		
		}
		for (int k = 0; k < nset; k++) {
			temp[perm[k]] += get(k);
		
		}
		for (int k = 0; k < nset; k++) {
			set(k, temp[k]);
		}
		setNumSets(new_nset);
		String old_legs[]=getLegends();
		if( old_legs != null ){
			String new_labels[] = new String[new_nset];
			for (int k = 0; k < new_nset; k++) {
				new_labels[k]="";
			}
			for (int k = 0; k < nset; k++) {
				
				int new_pos = perm[k];
				if( new_pos < new_nset){
					new_labels[new_pos] = old_legs[k];
				}
			}
			setLegends(new_labels);
		}
	}

	public void scale(float scale) {
		for (int s = 0; s < getNumSets(); s++) {
			float val = scale * get(s);
			set(s, val);
		}
	}

	public final double[] getCounts() {
		int nset = getNumSets();
		double count[] = new double[nset];

		for (int k = 0; k < nset; k++) {
			count[k] = get(k);
		}
		return count;
	}

	public String[] getLegends() {
		return labels;
	}

	public void setLegends(String[] leg) {
		labels=leg;
	}
	public boolean hasLegends(){
		return labels != null;
	}
}