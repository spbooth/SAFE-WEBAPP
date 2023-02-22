//| Copyright - The University of Edinburgh 2018                            |
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
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NumberFieldExpression;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
public class NumberTableFactory extends DataObjectFactory<NumberTableFactory.NumberTable> {

	public static final String NUM1="Number1";
	public static final String NUM2="Number2";
	public static final String NUM3="Number3";
	public NumberTableFactory(AppContext conn) {
		setContext(conn, "NumberTable");
	}
	public static class NumberTable extends DataObject{

		/**
		 * @param r
		 */
		protected NumberTable(Record r) {
			super(r);
		}
		public void setNumber1(double val) {
			record.setProperty(NUM1, val);
		}
		public void setNumber2(double val) {
			record.setProperty(NUM2, val);
		}
		public void setNumber3(double val) {
			record.setProperty(NUM3, val);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected NumberTable makeBDO(Record res) throws DataFault {
		return new NumberTable(res);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getDefaultTableSpecification(uk.ac.ed.epcc.webapp.AppContext, java.lang.String)
	 */
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(NUM1, new DoubleFieldType(true, 0.0));
		spec.setField(NUM2, new DoubleFieldType(true, 0.0));
		spec.setField(NUM3, new DoubleFieldType(true, null));
		return spec;
	}
	public NumberFieldExpression<Double,NumberTable> getNumber1Expr(){
		return res.getNumberExpression(Double.class, NUM1);
	}
	public NumberFieldExpression<Double,NumberTable> getNumber2Expr(){
		return res.getNumberExpression(Double.class, NUM2);
	}
	public NumberFieldExpression<Double,NumberTable> getNumber3Expr(){
		return res.getNumberExpression(Double.class, NUM3);
	}

	
	public class ReductionFinder extends AbstractFinder<Double>{

		/**
		 * @param c
		 * @param target
		 */
		public ReductionFinder( SQLExpression<Double> exp) {
			super();
			setMapper(new ReductionMapper<>(getContext(), Double.class, Reduction.SUM, 0.0, exp));
		}
	}
	public Double getSum(SQLFilter<NumberTable> fil,SQLExpression<Double> expr) throws DataException {
		ReductionFinder finder = new ReductionFinder(expr);
		return finder.find(fil);
	}
}
