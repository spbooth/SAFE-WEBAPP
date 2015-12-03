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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Class implementing transitions on factory classes identified by table.
 * The target factory has implement {@link TableTransitionTarget} and have its
 * class registered as a configuration property.
 * This TransitionProvider needs to be registered under the name <b>Table<b> 
 * to enable this feature.
 * 
 * @author spb
 *
 */


public class TableTransitionProvider implements ViewTransitionProvider<TransitionKey,TableTransitionTarget>, IndexTransitionProvider<TransitionKey, TableTransitionTarget>,Contexed{
    /**
	 * 
	 */
	public static final String TABLE_INDEX_ROLE = "TableIndex";

	/**
	 * 
	 */
	public static final String TABLE_TRANSITION_TAG = "Table";

	private final AppContext conn;
    
    public static final TransitionKey INDEX = new TransitionKey(TableTransitionProvider.class, "Index");
    public TableTransitionProvider(AppContext conn){
    	this.conn=conn;
    }
    public class IndexTransition extends AbstractTargetLessTransition<TableTransitionTarget>{
		public void buildForm(Form f, AppContext c) throws TransitionException {
			f.addInput("table", TABLE_TRANSITION_TAG, new TableInput<TableStructureTransitionTarget>(c, TableStructureTransitionTarget.class));
			f.addAction("View", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					String table = (String) f.get("table");
					TableStructureTransitionTarget target = getContext().makeObject(TableStructureTransitionTarget.class, table);
					return new ViewTableResult(target);
				}
			});
		}
    	
    }
	public boolean allowTransition(AppContext c,TableTransitionTarget target,
			TransitionKey name) {
		if( target == null ){
			// Restrict Index to Admin
			return name == INDEX && c.getService(SessionService.class).hasRoleFromList(SessionService.ADMIN_ROLE,TABLE_INDEX_ROLE);
		}
		return getRegistry(target).allowTableTransition(name,c.getService(SessionService.class));
	}

	public String getID(TableTransitionTarget target) {
		if( target == null){
			return null;
		}
		return target.getTableTransitionID();
	}

	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,TableTransitionTarget target) {
		SessionService operator = c.getService(SessionService.class);
		getRegistry(target).getTableTransitionSummary(cb,operator );
		return cb;
	}

	protected TableTransitionRegistry getRegistry(TableTransitionTarget target) {
		return target.getTableTransitionRegistry();
	}

	public TableTransitionTarget getTarget(String id) {
		if( id == null || id.length() == 0){
			return null;
		}
		return conn.makeObjectWithDefault(TableTransitionTarget.class, null,id);
	}

	public String getTargetName() {
		return TABLE_TRANSITION_TAG;
	}

	public Transition<TableTransitionTarget> getTransition(TableTransitionTarget target,TransitionKey name) {
		if( name == INDEX){
			return new IndexTransition();
		}
		return getRegistry(target).getTableTransition(name);
	}

	public Set<TransitionKey> getTransitions(TableTransitionTarget target) {
		return getRegistry(target).getTableTransitionKeys();
	}

	public TransitionKey lookupTransition(TableTransitionTarget target,String name) {
		if( target == null){
			if( INDEX.getName().equals(name)){
				return INDEX;
			}
		}else{
		for(TransitionKey key : getRegistry(target).getTableTransitionKeys()){
			if( key.getName().equals(name)){
				return key;
			}
		}
		}
		return null;
	}

	public AppContext getContext() {
		return conn;
	}
	public boolean canView(TableTransitionTarget target, SessionService<?> sess) {
		
		return sess.hasRoleFromList(SessionService.ADMIN_ROLE,TABLE_INDEX_ROLE);
	}
	public <X extends ContentBuilder> X getLogContent(X hb,TableTransitionTarget target, SessionService<?> sess) {
		hb.addHeading(2, "Table "+target.getTableTransitionID());
		hb.addHeading(3,"Table Type:"+target.getClass().getSimpleName());
		getRegistry(target).getTableTransitionSummary(hb,sess );
		return hb;
	}
	public <X extends ContentBuilder> X getTopContent(X hb,TableTransitionTarget target, SessionService<?> sess) {
		return hb;
	}
	public String getHelp(TransitionKey key) {
		return key.getHelp();
	}
	public String getText(TransitionKey key) {
		return key.toString();
	}
	public TransitionKey getIndexTransition() {
		return INDEX;
	}

	public <R> R accept(
			TransitionFactoryVisitor<R,TableTransitionTarget, TransitionKey> vis) {
		return vis.visitTransitionProvider(this);
		
	}
	

	
}