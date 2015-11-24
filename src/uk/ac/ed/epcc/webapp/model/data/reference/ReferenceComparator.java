// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data.reference;

import java.util.Comparator;

/** A Comparator that sorts {@link IndexedReference} objects by numerical order.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class ReferenceComparator implements Comparator<IndexedReference> {

	/**
	 * 
	 */
	public ReferenceComparator() {
		// TODO Auto-generated constructor stub
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
