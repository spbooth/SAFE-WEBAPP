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

import java.util.List;

/** A {@link SQLFilter} that also requests a particular order for the results.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface OrderFilter<T> extends BaseSQLFilter<T> {
	/**
	 * override the ORDER BY clause of the factory return null to use Factory
	 * default
	 * 
	 * @return List<OrderClause>
	 */
	public List<OrderClause> OrderBy();
	
}