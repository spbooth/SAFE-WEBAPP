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

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** An OrderFilter that enforces use of the PrimaryKey
 * 
 * This is for modification loops that might change the default ordering and therefore 
 * might fall foul of the chunking code.
 * 
 * @author spb
 *
 * @param <T>
 */


public class PrimaryOrderFilter<T> implements OrderFilter<T> , SQLFilter<T>{
	private final Class<? super T> target;
	private final Repository res;
	private final boolean descending;
	public PrimaryOrderFilter(Class<? super T> target,Repository res,boolean descending){
		this.target=target;
		this.res=res;
		this.descending=descending;
	}
	public List<OrderClause> OrderBy() {
		LinkedList<OrderClause> order = new LinkedList<OrderClause>();
		order.add(res.getOrder(null, descending));
		return order;
	}
	
	
	public void accept(T o) {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitOrderFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}

}