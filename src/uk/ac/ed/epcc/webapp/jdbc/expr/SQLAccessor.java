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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.model.data.FieldValue;

/** A combination of {@link Accessor} and {@link SQLValue}. 
 * In general the two behaviours are distinct though related.
 * This interface is for leaf level entities (database fields) where we
 * always need to support both behaviours so it is sensible to implement them
 * in the same object. 
 * @see FieldValue
 * @author spb
 *
 * @param <T> type of result value
 * @param <R> target object
 */
public interface SQLAccessor<T,R> extends Accessor<T,R>, GroupingSQLValue<T> {

}