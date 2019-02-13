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
package uk.ac.ed.epcc.webapp.model.data.expr;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
/** A type converter wrapper SQLValue.
 * 
 * 
 * @author spb
 *
 * @param <T> type of value produced
 * @param <D> underlying value
 * @param <X> DataObject
 */


public class TypeFilterProducerSQLValue<T,D,X extends DataObject> extends TypeConverterSQLValue<X, T, D> implements  SQLAccessor<T,X>,  FilterProvider<X,T> , Targetted<T> {
	public TypeFilterProducerSQLValue(DataObjectFactory<X> fac, TypeProducer<T, D> converter, SQLAccessor<D,X> inner) {
		super(fac.getTarget(),converter,inner);
		
	}
	public TypeProducer<T, D> getConverter(){
		return (TypeProducer<T, D>) super.getConverter();
	}
	public SQLAccessor<D,X> getNested(){
		return (SQLAccessor<D, X>) super.getNested();
	}
	public T getValue(X r) {

		return getConverter().find(getNested().getValue(r));
	}
	
	public void setValue(X r, T value) {
		getNested().setValue(r, getConverter().getIndex(value));

	}
	public boolean canSet() {
		return getNested().canSet();
	}
	
}