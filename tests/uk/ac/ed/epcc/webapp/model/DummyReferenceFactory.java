// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
		return new RemoteFilter<Dummy1>(fac.new StringFilter(name), DummyReference.REF_FIELD, fac);
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
