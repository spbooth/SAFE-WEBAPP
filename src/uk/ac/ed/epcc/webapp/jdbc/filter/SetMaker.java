// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

public abstract class SetMaker<F, O> extends FilterFinder<F, Set<O>> {

	public SetMaker(AppContext c,Class<? super F> target) {
		super(c, target,true);
	}

	public Set<O> makeSet(SQLFilter<F> f) throws DataException {
		setFilter(f);
		Set<O> set = make();
		if (set == null) {
			set = new HashSet<O>();
		}
		return set;
		
	}

}