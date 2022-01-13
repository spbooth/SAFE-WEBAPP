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

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
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
		protected Dummy2 makeBDO(Repository.Record res) throws DataFault {
			return new Dummy2(res);
		}
		public void nuke() throws DataFault{
    		for(Iterator it = getAllIterator(); it.hasNext();){
    			DataObject o = (DataObject) it.next();
    			o.delete();
    		}
    	}
		public SQLExpression<String> getNameExpression(){
			return res.getStringExpression(getTarget(), NAME);
		}
		@Override
		protected TableSpecification getDefaultTableSpecification(AppContext c,
				String table) {
			TableSpecification spec = new TableSpecification();
			spec.setField(NAME, new StringFieldType(true, "", 32));
			spec.setField(NUMBER, new DoubleFieldType(true, 0.0));
			return spec;
		}

		public StringFilter getStringFilter(String name) {
			return new StringFilter(name);
		}

    }
}