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

import java.util.Collection;
import java.util.LinkedHashSet;

/** A simple AND combination of {@link AcceptFilter}s.
 * @author spb
 * @param <T> 
 *
 */
public class AndAcceptFilter<T> extends LinkedHashSet<AcceptFilter<? super T>> implements AcceptFilter<T> {
	private final Class<T> target;
	/**
	 * 
	 * @param target
	 */
	public AndAcceptFilter(Class<T> target){
		super();
		this.target=target;
	}
	/**
	 * @param target 
	 * @param c
	 */
	public AndAcceptFilter(Class<T> target,Collection<? extends AcceptFilter<? super T>> c) {
		super(c);
		this.target=target;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		return vis.visitAcceptFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<T> getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(T o) {
		for(AcceptFilter<? super T> fil : this){
			if( ! fil.accept(o)){
				return false;
			}
		}
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AndAcceptFilter other = (AndAcceptFilter) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AndAcceptFilter(" + super.toString() + ")";
	}

}
