//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.convert.LazyTypeConverter;



public class LazyIndexProducer<A extends Indexed,F extends IndexedProducer<A>> extends LazyTypeConverter<A, Number, F> implements IndexedProducer<A>{

	public LazyIndexProducer(AppContext c, F result) {
		super(c, result);
	}

	public LazyIndexProducer(AppContext c, Class<? super F> clazz, String tag) {
		super(c, clazz, tag);
	}

	public A find(int id) throws DataException {
		return getInner().find(id);
	}

	public IndexedReference<A> makeReference(A obj) {
		return getInner().makeReference(obj);
	}

	public IndexedReference<A> makeReference(int id) {
		return getInner().makeReference(id);
	}

	public boolean isMyReference(IndexedReference ref) {
		return getInner().isMyReference(ref);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#getID(uk.ac.ed.epcc.webapp.Indexed)
	 */
	@Override
	public String getID(A obj) {
		return getInner().getID(obj);
	}

	
}