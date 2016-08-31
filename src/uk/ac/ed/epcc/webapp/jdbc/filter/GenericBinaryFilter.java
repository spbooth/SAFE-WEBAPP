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

/** {@link BaseFilter} that select true/false.
 * 
 * This class is a {@link BinaryFilter} and marked as an {@link SQLFilter}
 * 
 * @author spb
 *
 * @param <T>
 */
public class GenericBinaryFilter<T> implements SQLFilter<T>, BinaryFilter<T>{

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + (value ? 1231 : 1237);
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
		GenericBinaryFilter other = (GenericBinaryFilter) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (value != other.value)
			return false;
		return true;
	}


	protected final Class<? super T> target;
	protected boolean value;

	/**
	 * 
	 */
	public GenericBinaryFilter(Class<? super T> target,boolean value) {
		super();
		this.target=target;
		this.value=value;
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
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public final <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		// Default is to act as a binary filter
		return vis.visitBinaryFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
	 */
	@Override
	public void accept(T o) {
		
	}

	

}