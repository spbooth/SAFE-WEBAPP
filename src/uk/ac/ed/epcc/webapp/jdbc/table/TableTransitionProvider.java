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
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ForwardTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.UnDumper;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractTransitionProvider;
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


public class TableTransitionProvider extends AbstractTransitionProvider<DataObjectFactory, TableTransitionKey> implements ViewTransitionProvider<TableTransitionKey,DataObjectFactory>, IndexTransitionProvider<TableTransitionKey, DataObjectFactory>,Contexed{
	
	public static final Feature UPLOAD_XML_FEATURE = new Feature("table_transition.upload_xml",false,"Allow XML tables to be uploaded via a transition");
    /**
	 * 
	 */
	public static final String TABLE_INDEX_ROLE = "TableIndex";

	/**
	 * 
	 */
	public static final String TABLE_TRANSITION_TAG = "Table";

    
    public static final TableTransitionKey INDEX = new TableTransitionKey(DataObjectFactory.class, "Index"){

		@Override
		public boolean allow(SessionService serv, DataObjectFactory target) {
			return serv.hasRoleFromList(SessionService.ADMIN_ROLE,TABLE_INDEX_ROLE);
		}
    	
    };
    public static final TableTransitionKey UPLOAD = new AdminOperationKey(DataObjectFactory.class, "Upload");
    /**
	 * 
	 */
	static final TableTransitionKey ADD_FOREIGN_KEYS_KEY = new TableDeveloperKey("AddForeignKeys");
	/**
	 * 
	 */
	static final TableTransitionKey DROP_TABLE_KEY = new TableDeveloperKey("DropTable");
	/**
	 * 
	 */
	static final TableTransitionKey DROP_FIELD_KEY = new TableDeveloperKey("DropField");
	/**
	 * 
	 */
	static final TableTransitionKey DROP_INDEX_KEY = new TableDeveloperKey("DropIndex");
	/**
	 * 
	 */
	static final TableTransitionKey DROP_FOREIGN_KEY_KEY = new TableDeveloperKey("DropForeignKey");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_REFERENCE_FIELD_KEY = new TableDeveloperKey("AddReferenceField");
	
	/**
	 * 
	 */
	static final TableTransitionKey ADD_DATE_FIELD_KEY = new TableDeveloperKey("AddDateField");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_TEXT_FIELD_KEY = new TableDeveloperKey("AddTextField");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_INTEGER_FIELD_KEY = new TableDeveloperKey("AddIntegerField");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_LONG_FIELD_KEY = new TableDeveloperKey("AddLongField");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_FLOAT_FIELD_KEY = new TableDeveloperKey("AddFloatField");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_DOUBLE_FIELD_KEY = new TableDeveloperKey("AddDoubleField");

	/**
	 * 
	 */
	static final TableTransitionKey ADD_STD_INDEX = new TableStructureAdminOperationKey("Add Std index", "Add missing index from the default table specification for this class");
	/**
	 * 
	 */
	static final TableTransitionKey ADD_STD_FIELD = new TableStructureAdminOperationKey("Add Std field","Add missing fields from the default table specification for this class");
	static final TableTransitionKey DROP_OPTIONAL_FIELD =new TableStructureAdminOperationKey("Drop optional field","Drop optional existing field");

    public TableTransitionProvider(AppContext conn){
    	super(conn);
    	addTransition(INDEX, new IndexTransition());
    	if( UPLOAD_XML_FEATURE.isEnabled(conn)) {
    		addTransition(UPLOAD, new UploadTransition());
    	}
    	addTransition(DROP_TABLE_KEY,new ConfirmTransition<DataObjectFactory>(
			     "Delete this table ? (all data will be lost)", 
			     new DropTableTransition<DataObjectFactory>(conn), 
			     new ForwardTransition<DataObjectFactory>(new MessageResult("aborted"))) );
		addTransition(ADD_FOREIGN_KEYS_KEY,new ConfirmTransition<DataObjectFactory>(
			     "Add Foreign Key definitions?", 
			     new AddForeignKeyTransition<DataObjectFactory>(), 
			     new ForwardTransition<DataObjectFactory>(new MessageResult("aborted"))) );
	
		addTransition(DROP_FIELD_KEY, new DropFieldTransition<DataObjectFactory>());
		addTransition(DROP_INDEX_KEY, new DropIndexTransition<DataObjectFactory>());
		addTransition(DROP_FOREIGN_KEY_KEY, new DropForeignKeyTransition<DataObjectFactory>());
		addTransition(ADD_REFERENCE_FIELD_KEY, new AddReferenceTransition<DataObjectFactory>());
		addTransition(ADD_DATE_FIELD_KEY, new AddDateFieldTransition<DataObjectFactory>());
		addTransition(ADD_TEXT_FIELD_KEY, new AddTextFieldTransition<DataObjectFactory>());
		addTransition(ADD_INTEGER_FIELD_KEY, new AddIntegerFieldTransition<DataObjectFactory>());
		addTransition(ADD_LONG_FIELD_KEY, new AddLongFieldTransition<DataObjectFactory>());
		addTransition(ADD_FLOAT_FIELD_KEY, new AddFloatFieldTransition<DataObjectFactory>());
		addTransition(ADD_DOUBLE_FIELD_KEY, new AddDoubleFieldTransition<DataObjectFactory>());
		addTransition(ADD_STD_FIELD, new AddStdFieldTransition<DataObjectFactory>());
		addTransition(ADD_STD_INDEX, new AddStdIndexTransition<>());
		addTransition(DROP_OPTIONAL_FIELD, new DropOptionalFieldTransition<>());
    }
    public class IndexTransition extends AbstractTargetLessTransition<DataObjectFactory>{
		public void buildForm(Form f, AppContext c) throws TransitionException {
			f.addInput("table", TABLE_TRANSITION_TAG, new TableInput<DataObjectFactory>(c, DataObjectFactory.class));
			f.addAction("View", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					String table = (String) f.get("table");
					DataObjectFactory target = getContext().makeObject(DataObjectFactory.class, table);
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
    public class UploadTransition extends AbstractTargetLessTransition<DataObjectFactory>{

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
					return new IndexTransitionResult<DataObjectFactory,TableTransitionKey>(TableTransitionProvider.this);
				}
			});
		}
    	
    }
    
	public boolean allowTransition(AppContext c,DataObjectFactory target,
			TableTransitionKey name) {
		if( name.allow(c.getService(SessionService.class),target)) {
			return true;
		}
		return false;
	}

	public String getID(DataObjectFactory target) {
		if( target == null){
			return null;
		}
		return target.getTag();
	}

	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,DataObjectFactory target) {
		return getLogContent(cb, target, c.getService(SessionService.class));
	}


	public DataObjectFactory getTarget(String id) {
		if( id == null || id.length() == 0){
			return null;
		}
		return getContext().makeObjectWithDefault(DataObjectFactory.class, null,id);
	}

	public String getTargetName() {
		return TABLE_TRANSITION_TAG;
	}
	
	public boolean canView(DataObjectFactory target, SessionService<?> sess) {
		
		return sess.hasRoleFromList(SessionService.ADMIN_ROLE,TABLE_INDEX_ROLE);
	}
	public <X extends ContentBuilder> X getLogContent(X hb,DataObjectFactory target, SessionService<?> sess) {
		hb.addHeading(2, "Table "+target.getTag());
		hb.addHeading(3,"Table Type:"+target.getClass().getSimpleName());


		Collection<Composite> comps = target.getComposites();
		if( ! comps.isEmpty()){
			hb.addHeading(4, "Composites");
			LinkedHashSet<String> names = new LinkedHashSet<String>();
			for( Composite c : comps){
				names.add(c.toString());
			}
			hb.addList(names);
		}
		if( target instanceof TableContentProvider) {
			((TableContentProvider)target).addSummaryContent(hb);
		}
		for(TableContentProvider prov : ((DataObjectFactory<?>)target).getComposites(TableContentProvider.class)) {
			prov.addSummaryContent(hb);
		}
		return hb;
	}
	public <X extends ContentBuilder> X getTopContent(X hb,DataObjectFactory target, SessionService<?> sess) {
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
			TransitionFactoryVisitor<R,DataObjectFactory, TableTransitionKey> vis) {
		return vis.visitTransitionProvider(this);
		
	}
	

	
}