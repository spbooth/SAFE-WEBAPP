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
package uk.ac.ed.epcc.webapp.time;

import java.util.Calendar;




public class CalendarFieldSplitPeriod extends SplitPeriod {
	private final int field;
	private final int count;
	private final int nsplit;
	private final Calendar cal;
	/** Make a new period consisting of nsplit blocks each
	 * of count calendar units.
	 * 
	 * @param start the start time
	 * @param field the field of the time interval
	 * @param count multiple of field in each block
	 * @param nsplit number of blocks
	 */
	public CalendarFieldSplitPeriod(Calendar start, int field, int count, int nsplit) {
		super(makeSplits(start,field,count,nsplit));
		this.cal=start;
		this.field=field;
		this.count=count;
		this.nsplit=nsplit;
	}

	/**
	 * Instantiates a new split periodwhich start form the 'start' time, 
	 * and has 'count' splits of 'field' type. 
	 * 
	 * @param start the start time
	 * @param field the field of the time interval
	 * @param count multiple of field in each block
	 * @param nsplit number of blocks
	 * @return array of splits
	 */
	public static long[] makeSplits(Calendar start, int field, int count, int nsplit) {
		long splits[];
		if( nsplit < 1 ){
			nsplit=1;
		}
		if( count < 1){
			count=1;
		}
		
			Calendar tmp = Calendar.getInstance();
			splits = new long[nsplit + 1];
			tmp.setTime(start.getTime());
			for (int i = 0; i < nsplit + 1; i++) {
				splits[i] = tmp.getTime().getTime();
				tmp.add(field, count);
			}

		return splits;
	}

	public int getField() {
		return field;
	}

	public int getCount() {
		return count;
	}

	public int getNsplit() {
		return nsplit;
	}
	public Calendar getCalStart(){
		return cal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cal == null) ? 0 : cal.hashCode());
		result = prime * result + count;
		result = prime * result + field;
		result = prime * result + nsplit;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalendarFieldSplitPeriod other = (CalendarFieldSplitPeriod) obj;
		if (cal == null) {
			if (other.cal != null)
				return false;
		} else if (!cal.equals(other.cal))
			return false;
		if (count != other.count)
			return false;
		if (field != other.field)
			return false;
		if (nsplit != other.nsplit)
			return false;
		return true;
	}
}