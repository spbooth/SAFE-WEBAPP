//| Copyright - The University of Edinburgh 2012                            |
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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterFinder;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class DateTableFactory extends DataObjectFactory<DateTable> {
	/**
	 * 
	 */
	public static final String DOUBLE_HOURS = "DoubleHours";
	/**
	 * 
	 */
	public static final String YEAR = "Year";
	/**
	 * 
	 */
	public static final String HOURS = "Hours";
	/**
	 * 
	 */
	public static final String MILLIS = "Millis";
	/**
	 * 
	 */
	public static final String TIME = "Time";

	public DateTableFactory(AppContext c){
		setContext(c, "DateTable");
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DateTable makeBDO(Record res) throws DataFault {
		return new DateTable(res);
	}

	@Override
	public Class<? super DateTable> getTarget() {
		return DateTable.class;
	}

	public DateTable create(Calendar c) throws DataFault{
		Record r = res.new Record();
		r.setProperty(TIME, c.getTime());
		r.setProperty(MILLIS, c.getTimeInMillis());
		r.setProperty(HOURS, c.get(Calendar.HOUR_OF_DAY));
		r.setProperty(YEAR, c.get(Calendar.YEAR));
		r.setProperty(DOUBLE_HOURS, (double) c.get(Calendar.HOUR_OF_DAY));
		return makeBDO(r);
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification();
		spec.setField(TIME, new DateFieldType(true, null));
		spec.setField(MILLIS, new LongFieldType(true, null));
		spec.setField(HOURS, new IntegerFieldType());
		spec.setField(YEAR, new IntegerFieldType());
		spec.setField(DOUBLE_HOURS, new DoubleFieldType(true, null));
		return spec;
	}

	/** Give tests direct access to repository
	 * @return
	 */
	public Repository getRepository() {
		return res;
	}

	public class SumMapFinder extends AbstractFinder<Map<Integer,Number>>{

		/**
		 * @param c
		 */
		public SumMapFinder(AppContext c) {
			super();
			SumMapMapper<Integer> smm = new SumMapMapper<Integer>(c, res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS), "Hours", res.getNumberExpression(getTarget(),Long.class, DateTableFactory.MILLIS), "millis");
			setMapper(smm);
		}
		
	}
	public class BinarySumMapFinder extends AbstractFinder<Map<Number,Number>>{

		/**
		 * @param c
		 * @param op 
		 */
		public BinarySumMapFinder(AppContext c, Operator op) {
			super();
			SumMapMapper<Number> smm = new SumMapMapper<Number>(c,
					new BinarySQLValue(c,
							res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS),
							Operator.MUL,
							new ConstExpression<Integer, Integer>(Integer.class, 1)),
					"Hours", 
					BinaryExpression.create(c,
							res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS),
							op,
							res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.YEAR))
					, "composite");
			setMapper(smm);
		}		
	}
	public class ConstSumMapFinder extends AbstractFinder< Map<Integer,Number>>{

		/**
		 * @param c
		 */
		public ConstSumMapFinder(AppContext c) {
			super();
			SumMapMapper<Integer> smm = new SumMapMapper<Integer>(c, res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS), "Hours", 
					new ConstExpression<Integer, Integer>(Integer.class, 8), "sumconst");
			setMapper(smm);
		}
	}
	public class MinMapFinder extends AbstractFinder<Map<Integer,Number>>{

		/**
		 * @param c
		 */
		public MinMapFinder(AppContext c) {
			super();
			MinimumMapMapper<Integer> smm = new MinimumMapMapper<Integer>(c, res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS), "Hours", res.getNumberExpression(getTarget(),Long.class, DateTableFactory.MILLIS), "millis");
			setMapper(smm);
		}
		
	}
	public class MaxMapFinder extends AbstractFinder< Map<Integer,Number>>{

		/**
		 * @param c
		 */
		public MaxMapFinder(AppContext c) {
			super();
			MaximumMapMapper<Integer> mmm = new MaximumMapMapper<Integer>(c, res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS), "Hours", res.getNumberExpression(getTarget(),Long.class, DateTableFactory.MILLIS), "millis");
			setMapper(mmm);
		}
		
	}
	public class AvgMapFinder extends AbstractFinder<Map<Integer,Number>>{

		/**
		 * @param c
		 */
		public AvgMapFinder(AppContext c) {
			super();
			AverageMapMapper<Integer> amm = new AverageMapMapper<Integer>(c, res.getNumberExpression(getTarget(),Integer.class, DateTableFactory.HOURS), "Hours", res.getNumberExpression(getTarget(),Double.class, DateTableFactory.MILLIS), "avgmillis");
			setMapper(amm);
		}		
	}
	public Map<Integer,Number> getSumMap(SQLFilter<DateTable> fil) throws DataException{
		SumMapFinder finder = new SumMapFinder(getContext());
		return finder.find(fil);
	}
	public Map<Number,Number> getBinarySumMap(SQLFilter<DateTable> fil, Operator op) throws DataException{
		BinarySumMapFinder finder = new BinarySumMapFinder(getContext(), op);
		return finder.find(fil);
	}
	public Map<Integer,Number> getConstSumMap(SQLFilter<DateTable> fil) throws DataException{
		ConstSumMapFinder finder = new ConstSumMapFinder(getContext());
		return finder.find(fil);
	}
	public Map<Integer,Number> getMinMap(SQLFilter<DateTable> fil) throws DataException{
		MinMapFinder finder = new MinMapFinder(getContext());
		return finder.find(fil);
	}
	public Map<Integer,Number> getMaxMap(SQLFilter<DateTable> fil) throws DataException{
		MaxMapFinder finder = new MaxMapFinder(getContext());
		return finder.find(fil);
	}
	public Map<Integer,Number> getAvgMap(SQLFilter<DateTable> fil) throws DataException{
		AvgMapFinder finder = new AvgMapFinder(getContext());
		return finder.find(fil);
	}
}