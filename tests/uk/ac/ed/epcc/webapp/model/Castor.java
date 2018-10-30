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
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Dummy1.Factory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
/** Test target for mutual cross links
 * 
 * @author spb
 *
 */
public class Castor extends DataObject implements Removable {
	public static final String NAME = "Name";
	public static final String REMOTE = "Remote";
	public static final String DEFAULT_TABLE = "Castor";
	
  	 
	public Castor(Repository.Record res) {
		super(res);
	}

	public Castor(AppContext ctx) {
		super(getRecord(ctx,DEFAULT_TABLE));
	}

	
	public String getName(){
		return record.getStringProperty(NAME);
	}
	
	public void setName(String n){
		record.setProperty(NAME, n);
	}
	
	public void setReference(Pollux peer) {
		record.setProperty(REMOTE, peer);
	}
    public static class Factory extends DataObjectFactory<Castor> {
    	
		public Factory(AppContext c) {
			setContext(c,DEFAULT_TABLE);
		}
		public Factory(AppContext c,String table) {
			setContext(c,table);
		}
       
		@Override
		protected DataObject makeBDO(Repository.Record res) throws DataFault {
			return new Castor(res);
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
			spec.setField(REMOTE, new ReferenceFieldType(Pollux.DEFAULT_TABLE));
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
		public Class<Castor> getTarget() {
			return Castor.class;
		}
		public BaseFilter<Castor> getFilterFromPeer(BaseFilter<Pollux>  fil){
			return getRemoteFilter(new Pollux.Factory(getContext()), REMOTE, fil);
		}
		 public class StringFilter extends SQLValueFilter<Castor>{
	         	public StringFilter(String s){
	         		super(Factory.this.getTarget(),res,NAME,s);
	         	}
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