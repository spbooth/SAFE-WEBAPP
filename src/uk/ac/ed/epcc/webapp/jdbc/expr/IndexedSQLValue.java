//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/** A {@link SQLValue} that generates a {@link IndexedReference} to a remote table.
 * 
 * This also supports the necessary methods to join through to the remote table. 
 * @see DerefSQLExpression
 * @see RemoteSQLValue
 * @author spb
 * @param <T> Type of owning/home table.
 * @param <I> Type of remote table
 */
public interface IndexedSQLValue<T extends DataObject,I extends DataObject> extends SQLValue<IndexedReference<I>>,
FilterProvider<T,IndexedReference<I>> {
	/** Create a filter for the home table out of a a filter on the target object.
	 * 
	 * This adds any necessary join filters. If fil is null it only returns the necessary join filter.
	 * @param fil
	 * @return {@link SQLFilter}
	 * @throws CannotFilterException
	 */
	public SQLFilter<T> getSQLFilter(SQLFilter<I> fil) throws CannotFilterException;
	
	/** get the remote {@link DataObjectFactory}
	 * 
	 * @return {@link DataObjectFactory}
	 * @throws Exception
	 */
	public DataObjectFactory<I> getFactory() throws Exception;
}
