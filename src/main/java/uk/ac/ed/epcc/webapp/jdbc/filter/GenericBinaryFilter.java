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
 * @param <T> type of filter target
 */
public class GenericBinaryFilter<T> implements SQLFilter<T>, BinaryFilter<T>{

	

	

	
	protected boolean value;


	/**
	 * 
	 */
	public GenericBinaryFilter(boolean value) {
		super();
		this.value=value;
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter#getBooleanResult()
	 */
	@Override
	public final boolean getBooleanResult() {
		return value;
	}
	
	public String toString() {
		return "GenericBinaryFilter("+value+")";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
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
		GenericBinaryFilter other = (GenericBinaryFilter) obj;
		if (value != other.value)
			return false;
		return true;
	}
	

}