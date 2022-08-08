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
package uk.ac.ed.epcc.webapp.model.data.reference;

import java.util.Comparator;

/** A Comparator that sorts {@link IndexedReference} objects by numerical order.
 * @author spb
 *
 */

public class ReferenceComparator implements Comparator<IndexedReference> {

	/**
	 * 
	 */
	public ReferenceComparator() {
		
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(IndexedReference o1, IndexedReference o2) {
		if( o1.getFactoryClass() != o2.getFactoryClass()){
			// different factories 
			return o1.getFactoryClass().getCanonicalName().compareTo(o2.getFactoryClass().getCanonicalName());
		}
		if( o1.getTag() != null && o2.getTag() != null && ! o1.getTag().equals(o2.getTag())){
			// different tags.
			return o1.getTag().compareTo(o2.getTag());
		}
		if( o1.isNull()){
			if( o2.isNull()){
				return 0;
			}
			return 1;
		}
		if( o2.isNull()){
			return -1;
		}
		return o1.getID()-o2.getID();
	}

}