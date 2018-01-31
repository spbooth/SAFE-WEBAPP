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
}