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
package uk.ac.ed.epcc.webapp.jdbc.filter;

/** A composite of an {@link AcceptFilter} and a {@link SQLFilter}.
 * Both filters <em>MUST</em> implement the same selection rule. Depending on the context one or other of the filters will actually be used.
 * @author spb
 * @param <T> type of filter
 *
 */
public final class DualFilter<T> implements BaseFilter<T> {
  
public DualFilter(SQLFilter<T> sql, AcceptFilter<T> accept) {
		super();
		this.sql = sql;
		this.accept = accept;
	}
private final SQLFilter<T> sql;
   private final AcceptFilter<T> accept;
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
 */
@Override
public String getTag() {
	return sql.getTag();
}

public SQLFilter<T> getSQLFilter(){
	return sql;
}

public AcceptFilter<T> getAcceptFilter(){
	return accept;
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
 */
@Override
public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
	return vis.visitDualFilter(this);
}

/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accept == null) ? 0 : accept.hashCode());
		result = prime * result + ((sql == null) ? 0 : sql.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DualFilter other = (DualFilter) obj;
		if (accept == null) {
			if (other.accept != null)
				return false;
		} else if (!accept.equals(other.accept))
			return false;
		if (sql == null) {
			if (other.sql != null)
				return false;
		} else if (!sql.equals(other.sql))
			return false;
		return true;
	}
	public String toString() {
		return "DualFilter("+accept.toString()+","+sql.toString()+")";
	}
}
