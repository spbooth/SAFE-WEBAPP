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

import java.util.List;

/** superclass for filters that never select anything.
 * @author spb
 *
 * @param <T>
 */
public abstract class AbstractBinaryFilter<T> implements PatternFilter<T>, BinaryFilter<T>{

	

	protected final Class<? super T> target;
	protected boolean value;

	/**
	 * 
	 */
	public AbstractBinaryFilter(Class<? super T> target,boolean value) {
		super();
		this.target=target;
		this.value=value;
	}

	public final StringBuilder addPattern(StringBuilder sb, boolean qualify) {
		sb.append(" ");
		sb.append(Boolean.toString(value));
		sb.append(" ");
		return sb;
	}

	public final List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter#getBooleanResult()
	 */
	@Override
	public final boolean getBooleanResult() {
		return value;
	}
	
	
	public final Class<? super T> getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		AbstractBinaryFilter other = (AbstractBinaryFilter) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target)){
			return false;
		}else if( value != other.value)
			return false;
		return true;
	}

}