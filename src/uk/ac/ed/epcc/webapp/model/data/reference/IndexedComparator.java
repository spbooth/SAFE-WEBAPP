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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import java.util.Comparator;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.Identified;
/** Comparator for Indexed objects
 * compares length of code in preference to value of string.
 * 
 * @author spb
 *
 */


public class IndexedComparator<I extends Indexed> extends AbstractContexed implements Comparator<IndexedReference<I>> {

	public IndexedComparator(AppContext conn){
		super(conn);
	}
	
	public int compare(IndexedReference<I> i0, IndexedReference<I> i1) {
		
		if( i0 == null || i0.isNull()){
			return 1;
		}
		if( i1 == null || i1.isNull())
		{
			return -1;
		}
		if( i0.getID() == i1.getID()){
			return 0;
		}
		I obj0=i0.getIndexed(conn);
		I obj1=i1.getIndexed(conn);
		if( obj0 instanceof Comparable && obj1 instanceof Comparable){
			return ((Comparable)obj0).compareTo(obj1);
		}
		if( obj0 instanceof Identified && obj1 instanceof Identified){
			return ((Identified)obj0).getIdentifier().compareTo(((Identified)obj1).getIdentifier());
		}
		// Default to sorting by string representation
		return obj0.toString().compareTo(obj1.toString());
	}

}