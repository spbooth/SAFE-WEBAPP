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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
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
import uk.ac.ed.epcc.webapp.model.data.ConfigParamProvider;
import uk.ac.ed.epcc.webapp.model.data.ConfigTransition;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.UnDumper;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Class implementing transitions on factory classes identified by table.
 * The target factory must have its
 * class registered as a configuration property.
 * This TransitionProvider needs to be registered under the name <b>Table</b> 
 * to enable this feature.
 * 
 * @author spb
 *
 */


public class TableTransitionProvider  extends AbstractContexed implements ViewTransitionProvider<TableTransitionKey,DataObjectFactory>, IndexTransitionProvider<TableTransitionKey, DataObjectFactory>{
	
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

	static final TableTransitionKey ADD_BOOLEAN_FIELD_KEY = new TableDeveloperKey("AddBooleanField");
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
    }
    
    private Map<TableTransitionKey,Transition> getTransitionMap(DataObjectFactory target){
    	LinkedHashMap<TableTransitionKey, Transition> map = new LinkedHashMap<>();
    	if( target == null ) {
    		map.put(INDEX, new IndexTransition());
    	}
    	if( UPLOAD_XML_FEATURE.isEnabled(conn)) {
    		map.put(UPLOAD, new UploadTransition());
    	}
    	map.put(DROP_TABLE_KEY,new ConfirmTransition<>(
			     "Delete this table ? (all data will be lost)", 
			     new DropTableTransition<>(conn), 
			     new ForwardTransition<DataObjectFactory>(new MessageResult("aborted"))) );
		map.put(ADD_FOREIGN_KEYS_KEY,new ConfirmTransition<>(
			     "Add Foreign Key definitions?", 
			     new AddForeignKeyTransition<>(), 
			     new ForwardTransition<DataObjectFactory>(new MessageResult("aborted"))) );
	
		map.put(DROP_FIELD_KEY, new DropFieldTransition<>());
		map.put(DROP_INDEX_KEY, new DropIndexTransition<>());
		map.put(DROP_FOREIGN_KEY_KEY, new DropForeignKeyTransition<>());
		map.put(ADD_REFERENCE_FIELD_KEY, new AddReferenceTransition<>());
		map.put(ADD_DATE_FIELD_KEY, new AddDateFieldTransition<>());
		map.put(ADD_TEXT_FIELD_KEY, new AddTextFieldTransition<>());
		map.put(ADD_INTEGER_FIELD_KEY, new AddIntegerFieldTransition<>());
		map.put(ADD_LONG_FIELD_KEY, new AddLongFieldTransition<>());
		map.put(ADD_FLOAT_FIELD_KEY, new AddFloatFieldTransition<>());
		map.put(ADD_DOUBLE_FIELD_KEY, new AddDoubleFieldTransition<>());
		map.put(ADD_BOOLEAN_FIELD_KEY,new AddBooleanFieldTransition<>());
		if( target == null) {
			return map;
		}
		if( target.getTableSpecification() != null ) {
			map.put(ADD_STD_FIELD, new AddStdFieldTransition<>());
			map.put(ADD_STD_INDEX, new AddStdIndexTransition<>());
			map.put(DROP_OPTIONAL_FIELD, new DropOptionalFieldTransition<>());
		}
		if(target instanceof TableTransitionContributor) {
			map.putAll(((TableTransitionContributor)target).getTableTransitions());
		}
		for(TableTransitionContributor c : ((DataObjectFactory<?>)target).getComposites(TableTransitionContributor.class)) {
			map.putAll(c.getTableTransitions());
		}
		Set<String> configs = getConfigProperties(target);
		if( configs != null && ! configs.isEmpty()){
			map.put(new AdminOperationKey( "Configure","Edit configuration parameters directly"), new ConfigTransition(getContext(), configs));
		}
    	return map;
    }
    /** get a set of configuration parameters that configure this object.
	 * This is used to produce a generic configuration table transtition.
	 * 
	 * @return
	 */
	public Set<String> getConfigProperties(DataObjectFactory<?> target){
		Set<String> params = new  LinkedHashSet<>();
		if( target instanceof ConfigParamProvider) {
			((ConfigParamProvider)target).addConfigParameters(params);
		}
		for(ConfigParamProvider c : target.getComposites(ConfigParamProvider.class)) {
			c.addConfigParameters(params);
		}
		return params;
	}
    public class IndexTransition extends AbstractTargetLessTransition<DataObjectFactory>{
		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			f.addInput("table", TABLE_TRANSITION_TAG, new TableInput<>(c, DataObjectFactory.class));
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
					return new IndexTransitionResult<>(TableTransitionProvider.this);
				}
			});
		}
    	
    }
    
	@Override
	public boolean allowTransition(AppContext c,DataObjectFactory target,
			TableTransitionKey name) {
		SessionService sess = c.getService(SessionService.class);
		if( name == null) {
			return canView(target, sess);
		}
		
		if( name.allow(sess,target)) {
			return true;
		}
		return false;
	}

	@Override
	public String getID(DataObjectFactory target) {
		if( target == null){
			return null;
		}
		return target.getTag();
	}

	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,DataObjectFactory target) {
		return getLogContent(cb, target, c.getService(SessionService.class));
	}


	@Override
	public DataObjectFactory getTarget(String id) {
		if( id == null || id.length() == 0){
			return null;
		}
		return getContext().makeObjectWithDefault(DataObjectFactory.class, null,id);
	}

	@Override
	public String getTargetName() {
		return TABLE_TRANSITION_TAG;
	}
	
	@Override
	public boolean canView(DataObjectFactory target, SessionService<?> sess) {
		
		return sess.hasRoleFromList(SessionService.ADMIN_ROLE,TABLE_INDEX_ROLE);
	}
	@Override
	public <X extends ContentBuilder> X getLogContent(X hb,DataObjectFactory target, SessionService<?> sess) {
		hb.addHeading(2, "Table "+target.getTag());
		hb.addHeading(3,"Table Type:"+target.getClass().getSimpleName());


		Collection<Composite> comps = target.getComposites();
		if( ! comps.isEmpty()){
			hb.addHeading(4, "Composites");
			LinkedHashSet<String> names = new LinkedHashSet<>();
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
		Set<String> configs = getConfigProperties(target);
		if( configs != null && ! configs.isEmpty()){
			hb.addHeading(3, "Configuration parameters");
			Table t = new Table();
			for(String param : configs){
				t.put("Value",param, getContext().getInitParameter(param, "Not-set"));
			}
			t.setKeyName("Parameter");
			hb.addTable(getContext(), t);
		}
		return hb;
	}
	

	@Override
	public <X extends ContentBuilder> X getTopContent(X hb,DataObjectFactory target, SessionService<?> sess) {
		return hb;
	}
	@Override
	public <X extends ContentBuilder> X getBottomContent(X hb,DataObjectFactory target, SessionService<?> sess) {
		return hb;
	}
	@Override
	public String getHelp(TableTransitionKey key) {
		return key.getHelp();
	}
	@Override
	public String getText(TableTransitionKey key) {
		return key.toString();
	}
	@Override
	public TableTransitionKey getIndexTransition() {
		return INDEX;
	}

	@Override
	public <R> R accept(
			TransitionFactoryVisitor<R,DataObjectFactory, TableTransitionKey> vis) {
		return vis.visitTransitionProvider(this);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTransitions(java.lang.Object)
	 */
	@Override
	public Set<TableTransitionKey> getTransitions(DataObjectFactory target) {
		return getTransitionMap(target).keySet();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTransition(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Transition<DataObjectFactory> getTransition(DataObjectFactory target, TableTransitionKey key) {
		return (Transition<DataObjectFactory>) getTransitionMap(target).get(key);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#lookupTransition(java.lang.Object, java.lang.String)
	 */
	@Override
	public TableTransitionKey lookupTransition(DataObjectFactory target, String name) {
		for(TableTransitionKey key : getTransitions(target)) {
			if( key.getName().equals(name)) {
				return key;
			}
		}
		return null;
	}
}