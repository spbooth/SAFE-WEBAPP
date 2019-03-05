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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Stephen Booth
 *
 */
public class DistinctCount<D> extends Number {

	private Set<D> values;
	/**
	 * 
	 */
	public DistinctCount(Set<D> data) {
		values=Collections.unmodifiableSet(data);
	}
	

	public Set<D> getSet(){
		return values;
	}
	/* (non-Javadoc)
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return intValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return intValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return values.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return intValue();
	}
	
	public static <D> DistinctCount<D> make(D obj){
		HashSet<D> v = new HashSet<>();
		v.add(obj);
		return new DistinctCount<>(v);
	}

	public static <D> DistinctCount<D> add(DistinctCount<D> a, DistinctCount<D> b){
		if( a == null) {
			return b;
		}
		if( b == null ) {
			return a;
		}
		Set<D> v = new HashSet<>();
		v.addAll(a.getSet());
		v.addAll(b.getSet());
		return new DistinctCount<>(v);
	}
 }
