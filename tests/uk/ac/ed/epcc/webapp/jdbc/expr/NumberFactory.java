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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class NumberFactory extends DataObjectFactory<NumberFactory.NumberObject> {
	public class NumberObject extends DataObject{

		/**
		 * @param r
		 */
		protected NumberObject(Record r) {
			super(r);
		}
		
	}
	
	public NumberFactory(AppContext conn){
		setContext(conn, "NumberTable");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new NumberObject(res);
	}
	
	public Class<NumberObject>getTarget(){
		return NumberObject.class;
	}
	
	public SQLExpression<? extends Number> getExpr(){
		return res.getNumberExpression(NumberObject.class, Number.class, "Number");
	}
	public SQLExpression<? extends Number> getExpr2(){
		return res.getNumberExpression(NumberObject.class, Number.class, "Number2");
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField("Number", new DoubleFieldType(true, null));
		spec.setField("Number2", new DoubleFieldType(true, null));
		return spec;
	}
	
}