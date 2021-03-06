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

/** An abstract class for implementing {@link AcceptFilter}.
 * 
 * @author spb
 *
 */
public abstract class AbstractAcceptFilter<T> implements AcceptFilter<T> {
	
	private final Class<T> target;

   /**
	 * 
	 */
	protected AbstractAcceptFilter(Class<T> target) {
		this.target=target;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public final <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		return vis.visitAcceptFilter(this);
	}

	@Override
	public final Class<T> getTarget(){
		return target;
	}
}
