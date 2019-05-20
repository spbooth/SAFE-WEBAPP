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
package uk.ac.ed.epcc.webapp.charts;

import java.util.Date;
import java.util.Map;

import uk.ac.ed.epcc.webapp.charts.strategy.QueryMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.SetRangeMapper;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * @author spb
 *
 */
public abstract class AbstractPeriodSetPlot extends AbstractSingleValueSetPlot implements
		PeriodSetPlot {

	public AbstractPeriodSetPlot(TimePeriod period) {
		super();
		this.period = period;
	}

	final TimePeriod period;
	
	
	public TimePeriod getPeriod() {
		return period;
	}


	public <D> void addData(SetRangeMapper<D> t, D r) throws Exception {
		TimePeriod p = getPeriod();
		Date start = p.getStart();
		Date end = p.getEnd();
		if (t.overlapps(r, start, end)) {
			float val = t.getOverlapp(r, start, end);
			int set = t.getSet(r);
			if (set >= getNumSets()) {
				setNumSets(set + 1);
			}
			add(set,  val);
	
		}
	}

	public <F> boolean addMapData(QueryMapper<F> t, F fac)
			throws InvalidTransformException {
				TimePeriod p = getPeriod();
				Date start = p.getStart();
				Date end = p.getEnd();
				boolean added=false;
				Map<Integer,Number> dat = t.getOverlapMap(fac, start, end);
				
				for(Integer i : dat.keySet()){
					added=true;
					int set = i.intValue();
					Number n = dat.get(i);
					if( n != null){
						float val = n.floatValue();
						if (set >= getNumSets()) {
							setNumSets(set + 1);
						}
						set(set, get(set) + val);
					}
				}
				return added;
			}
	

	

}