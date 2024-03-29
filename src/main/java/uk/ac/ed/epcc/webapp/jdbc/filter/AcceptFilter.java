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

import java.util.function.Predicate;

/**
 * A Filter that accepts objects based on a Java method
 * 
 * This is a {@link Predicate} but also implements the {@link BaseFilter} accept method
 * so it can be used wherever a {@link BaseFilter} can be used.
 * As these work directly on the java instance types the additional run-time
 * checking is largely redundant so the default {@link #getTag()} implementation
 * that disables checking is generally fine for {@link AcceptFilter}s
 * 
 * @author spb
 * @param <T> type of object selected
 * 
 */
public interface AcceptFilter<T>  extends BaseFilter<T>, Predicate<T>{
	@Override
	default <X> X acceptVisitor(FilterVisitor<X, T> vis) throws Exception {
		return vis.visitAcceptFilter(this);
	}

}