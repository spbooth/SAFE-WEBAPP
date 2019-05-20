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
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.history.HistoryHandler;
import uk.ac.ed.epcc.webapp.model.history.LinkDummyHistory;


public class LinkDummy extends LinkManager<LinkDummy.DummyLink,Dummy1,Dummy2> {
	private static class Status extends BasicType<Status.Value>{
	    private Status(){
	        super("Status");
	    }
	    private class Value extends BasicType.Value{
            private Value(String tag, String name){
                super(Status.this,tag,name);
            }
        }
	}
	private static Status status = new Status();
	public static final Status.Value ACTIVE    = status.new Value("A","Active"); 
	public static final Status.Value INACTIVE    = status.new Value("I","Inactive"); 
	
	public static LinkDummy getInstance(AppContext c){
		LinkDummy dum;
		dum = (LinkDummy) c.getAttribute("LinkDummy");
		if( dum == null ){
			dum = new LinkDummy(c);
			c.setAttribute("LinkDummy",dum);
		}
		return dum;
	}
	public LinkDummy(AppContext c){
		super(c,"LinkTest1Test2", new Dummy1.Factory(c), "Test1ID", new Dummy2.Factory(c), "Test2ID");
	
	}

	
	public void addLink(Dummy1 a, Dummy2 b) throws Exception{
		DummyLink l = makeLink(a,b);
		l.setStatus(ACTIVE);
		l.commit();
	}
	public void removeLink(Dummy1 a, Dummy2 b) throws DataException{
		DummyLink l = getLink(a,b);
		if( l == null ) return;
		l.setStatus(INACTIVE);
		l.commit();
	}
	public boolean isLinked(Dummy1 a, Dummy2 b) throws DataException{
		DummyLink l = getLink(a,b);
		if( l == null ) return false;
		return l.getStatus() == ACTIVE;
	}
	public Set getDummy2(Dummy1 arg) throws DataException{
		return getRightSet(arg, status.getFilter(this,ACTIVE));
	}
	public Set getDummy1(Dummy2 arg) throws DataException{
		return getLeftSet(arg,status.getFilter(this,ACTIVE));
	}
	public SQLFilter<DummyLink> getLinkedFilter(){
		return status.getFilter(this, ACTIVE);
	}
	public void nuke() throws DataFault{
		for(Iterator it = getAllIterator(); it.hasNext();){
			DataObject o = (DataObject) it.next();
			o.delete();
		}
		((Dummy1.Factory) getLeftFactory()).nuke();
		((Dummy2.Factory) getRightFactory()).nuke();
		LinkDummyHistory h = (LinkDummyHistory) getHistoryHandler();
		if( h != null ){
			h.nuke();
		}
	}
	public static class DummyLink extends LinkManager.Link<Dummy1,Dummy2>{
        /* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.LinkManager.Link#setup()
		 */
		@Override
		public void setup() {
			setStatus(ACTIVE);
		}
		public Status.Value getStatus(){
        	return record.getProperty(status);
        }
        public void setStatus(Status.Value v){
        	record.setProperty(status, v);
        }
		protected DummyLink(LinkDummy man,Repository.Record res) {
			super(man,res);
		}
		public Dummy1 getDummy1() throws DataException{
			return getLeft();
		}
		public Dummy2 getDummy2() throws DataException{
			return getRight();
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.LinkManager#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository)
	 */
	@Override
	protected DataObject makeBDO(Repository.Record res) throws DataFault {
		return new DummyLink(this,res);
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.LinkManager#makeHistoryFactory()
	 */
	@Override
	protected HistoryHandler<DummyLink> makeHistoryHandler() {
		System.out.println("calling makeHistoryFactory");
		return new LinkDummyHistory(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.LinkManager#useAutoHistory()
	 */
	@Override
	protected boolean useAutoHistory() {
		return true;
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table, IndexedProducer<Dummy1> leftFac, String leftField,
			IndexedProducer<Dummy2> rightFac, String rightField) {
		final TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField,
						rightFac, rightField);
		spec.setField(status.getField(), status.getFieldType(ACTIVE));
		return spec;
	}
}