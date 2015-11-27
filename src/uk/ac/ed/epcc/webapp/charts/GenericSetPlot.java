// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

/**
 * generic version of plot for use where the underlying data-model of the
 * graphics class is radically different
 * 
 * @author spb
 * 
 */

public class GenericSetPlot extends AbstractSingleValueSetPlot {
	int nset = 0;

	float data[];

	

	public GenericSetPlot(int i) {
		setNumSets(i);
	}

	
	public float get(int i) {
		if (i >= nset ) {
			return 0;
		}
		return data[i];
	}

	
	public int getNumSets() {
		return nset;
	}

	private void grow(int i) {
		if (nset > i)
			i = nset;
		setNumSets(i);

	}

	public void set(int i, float f) {
		grow(i + 1);
		data[i] = f;
	}

	public void setNumSets(int new_nset) {
		if (new_nset == nset ) {
			return;
		}
		float new_data[] = new float[new_nset];
		int ns;
		if (new_nset > nset) {
			ns = nset;
		} else {
			ns = new_nset;
		}
		
		for (int i = 0; i < ns; i++) {
					new_data[i] = data[i];
		}
		data = new_data;
		nset = new_nset;
	}

	public void add(int nset, float value) {
		set(nset,get(nset)+value);
		
	}


}