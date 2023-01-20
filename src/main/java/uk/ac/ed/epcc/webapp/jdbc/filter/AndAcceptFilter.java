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
	private final String tag;
	/**
	 * 
	 * @param target
	 */
	public AndAcceptFilter(String tag){
		super();
		this.tag=tag;
	}
	/**
	 * @param target 
	 * @param c
	 */
	public AndAcceptFilter(String tag,Collection<? extends AcceptFilter<? super T>> c) {
		super(c);
		this.tag=tag;
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return tag;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean test(T o) {
		for(AcceptFilter<? super T> fil : this){
			if( ! fil.test(o)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "AndAcceptFilter(" + super.toString() + ")";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

}
