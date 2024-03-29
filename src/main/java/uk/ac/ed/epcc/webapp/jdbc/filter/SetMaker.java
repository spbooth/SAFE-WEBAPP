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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
/**
 * 
 * @author Stephen Booth
 *
 * @param <F> type of filter
 * @param <O> result type
 */
public abstract class SetMaker<F, O> extends FilterFinder<F, Set<O>> {

	public SetMaker(AppContext c,String tag) {
		super(c, tag,true);
	}

	public Set<O> makeSet(SQLFilter<F> f) throws DataException {
		setFilter(f);
		Set<O> set = make();
		if (set == null) {
			set = new HashSet<>();
		}
		return set;
		
	}

}