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

import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.NumericEnumProducer;
import uk.ac.ed.epcc.webapp.model.data.filter.FieldOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
/** class instantiating the standard test table.
 * 
 * @author spb
 *
 */
public class Dummy1 extends DataObject implements Removable {
	@ConfigTag("Dummy")
	public static final String NAME = "Name";
	@ConfigTag("Dummy")
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
	
	
    public static class Factory extends DataObjectFactory<Dummy1> implements TestComposable, FieldHandler{
    	
    	@ConfigTag("Dummy")
    	public static final String BASE="Base";
    	 /**
		 * 
		 */
		
		public class NumberFilter extends SQLValueFilter<Dummy1>{
         	public NumberFilter(Number n){
         		super(res,NUMBER,n);
         	}
         }
		 public class StringAcceptFilter implements AcceptFilter<Dummy1>{
			 String s;
			 public StringAcceptFilter(String s) {
				 this.s=s;
			 }
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
			 */
			@Override
			public boolean test(Dummy1 o) {
				return s.equals(o.getName());
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + ((s == null) ? 0 : s.hashCode());
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				StringAcceptFilter other = (StringAcceptFilter) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (s == null) {
					if (other.s != null)
						return false;
				} else if (!s.equals(other.s))
					return false;
				return true;
			}
			private Factory getOuterType() {
				return Factory.this;
			}
			@Override
			public String toString() {
				return "StringAcceptFilter(" + s + ")";
			}
		 }
    	 public class NumberAcceptFilter implements AcceptFilter<Dummy1>{
    		 Number n;
          	public NumberAcceptFilter(Number n){
          		this.n=n;
          	}
			public boolean test(Dummy1 d) {
				return n.intValue() == d.getNumber();
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + ((n == null) ? 0 : n.hashCode());
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				NumberAcceptFilter other = (NumberAcceptFilter) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (n == null) {
					if (other.n != null)
						return false;
				} else if (!n.equals(other.n))
					return false;
				return true;
			}
			private Factory getOuterType() {
				return Factory.this;
			}
			@Override
			public String toString() {
				return "NumberAcceptFilter(" + n + ")";
			}
          }
         public class StringFilter extends SQLValueFilter<Dummy1>{
         	public StringFilter(String s){
         		super(res,NAME,s);
         	}
         }
         public class BeatleFilter extends FieldValueFilter<Beatle, Dummy1>{
        	 public BeatleFilter(Beatle beat) {
        		 super(res.getTypeProducerExpression(Beatle.class,beatles),beat);
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
		protected Dummy1 makeBDO(Repository.Record res) throws DataFault {
			return new Dummy1(res);
		}
    	public void nuke() throws DataFault{
    		FilterDelete del = new FilterDelete<>(res);
    		del.delete(null);
    	}
		@Override
		protected List<OrderClause> getOrder() {
			List<OrderClause> result = new LinkedList<>();
			result.add(res.getOrder(NUMBER, false));
			return result;
		}
		
		public FilterResult<Dummy1> getReverse() throws DataFault{
			return getResult(new FieldOrderFilter<>(res, NUMBER, true));
			
		}
		public SQLExpression<String> getNameExpression(){
			return res.getStringExpression(NAME);
		}
		public SQLExpression<Number> getNumberExpression(){
			return res.getNumberExpression(Number.class, NUMBER);
		}
		
		public TypeProducerFieldValue<Dummy1, Beatle, String> getBeatleFieldValue(){
			return res.getTypeProducerExpression(Beatle.class,beatles);
		}
		public FilterResult<Dummy1> getWithFilter() throws DataFault{
			AndFilter<Dummy1>fil = getAndFilter();
			return getResult(fil);
			
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
		
		public NumberFilter getNumberFilter(Number n) {
			return new NumberFilter(n);
		}
		public NumberAcceptFilter getNumberAcceptFilter(Number n) {
			return new NumberAcceptFilter(n);
		}
		public StringFilter getStringFilter(String name) {
			return new StringFilter(name);
		}
		public StringAcceptFilter getStringAcceptFilter(String s) {
			return new StringAcceptFilter(s);
		}
		public BeatleFilter getBeatleFilter(Beatle beat) {
			return new BeatleFilter(beat);
		}
		public FilterUpdate<Dummy1> getUpdate(){
			return new FilterUpdate<>(res);
		}
		@Override
		public void addConfigTags(Map<String, String> config_tags) throws Exception {
			FieldHandler.addConfigTags(getClass(),config_tags);
			FieldHandler.addConfigTags(Dummy1.class,config_tags);
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