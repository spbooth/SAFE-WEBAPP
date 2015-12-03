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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.Contexed;


/** Interface implemented by factory classes that support table transitions.
 * Most of the logic is delegated to TableTransitionRegistry in order to allow
 * the implementation to be by aggregation rather than by inheritance.
 * Inheritance could still be used by having the factory implement both interfaces
 * and return a self reference as the registry.
 * @author spb
 *
 */
public interface TableTransitionTarget extends Contexed {
	/** get TableTransitionRegisty
	 * 
	 * @return TableTransitionRegistry
	 */
    public TableTransitionRegistry getTableTransitionRegistry();
	/** Table name needed to look up this class in the Configuration Properties
	 * 
	 * @return String
	 */
	public String getTableTransitionID();

	
}