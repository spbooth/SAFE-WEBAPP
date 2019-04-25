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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.NumberFieldType;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
/** TypeProducer to make {@link Indexed} objects from an Integer field.
 * These can be initialised either from a Producer or from the necessary class/tag etc.
 * 
 * @author spb
 *
 * @param <T>
 * @param <F> 
 */


public class IndexedTypeProducer<T extends Indexed,F extends IndexedProducer<T>> extends LazyIndexProducer<T, F>implements TypeProducer<T,Number> {
	
	

	private final String field;
	
	/** Constructor to use when an instance of the {@link IndexedProducer} is already available.
	 * @param conn 
	 * 
	 * @param field
	 * @param p
	 */
	public IndexedTypeProducer(AppContext conn,String field,F p){
		super(conn,p);
		this.field=field;
	}
	/** Constructor that lazily evaluates the IndexedProducer. 
	 * Use this constructor to avoid recursion where classes have referenced to each other.
	 * 
	 * @param field
	 * @param conn
	 * @param clazz
	 * @param tag
	 */
	public IndexedTypeProducer(String field,AppContext conn,Class<F> clazz,String tag){
		super(conn,clazz,tag);
		this.field=field;
	}
	
	@Override
	public String getField() {
		return field;
	}
	
	public IndexedProducer<T> getProducer(){
		return getInner();
	}
	@Override
	public String toString() {
		return "IndexedTypeProducer [field=" + field + ", getProducer()="
				+ getProducer() + "]";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer#getFieldType(java.lang.Object)
	 */
	@Override
	public FieldType<Integer> getFieldType(T def) {
		return new NumberFieldType<>(Integer.class, def == null,def == null ? null : def.getID());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
	 */
	@Override
	public Input<Integer> getInput() {
		IndexedProducer<T> producer = getProducer();
		if( producer instanceof Selector){
			return ((Selector)producer).getInput();
		}
		return null;
	}
	
	
}