// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.model.Dummy1.Factory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;

/** Test class that references remote objects
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class DummyReference extends DataObject {
    public static final String REF_FIELD="Reference";
    public static final String NUMBER_FIELD="Number";
    public static final String STRING_FIELD="Name";
    
    final IndexedTypeProducer<Dummy1, Dummy1.Factory> prod;
	/**
	 * @param r
	 */
	public DummyReference(Record r) {
		super(r);
		 prod = new IndexedTypeProducer<Dummy1, Dummy1.Factory>(getContext(), REF_FIELD, new Dummy1.Factory(getContext()));
	}
	
	public String getName(){
		return record.getStringProperty(STRING_FIELD);
	}
	public void setName(String s){
		record.setProperty(STRING_FIELD, s);
	}

	/**
	 * @param dummy1
	 */
	public void setReference(Dummy1 dummy1) {
		
		record.setProperty(prod, dummy1 );
		
	}
	public Dummy1 getReference(){
		return record.getProperty(prod);
	}
	
	public int getNumber(){
		return record.getIntProperty(NUMBER_FIELD,0);
	}
	public void setNumber(int num){
		record.setProperty(NUMBER_FIELD, num);
	}

}
