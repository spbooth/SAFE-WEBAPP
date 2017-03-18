//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model;

import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;

/**
 * @author spb
 *
 */

public class DummyReferenceFactory extends DataObjectFactory<DummyReference> {

	@Override
	public Class<? super DummyReference> getTarget() {
		return DummyReference.class;
	}



	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(DummyReference.STRING_FIELD, new StringFieldType(true, null, 32));
		Dummy1.Factory fac = new Dummy1.Factory(c);
		spec.setField(DummyReference.REF_FIELD, fac.getReferenceFieldType());
		spec.setField(DummyReference.NUMBER_FIELD, new IntegerFieldType(false, 99));
		return spec;
	}


	/** get filter on Dummy1 based on our name field.
	 * 
	 * @param name
	 * @return
	 */
	public SQLFilter<Dummy1> getReferencedFilter(String name){
		return new DestFilter<Dummy1>(new SQLValueFilter<DummyReference>(getTarget(),res,DummyReference.STRING_FIELD,name), DummyReference.REF_FIELD, new Dummy1.Factory(getContext()));
	}

	public SQLFilter<DummyReference> getRemoteNameFilter(String name){
		Dummy1.Factory fac = new Dummy1.Factory(getContext());
		return new RemoteFilter<Dummy1>(fac, DummyReference.REF_FIELD, fac.new StringFilter(name));
	}
	
	public Set<Dummy1> geReferencedDummy(String name) throws DataFault{
		IndexedTypeProducer<Dummy1, Dummy1.Factory>prod = new IndexedTypeProducer<Dummy1, Dummy1.Factory>(getContext(), DummyReference.REF_FIELD, new Dummy1.Factory(getContext()));
		return getReferenced(prod, new SQLValueFilter<DummyReference>(getTarget(), res, DummyReference.STRING_FIELD, name));
	}
	
	/**
	 * 
	 */
	public DummyReferenceFactory(AppContext conn) {
		setContext(conn, "DummyReference");
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DummyReference makeBDO(Record res) throws DataFault {
		return new DummyReference(res);
	}

	
}