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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.time.Period;
/** {@link PeriodChart}s that represent a set of values such as 
 * a pie-chart of bar-chart.
 * 
 * Unlike {@link TimeChart}s only a single dataset is plotted. This defaults to the
 * last created {@link Plot} but can be reset using the {@link #setPlot(PeriodPlot)} method.
 * 
 * 
 * @author spb
 *
 * @param <P> type of plot
 */
public abstract class SetPeriodChart<P extends PeriodPlot> extends PeriodChart<P> {

	protected SetPeriodChart(AppContext c) {
		super(c);
	}

	

	@Override
	public Table getTable() {
		Table t = new Table();
		
		P ds = (P) getPlot();
		if( ds != null ){
			t.add(ds.getTable(getChartData().getQuantityName()));
			if( ds.hasLegends()){
				t.setKeyName(getLegendName());
			}
		}
		return t;

	}

	public abstract  P getPlot();
	
    public abstract void setPlot(P ds);
}