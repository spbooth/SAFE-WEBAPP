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

import java.util.Comparator;

/**
 * ListSorter sorts an array of indecies according to the values in an array
 * 
 * @author spb
 * 
 */
public class ListSorter implements Comparator<Number> {
	double data[];

	/**
	 * @param dat
	 * 
	 */
	public ListSorter(double dat[]) {
		super();
		data = dat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Number o1, Number o2) {
		int i =  o1.intValue();
		int j =  o2.intValue();
		double res = data[i] - data[j];
		if (res < 0)
			return -1;
		if (res > 0)
			return 1;
		return i - j;
	}

}