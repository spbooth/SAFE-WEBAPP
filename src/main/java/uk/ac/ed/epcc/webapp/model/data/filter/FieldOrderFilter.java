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
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** An OrderFilter that uses a single field
 * 
 * This is for modification loops that might change the default ordering and therefore 
 * might fall foul of the chunking code.
 * 
 * @author spb
 *
 * @param <T>
 */


public class FieldOrderFilter<T> implements SQLOrderFilter<T>{

	private final Repository res;
	private final String name;
	private final boolean descending;
	public FieldOrderFilter(Repository res,String name,boolean descending){
		this.res=res;
		this.name=name;
		this.descending=descending;
	}
	@Override
	public final List<OrderClause> OrderBy() {
		LinkedList<OrderClause> order = new LinkedList<>();
		order.add(res.getOrder(name, descending));
		return order;
	}
	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public final <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		return vis.visitOrderFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return res.getTag();
	}

}