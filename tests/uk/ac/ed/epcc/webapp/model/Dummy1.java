//| Copyright - The University of Edinburgh 2015                            |
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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.NumericEnumProducer;
import uk.ac.ed.epcc.webapp.model.data.filter.FieldOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** class instantiating the standard test table.
 * 
 * @author spb
 *
 */
public class Dummy1 extends DataObject implements Removable {
	public static final String NAME = "Name";
	public static final String MANDATORY ="Mandatory";
	public static final String NUMBER = "Number";
	public static final String UNSIGNED = "UnsignedInt";
	public static final String TIME="Time";
	public static final String DEFAULT_TABLE = "Test";
	public static enum Beatle{
		John,
		Paul,
		Ringo,
		George
	};
   	public static final EnumProducer<Beatle> beatles = new EnumProducer<>(Beatle.class, "Beatles");
  	public static final NumericEnumProducer<Beatle> ruttles = new NumericEnumProducer<>(Beatle.class, "Ruttles");
  	 
	public Dummy1(Repository.Record res) {
		super(res);
	}

	public Dummy1(AppContext ctx) {
		super(getRecord(ctx,DEFAULT_TABLE));
	}

	public Beatle getBeatle(){
		return record.getProperty(beatles);
	}
	
	public void setBeatle(Beatle b){
		record.setProperty(beatles, b);
	}
	
	public Beatle getRuttle(){
		return record.getProperty(ruttles);
	}
	
	public void setRuttle(Beatle b){
		record.setProperty(ruttles, b);
	}
	public String getName(){
		return record.getStringProperty(NAME);
	}
	public int getNumber(){
		return record.getIntProperty(NUMBER, 0);
	}
	public void setName(String n){
		record.setProperty(NAME, n);
	}
	public void setNumber(int n){
		record.setProperty(NUMBER, n);
	}
	public long getUnsigned(){
		// regression test for mysql bug where unsigned fields returned corrupt long values
		return record.getLongProperty(UNSIGNED,0);
	}
	public void setUnsigned(long i){
		record.setProperty(UNSIGNED,i);
	}
	
	public Date getDateTime(){
		return record.getDateProperty(TIME);
	}

	public void setDateTime(Date d){
		record.setProperty(TIME, d);
	}
	
	
    public static class Factory extends DataObjectFactory<Dummy1> {
    	 /**
		 * 
		 */
		
		public class NumberFilter extends SQLValueFilter<Dummy1>{
         	public NumberFilter(Number n){
         		super(Factory.this.getTarget(),res,NUMBER,n);
         	}
         }
    	 public class NumberAcceptFilter extends AbstractAcceptFilter<Dummy1>{
    		 Number n;
          	public NumberAcceptFilter(Number n){
          		super(Factory.this.getTarget());
          		this.n=n;
          	}
			public boolean accept(Dummy1 d) {
				return n.intValue() == d.getNumber();
			}
          }
         public class StringFilter extends SQLValueFilter<Dummy1>{
         	public StringFilter(String s){
         		super(Factory.this.getTarget(),res,NAME,s);
         	}
         }
		public Factory(AppContext c) {
			setContext(c,DEFAULT_TABLE);
		}
		public Factory(AppContext c,String table) {
			setContext(c,table);
		}
        public long count(SQLFilter<Dummy1> f) throws DataException{
        	return getCount(f);
        }
		@Override
		protected DataObject makeBDO(Repository.Record res) throws DataFault {
			return new Dummy1(res);
		}
    	public void nuke() throws DataFault{
    		for(Iterator it = getAllIterator(); it.hasNext();){
    			DataObject o = (DataObject) it.next();
    			o.delete();
    		}
    	}
		@Override
		protected List<OrderClause> getOrder() {
			List<OrderClause> result = new LinkedList<OrderClause>();
			result.add(res.getOrder(NUMBER, false));
			return result;
		}
		
		public FilterResult<Dummy1> getReverse() throws DataFault{
			AndFilter<Dummy1>fil = new AndFilter<Dummy1>(getTarget());
			fil.addFilter(new FieldOrderFilter<Dummy1>(Factory.this.getTarget(),res, NUMBER, true));
			return new FilterSet(fil);
			
		}
		public SQLExpression<String> getNameExpression(){
			return res.getStringExpression(getTarget(), NAME);
		}
		public FilterResult<Dummy1> getWithFilter() throws DataFault{
			AndFilter<Dummy1>fil = new AndFilter<Dummy1>(getTarget());
			return new FilterSet(fil);
			
		}
		@Override
		protected TableSpecification getDefaultTableSpecification(AppContext c,
				String table) {
			TableSpecification spec = new TableSpecification();
			spec.setField(NAME, new StringFieldType(true, "", 32));
			spec.setField(NUMBER, new DoubleFieldType(true, 0.0));
			spec.setField(UNSIGNED, new LongFieldType(true, 0L));
			spec.setField(MANDATORY, new StringFieldType(false, "Junk", 32));
			spec.setField(TIME,new DateFieldType(true, null));
			spec.setField(beatles.getField(), beatles.getFieldType(Beatle.Paul));
			spec.setField(ruttles.getField(), ruttles.getFieldType(Beatle.Ringo));
			return spec;
		}
		public Set<String> getNullFields(){
			// expose for testing
			return getNullable();
		}
		public boolean fieldExists(String name){
			return res.hasField(name);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getTarget()
		 */
		@Override
		public Class<? super Dummy1> getTarget() {
			return Dummy1.class;
		}
		
		
		
    }


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Removable#remove()
	 */
	@Override
	public void remove() throws DataException {
		delete();
		
	}
}