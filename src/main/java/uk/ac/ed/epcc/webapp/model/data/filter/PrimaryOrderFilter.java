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
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** An {@link OrderFilter} that enforces use of the PrimaryKey
 * 
 * This is for modification loops that might change the default ordering and therefore 
 * might fall foul of the chunking code.
 * 
 * @author spb
 *
 * @param <T>
 */


public class PrimaryOrderFilter<T> implements SQLOrderFilter<T> {
	private final Repository res;
	private final boolean descending;
	public PrimaryOrderFilter(Repository res,boolean descending){
		this.res=res;
		this.descending=descending;
	}
	@Override
	public List<OrderClause> OrderBy() {
		LinkedList<OrderClause> order = new LinkedList<>();
		order.add(res.getOrder(null, descending));
		return order;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		return vis.visitOrderFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return res.getTag();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (descending ? 1231 : 1237);
		result = prime * result + ((res == null) ? 0 : res.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrimaryOrderFilter other = (PrimaryOrderFilter) obj;
		if (descending != other.descending)
			return false;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}

}