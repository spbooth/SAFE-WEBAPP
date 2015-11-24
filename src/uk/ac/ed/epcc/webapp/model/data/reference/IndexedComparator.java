// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import java.util.Comparator;

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
@uk.ac.ed.epcc.webapp.Version("$Id: IndexedComparator.java,v 1.2 2015/03/02 10:49:34 spb Exp $")

public class IndexedComparator<I extends Indexed> implements Comparator<IndexedReference<I>> , Contexed{

	private final AppContext conn;
	public IndexedComparator(AppContext conn){
		this.conn=conn;
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		
		return conn;
	}

	

}