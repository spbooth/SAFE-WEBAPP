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

import uk.ac.ed.epcc.webapp.Targetted;

/**
 * Base Interface for all Filter types.
 * 
 * There are multiple sub-interfaces that provide different functionality 
 * and but we also need to support composite filters that combine this functionality.
 * 
 * We therefore use the visitor-pattern. Though a filter can implement combinations of the interfaces it has to choose to accept
 * only one of the methods in {@link FilterVisitor}. 
 * 
 * Code that uses filters and the composite types need to be updated if a new visitor target is
 * introduced. We therefore require that filters either implement just one of the sub-types or
 * extend one of the canonical combining types. This is enforced using the 
 * visitor pattern.
 * <p>
 * All filters are parameterised by the type of object they select for
 * usually following the target type of the corresponding factory. Other than for
 * {@link AcceptFilter}s this is purely for the compiler to do consistency checks
 * 
 * @author spb
 * @param <T> target type.
 */
public interface BaseFilter<T> {
	public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception;
	public int hashCode();
	public boolean equals(Object obj) ;
	
	/** Returns the construction tag of the target factory or null.
	 * This is to allow limited run-time checking to detect if filters for two different targets
	 * are being combined. It is however always permissible to return null
	 * 
	 * @return
	 */
	default public String getTag() {
		return null;
	}
}