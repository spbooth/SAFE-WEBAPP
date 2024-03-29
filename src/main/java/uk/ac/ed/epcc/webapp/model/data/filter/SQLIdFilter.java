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
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Filter to select/exclude an entry by Id.
 * 
 * @author spb
 * @see SelfReferenceFilter
 * @see IdAcceptFilter
 *
 * @param <T>
 */
public class SQLIdFilter<T extends DataObject> implements SQLFilter<T>, PatternFilter<T>{

	
	public SQLIdFilter(Repository res, int id) {
		this( res,id,false);
	}
		
	public SQLIdFilter(Repository res, int id,boolean exclude) {
		super();
		this.res = res;
		this.id=id;
		this.exclude=exclude;
	}


	private final Repository res;
	private final int id;
	private final boolean exclude;
	
	
	


	
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list.add(new ConstPatternArgument<>(Integer.class, id));
		return list;
	}

	
	@Override
	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb, boolean qualify) {
		res.addUniqueName(sb, qualify, true);
		if( exclude) {
			sb.append("!=?");
		}else {
			sb.append("=?");
		}
		return sb;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return res.getTag();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SQLIdFilter(id=");
		sb.append(Integer.toString(id));
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (exclude ? 1231 : 1237);
		result = prime * result + id;
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
		SQLIdFilter other = (SQLIdFilter) obj;
		if (exclude != other.exclude)
			return false;
		if (id != other.id)
			return false;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}

	

}