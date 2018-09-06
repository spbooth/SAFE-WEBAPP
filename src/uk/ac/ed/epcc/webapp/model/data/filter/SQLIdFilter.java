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

import uk.ac.ed.epcc.webapp.jdbc.filter.ConstPatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
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

	
	public SQLIdFilter(Class<? super T> target,Repository res, int id) {
		this( target,res,id,false);
	}
		
	public SQLIdFilter(Class<? super T> target,Repository res, int id,boolean exclude) {
		super();
		this.target=target;
		this.res = res;
		this.id=id;
		this.exclude=exclude;
	}

	private final Class<? super T> target;
	private final Repository res;
	private final int id;
	private final boolean exclude;
	
	
	

	
	public void accept(T o) {
		
	}

	
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list.add(new ConstPatternArgument<Integer>(Integer.class, id));
		return list;
	}

	
	public StringBuilder addPattern(StringBuilder sb, boolean qualify) {
		res.addUniqueName(sb, qualify, true);
		if( exclude) {
			sb.append("!=?");
		}else {
			sb.append("=?");
		}
		return sb;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SQLIdFilter(id=");
		sb.append(Integer.toString(id));
		sb.append(")");
		return sb.toString();
	}

}