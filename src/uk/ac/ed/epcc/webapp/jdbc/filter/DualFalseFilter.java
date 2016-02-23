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

/** A {@link DualFilter} that never selects anything.
 * 
 * @author spb
 *
 * @param <T>
 */
public class DualFalseFilter<T> extends AbstractFalseFilter<T> implements DualFilter<T> {

	public DualFalseFilter(Class<? super T> target) {
		super(target);
		
	}

	public boolean accept(T o) {
		return false;
	}

	public <X> X acceptVisitor(FilterVisitor<X,? extends T> vis) throws Exception {
		return vis.visitDualFilter(this);
	}
}