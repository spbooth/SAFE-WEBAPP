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

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** A Negating filter that works with any base filter.
 * Should really use the {@link NegatingFilterVisitor} instead
 * @author spb
 * @see NegatingFilterVisitor
 */
public class NotAcceptFilter<T extends DataObject> extends AbstractAcceptFilter<T> implements NegatingFilter<BaseFilter<T>> {

	/**
	 * @param target
	 * @param fac
	 * @param fil
	 */
	public NotAcceptFilter(DataObjectFactory<T> fac, BaseFilter<T> fil) {
		super(fac.getTag());
		this.fac = fac;
		this.fil = fil;
	}



	private final DataObjectFactory<T> fac;
	private final BaseFilter<T> fil;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean test(T o) {
		return ! fac.matches(fil, o);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.NegatingFilter#getNested()
	 */
	@Override
	public BaseFilter<T> getNested() {
		return fil;
	}

}
