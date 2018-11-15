//| Copyright - The University of Edinburgh 2018                            |
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

import java.util.Comparator;

/** A {@link Comparator} that compares Strings as well as another type.
 * Strings always sort after the primary type.
 *  
 * @author Stephen Booth
 * @param <T> type of non-string arguements
 *
 */
public class StringObjectComparator<T> implements Comparator<Object> {
	private final Comparator<T> nested;

	/**
	 * @param nested
	 */
	public StringObjectComparator(Comparator<T> nested) {
		super();
		this.nested = nested;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object arg0, Object arg1) {
		if( arg0 instanceof String ) {
			if( arg1 instanceof String) {
				return ((String)arg0).compareTo((String) arg1); // compate strings
			}else {
				// Strings after other objects
				return 1;
			}
		}else {
			if( arg1 instanceof String) {
				return -1;
			}else {
				return nested.compare((T)arg0, (T)arg1);
			}
		}
	}

}
