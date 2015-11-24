// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.convert.LazyTypeConverter;
@uk.ac.ed.epcc.webapp.Version("$Id: LazyIndexProducer.java,v 1.6 2015/06/27 09:39:31 spb Exp $")


public class LazyIndexProducer<A extends Indexed,F extends IndexedProducer<A>> extends LazyTypeConverter<A, Number, F> implements IndexedProducer<A>{

	public LazyIndexProducer(AppContext c, F result) {
		super(c, result);
	}

	public LazyIndexProducer(AppContext c, Class<? super F> clazz, String tag) {
		super(c, clazz, tag);
	}

	public A find(int id) throws DataException {
		return getInner().find(id);
	}

	public IndexedReference<A> makeReference(A obj) {
		return getInner().makeReference(obj);
	}

	public IndexedReference<A> makeReference(int id) {
		return getInner().makeReference(id);
	}

	public boolean isMyReference(IndexedReference ref) {
		return getInner().isMyReference(ref);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#getID(uk.ac.ed.epcc.webapp.Indexed)
	 */
	@Override
	public String getID(A obj) {
		return getInner().getID(obj);
	}

	
}