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


import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Combining filter that is pure SQL.
 * Any attempt to add an {@link AcceptFilter} will
 * throw an exception.
 * * 
 * @author spb
 * @param <T> type of object selected
 *
 */
public abstract class BaseSQLCombineFilter<T> extends BaseCombineFilter<T> implements
		SQLFilter<T>{


	/**
	 * @param target
	 */
	protected BaseSQLCombineFilter(Class<? super T> target) {
		super(target);
	}

	
	
	@Override
	protected final void addAccept(AcceptFilter<? super T> filter) throws ConsistencyError {
		throw new ConsistencyError("Adding AcceptFilter to SQLFilter");
	}
	
	public final BaseSQLCombineFilter<T> addFilter(SQLFilter<? super T> fil){
		assert( fil != this ); // probably a typo
		return (BaseSQLCombineFilter<T>) super.add(fil,true);
	}
	public final void accept(T o) {

	}
	public final <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		if( useBinary(false)){
			return vis.visitBinaryFilter(this);
		}
		return vis.visitSQLCombineFilter(this);
	}

}