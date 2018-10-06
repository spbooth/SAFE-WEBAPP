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


/** Interface for filters that add an explicit join clause. 
 * The intention here is to filter entries from the primary tables based on the data it
 * points to
 * 
 * The filter clauses on the remote object have to be done entirely in SQL because 
 * we are only returning objects from the 
 * primary table. Any condition clause will need to qualify the field names 
 * 
 * This is added in to the source clause by the {@link FilterReader}.
 * @author spb
 * @param <T> type of object selected
 *
 */
public interface JoinFilter<T> extends BaseFilter<T>, MultiTableFilter {
	
   /** Join clause to add to query
    * 
    * @return String
    */
   public String getJoin();
}