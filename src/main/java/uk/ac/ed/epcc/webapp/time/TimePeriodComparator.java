//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.time;

import java.util.Comparator;

/** A {@link Comparator} for {@link TimePeriod}s
 * 
 * These are primarily ordered by start time, though if the start
 * times are the same the end date will distinguish.
 * 
 * @author Stephen Booth
 *
 */
public class TimePeriodComparator implements Comparator<TimePeriod> {

	private final boolean reverse;
	/**
	 * 
	 */
	public TimePeriodComparator() {
		reverse=false;
	}
	
	public TimePeriodComparator(boolean reverse) {
		this.reverse=reverse;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TimePeriod arg0, TimePeriod arg1) {
		int s = arg0.getStart().compareTo(arg1.getStart());
		
		if( s != 0) {
			return reverse ? -s : s;  // Order by start date first
		}
		int e = arg0.getEnd().compareTo(arg1.getEnd());
		return reverse ? -e : e;  // Tie-break with end.
	}

}
