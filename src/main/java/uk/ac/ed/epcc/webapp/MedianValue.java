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
package uk.ac.ed.epcc.webapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A simple class to calculate median values.
 * 
 * 
 * Thsi is NOT immuntable
 * Internally the values are held as a list of doubles so the results are subject to rounding to double
 * @author Stephen Booth
 *
 */
public class MedianValue extends Number {

	private  List<Double> values = new ArrayList<Double>();
	private boolean sorted=true;
	
	public MedianValue(Number ...numbers) {
		for(Number n : numbers) {
			add(n);
		}
	}
	
	private double getMedian() {
		if( values.isEmpty()) {
			return 0.0;
		}
		if( ! sorted ) {
			Collections.sort(values);
			sorted=true;
		}
		int n = values.size();
		int low = (n-1)/2;
		int high = n/2;
		if( low == high) {
			return values.get(low);
		}else {
			return (values.get(low)+values.get(high))/2.0;
		}
	}
	/** Add a {@link Number} to the internal dataset.
	 * Other {@link MedianValue} objects are supported in which case the datasets are combined
	 * 
	 * @param n
	 */
	public void add(Number n) {
		if( n instanceof MedianValue) {
			values.addAll(((MedianValue)n).values);
		}else {
			values.add(n.doubleValue());
		}
		sorted=false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return getMedian();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return (float) getMedian();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return (int) getMedian();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return (long) getMedian();
	}

}
