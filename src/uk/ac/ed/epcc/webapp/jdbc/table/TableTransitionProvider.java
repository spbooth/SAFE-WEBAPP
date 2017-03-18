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

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.FileUploadDecorator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.IndexTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.UnDumper;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Class implementing transitions on factory classes identified by table.
 * The target factory has implement {@link TableTransitionTarget} and have its
 * class registered as a configuration property.
 * This TransitionProvider needs to be registered under the name <b>Table</b> 
 * to enable this feature.
 * 
 * @author spb
 *
 */


public class TableTransitionProvider implements ViewTransitionProvider<TableTransitionKey,TableTransitionTarget>, IndexTransitionProvider<TableTransitionKey, TableTransitionTarget>,Contexed{
	
	public static final Feature UPLOAD_XML_FEATURE = new Feature("table_transition.upload_xml",false,"Allow XML tables to be uploaded via a transition");
    /**
	 * 
	 */
	public static final String TABLE_INDEX_ROLE = "TableIndex";

	/**
	 * 
	 */
	public static final String TABLE_TRANSITION_TAG = "Table";

	private final AppContext conn;
    
    public static final TableTransitionKey INDEX = new TableTransitionKey(TableTransitionProvider.class, "Index"){

		@Override
		public boolean allow(SessionService serv, TableTransitionTarget target) {
			return serv.hasRoleFromList(SessionService.ADMIN_ROLE,TABLE_INDEX_ROLE);
		}
    	
    };
    public static final TableTransitionKey UPLOAD = new AdminOperationKey(TableTransitionProvider.class, "Upload");
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
    /** A transition to upload an XML dump into the table.
     * 
     * @author spb
     *
     */
    public class UploadTransition extends AbstractTargetLessTransition<TableTransitionTarget>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			TextInput update_input = new TextInput();
			update_input.setMaxResultLength(23*1024*2024);
			FileUploadDecorator decorator = new FileUploadDecorator(update_input);
			f.addInput("data", "Update text", decorator);
			f.addAction("Update", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					String update = (String) f.get("data");
					UnDumper undumper = new UnDumper(getContext());
					SAXParserFactory spf = SAXParserFactory.newInstance();

					SAXParser parser;
					try {
						parser = spf.newSAXParser();
						XMLReader reader = parser.getXMLReader();
						reader.setContentHandler(undumper);
						reader.parse(new InputSource(new StringReader(update)));
					} catch (Exception e) {
						throw new ActionException("Error readind dump", e);
					}
					return new IndexTransitionResult<TableTransitionTarget,TableTransitionKey>(TableTransitionProvider.this);
				}
			});
		}
    	
    }
    
	public boolean allowTransition(AppContext c,TableTransitionTarget target,
			TableTransitionKey name) {
		return name.allow(c.getService(SessionService.class),target);
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

	public Transition<TableTransitionTarget> getTransition(TableTransitionTarget target,TableTransitionKey name) {
		if( name == INDEX){
			return new IndexTransition();
		}
		if( name == UPLOAD && UPLOAD_XML_FEATURE.isEnabled(getContext())){
			return new UploadTransition();
		}
		return getRegistry(target).getTableTransition(name);
	}

	public Set<TableTransitionKey> getTransitions(TableTransitionTarget target) {
		return getRegistry(target).getTableTransitionKeys();
	}

	public TableTransitionKey lookupTransition(TableTransitionTarget target,String name) {
		if( target == null){
			if( INDEX.getName().equals(name)){
				return INDEX;
			}
		}else{
		for(TableTransitionKey key : getRegistry(target).getTableTransitionKeys()){
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
		if( target instanceof DataObjectFactory){
			DataObjectFactory fac = (DataObjectFactory) target;
			Collection<Composite> comps = fac.getComposites();
			if( ! comps.isEmpty()){
				hb.addHeading(4, "Composites");
				LinkedHashSet<String> names = new LinkedHashSet<String>();
				for( Composite c : comps){
					names.add(c.getClass().getSimpleName());
				}
				hb.addList(names);
			}
		}
		if( target instanceof TableContentProvider){
			((TableContentProvider)target).addSummaryContent(hb);
		}
		getRegistry(target).getTableTransitionSummary(hb,sess );
		return hb;
	}
	public <X extends ContentBuilder> X getTopContent(X hb,TableTransitionTarget target, SessionService<?> sess) {
		return hb;
	}
	public String getHelp(TableTransitionKey key) {
		return key.getHelp();
	}
	public String getText(TableTransitionKey key) {
		return key.toString();
	}
	public TableTransitionKey getIndexTransition() {
		return INDEX;
	}

	public <R> R accept(
			TransitionFactoryVisitor<R,TableTransitionTarget, TableTransitionKey> vis) {
		return vis.visitTransitionProvider(this);
		
	}
	

	
}