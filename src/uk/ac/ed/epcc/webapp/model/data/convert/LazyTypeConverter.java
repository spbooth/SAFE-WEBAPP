// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.convert;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.LazyObjectCreator;
@uk.ac.ed.epcc.webapp.Version("$Id: LazyTypeConverter.java,v 1.2 2014/09/15 14:30:30 spb Exp $")


public class LazyTypeConverter<T,D,F extends TypeConverter<T,D>> extends LazyObjectCreator<F> implements TypeConverter<T, D>{

	public LazyTypeConverter(AppContext c, F result) {
		super(c, result);
	}

	public LazyTypeConverter(AppContext c, Class<? super F> clazz, String tag) {
		super(c, clazz, tag);
	}

	public Class<? super T> getTarget() {
		return getInner().getTarget();
	}

	public T find(D o) {
		return getInner().find(o);
	}

	public D getIndex(T value) {
		return getInner().getIndex(value);
	}

}