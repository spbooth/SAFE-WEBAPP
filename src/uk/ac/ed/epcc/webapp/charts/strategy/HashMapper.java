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
package uk.ac.ed.epcc.webapp.charts.strategy;

import java.util.Date;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.AppContext;


/**
	 * HashMapper base class for LabeledTransform Mappers that builds the label
	 * vector and the mappings as data is seen. Sets are ordered according to
	 * the order the first data from that set was seen.
	 * 
	 * The Rangemapper interface is provided by composition
	 * 
	 * @author spb
	 * @param <T> type of object being mapped
	 * 
	 */


	public class HashMapper<T> implements LabelledSetRangeMapper<T> {

		RangeMapper<T> rm;
		HashLabeller<T,Number> hl;

		
		AppContext conn;

	

		public HashMapper(AppContext conn, RangeMapper<T> r, HashLabeller<T,Number> l) {
			this.conn = conn;
			
			rm = r;
			hl=l;
		}

	

		public AppContext getContext() {
			return conn;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.hpcx.report.TimeChart.LabeledTransform#getLabels()
		 */
		public Vector<String> getLabels() {

			return hl.getLabels();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.charts.Chart.RangeMapper#getOverlapp(java.lang.Object,
		 *      java.util.Date, java.util.Date)
		 */
		public float getOverlapp(T o, Date start, Date end) throws Exception {

			return rm.getOverlapp(o, start, end);
		}

		public int getSet(T o) {
            return hl.getSet(o);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.hpcx.report.TimeChart.LabeledTransform#nSets()
		 */
		public int nSets() {

			return hl.nSets();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.charts.Chart.RangeMapper#overlapps(java.lang.Object,
		 *      java.util.Date, java.util.Date)
		 */
		public boolean overlapps(T o, Date start, Date end) {

			return rm.overlapps(o, start, end);
		}
	}