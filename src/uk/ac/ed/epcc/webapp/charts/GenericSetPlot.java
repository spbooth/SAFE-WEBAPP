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

import javax.xml.ws.WebServiceProvider;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * generic version of plot for use where the underlying data-model of the
 * graphics class is radically different
 * 
 * @author spb
 * 
 */

public class GenericSetPlot extends AbstractPeriodSetPlot {
	int nset = 0;

	float data[];

	

	public GenericSetPlot(TimePeriod p,int i) {
		super(p);
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

	@Override
    public void addData(Plot plot) {
    	if( ! (plot instanceof GenericSetPlot)) {
    		throw new ConsistencyError("Unexpected plot type "+plot.getClass().getCanonicalName()+" expecting GenericSetPlot");
    	}
    	GenericSetPlot setplot = (GenericSetPlot) plot;
    	grow(setplot.nset);
    	for(int i =0 ; i< nset ; i++) {
    		add(i,setplot.get(i));
    	}
    	
    }
}