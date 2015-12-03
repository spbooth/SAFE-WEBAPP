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

import java.util.Date;




public class RegularSplitPeriod extends SplitPeriod {
	private final int nsplit;
	public RegularSplitPeriod(Date start,Date end,int nsplit){
		super(makeSplits(start, end, nsplit));
		this.nsplit=nsplit;
	}
	public static long[] makeSplits(Date start, Date end, int nsplit) {
		long splits[];
		long start_time=start.getTime();
		long end_time=end.getTime();
		splits = new long[nsplit + 1];
		long step = (end_time - start_time) / nsplit;
		assert(step>0L);
		splits[0] = start_time;
		splits[nsplit] = end_time;
		for (int i = 1; i < nsplit; i++) {
			splits[i] = splits[i - 1] + step;
		}
		return splits;
	}
	public int getNsplit() {
		return nsplit;
	}
}