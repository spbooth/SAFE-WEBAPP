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

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;

/** Test class that references remote objects
 * @author spb
 *
 */

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