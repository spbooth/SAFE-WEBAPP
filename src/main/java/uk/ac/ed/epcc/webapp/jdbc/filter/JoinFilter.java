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

import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.filter.LinkClause;

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
	
/** add join clause to add to query. Each {@link Repository} should only be joined to once. 
 * A table may be joined to multiple times if via a {@link Repository} with an alias.
 * If the {@link Repository} being joined to is already present the <b>join_clause</b> is left unchanged but the
 * {@link LinkClause} is added to the <b>additions</b> list.
 * 
 * 
 * @param tables Set of {@link Repository}s in join (updated)
 * @param join_clause Query join clause (modified)
 * @param additions  Additional {@link LinkClause}s to add to select
    */
   public void addJoin(Set<Repository> tables, StringBuilder join_clause, Set<LinkClause> additions);
}