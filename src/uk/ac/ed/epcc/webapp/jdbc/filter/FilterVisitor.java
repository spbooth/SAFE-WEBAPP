//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

/** A visitor for {@link BaseFilter}. 
 * 
 * We use the visitor pattern as this makes the dependencies 
 * between the type hierarchy and the code that uses them explicit.
 * If a new sub-type of {@link BaseFilter} is introduced it has to either extend an existing sub-type
 * and function only as that sub-type
 * and/or have a new method added to the {link FilterVisitor} interface. This in turn ensures that
 * all visitor logic needs to be updated to take account of the new type. 
 * <p>
 * Combinations of multiple sub-types should have an explicit interface and visit method because the visitor has to 
 * specify how to combine the different behaviours.
 * @author spb
 *
 * @param <X> return type of visitor
 * @param <T> type of filter.
 */
public interface FilterVisitor<X,T> {
	/** process a pure {@link PatternFilter}. Objects that accept via this method.
	 * should also implement {@link SQLFilter}
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitPatternFilter(PatternFilter<? super T> fil) throws Exception;
	/** process a {@link BaseSQLCombineFilter} this combines all of the sub-classes except
	 * {@link AccceptFilter} and can be either AND or OR combinations. This only combines filters that also
	 * implement {@link SQLFilter}.
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitSQLCombineFilter(BaseSQLCombineFilter<? super T> fil) throws Exception;
	/** process a {@link AndFilter} or its sub-types. this can combine any of the filter sub-types.
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitAndFilter(AndFilter<? super T> fil) throws Exception;
	
	/** process a {@link OrFilter} or its sub-types. this can combine any of the filter sub-types.
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitOrFilter(OrFilter<? super T> fil) throws Exception;
	/** process a pure {@link OrderFilter}. Objects that accept via this method.
	 * should also implement {@link SQLFilter}
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitOrderFilter(SQLOrderFilter<? super T> fil) throws Exception;
	/** process a pure {@link AcceptFilter}
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitAcceptFilter(AcceptFilter<? super T> fil) throws Exception;
	/** process a {@link JoinFilter}. Objects that accept via this method
	 * should also implement {@link SQLFilter} and should not implement any other of the
	 * filter sub-types except {@link PatternFilter} which {@link JoinFilter} extends.
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitJoinFilter(JoinFilter<? super T> fil) throws Exception;
	
	/** process a {@link BinaryFilter} Objects that accept this method should generate a
	 * <em>true</em> or a <em>false</em> selection value. They can also implement {@link SQLFilter}.
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitBinaryFilter(BinaryFilter<? super T> fil) throws Exception;
	
	/** process a {@link BinaryAcceptFilter} Objects that accept this method
	 *  can either act as a {@link BinaryFilter} or an {@link AcceptFilter}
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitBinaryAcceptFilter(BinaryAcceptFilter<? super T> fil) throws Exception;
	/** process a {@link DualFilter}.
	 * 
	 * @param fil
	 * @return
	 * @throws Exception
	 */
	public X visitDualFilter(DualFilter<? super T> fil) throws Exception;
}