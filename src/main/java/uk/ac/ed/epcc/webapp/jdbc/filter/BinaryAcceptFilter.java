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

/** A wrapper that converts a {@link BinaryFilter} to an {@link AcceptFilter}
 * 
 * This is essentially just a cast operation to allow any {@link BinaryFilter}
 * to be used where and {@link AcceptFilter} is required
 * 
 * visitors will usually treat as a binary filter 
 * 
 * @author spb
 * @param <T> type of filter
 *
 */
public class BinaryAcceptFilter<T> implements AcceptFilter<T>, BinaryFilter<T> {

	

	public BinaryAcceptFilter(BinaryFilter<T> nested) {
		super();
		this.nested = nested;
	}

	private final BinaryFilter<T> nested;
	


	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		return vis.visitBinaryAcceptFilter(this);
	}

	public final boolean test(T o){
		return nested.getBooleanResult();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return nested.getTag();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nested == null) ? 0 : nested.hashCode());
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
		BinaryAcceptFilter other = (BinaryAcceptFilter) obj;
		if (nested == null) {
			if (other.nested != null)
				return false;
		} else if (!nested.equals(other.nested))
			return false;
		return true;
	}
	public String toString(){
		return "BinaryAcceptFilter("+nested.getBooleanResult()+","+nested+")";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter#getBooleanResult()
	 */
	@Override
	public boolean getBooleanResult() {
		return nested.getBooleanResult();
	}
}
