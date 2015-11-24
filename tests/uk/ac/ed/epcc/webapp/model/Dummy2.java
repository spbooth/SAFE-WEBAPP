/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

public class Dummy2 extends DataObject {
	private static final String NUMBER = "Number";
	private static final String NAME = "Name";
	public static final String DEFAULT_TABLE = "Test2";
	

	public Dummy2(Repository.Record res) {
		super(res);
	}

	public Dummy2(AppContext ctx) {
		super(getRecord(ctx,DEFAULT_TABLE));
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
	
    public static class Factory extends DataObjectFactory<Dummy2>{
		public class NumberFilter extends SQLValueFilter<Dummy2>{
        	public NumberFilter(Number n){
        		super(Factory.this.getTarget(),res,NUMBER,n);
        	}
        }
        public class StringFilter extends SQLValueFilter<Dummy2>{
        	public StringFilter(String s){
        		super(Factory.this.getTarget(),res,NAME,s);
        	}
        }
		public Factory(AppContext c) {
			setContext(c,DEFAULT_TABLE);
		}

		@Override
		protected DataObject makeBDO(Repository.Record res) throws DataFault {
			return new Dummy2(res);
		}
		public void nuke() throws DataFault{
    		for(Iterator it = getAllIterator(); it.hasNext();){
    			DataObject o = (DataObject) it.next();
    			o.delete();
    		}
    	}
		@Override
		protected TableSpecification getDefaultTableSpecification(AppContext c,
				String table) {
			TableSpecification spec = new TableSpecification();
			spec.setField(NAME, new StringFieldType(true, "", 32));
			spec.setField(NUMBER, new DoubleFieldType(true, 0.0));
			return spec;
		}
    }
}
