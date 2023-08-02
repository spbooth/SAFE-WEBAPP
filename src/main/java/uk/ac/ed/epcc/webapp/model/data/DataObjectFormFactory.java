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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.Types;
import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.FormFactory;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.IndexedFormEntry;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;
import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;
/** Base class for Forms based on DataObject fields such as create/update forms.
 * This class is intended to hold code common to both create and update forms which can 
 * customise their behaviour by overriding the various methods.
 * This class implements default implementations for a target class by calling the equivalent methods in the Factory.
 * However sub-classes can customise further.
 * <p>
 * The class implements {@link IndexedProducer} by forwarding onto the nested factory. This allows custom from factories to be installed in
 * {@link IndexedFormEntry}s to produce specialised creation forms that add entries to an existing factory.
 * <p>
 * Note that if {@link FieldConstraint}s are defined form building may return early (returning false from the buildForm methods)
 * indicating that multi-stage forms are being used.
 * The customise methods are usually still called so these (or any validators they add) may need to be tolerant about missing fields
 * when using FieldConstraints
 * @author spb
 *
 * @param <BDO>
 */
public  abstract class DataObjectFormFactory<BDO extends DataObject> extends DataObjectLabeller<BDO> implements FormFactory, FormBuilder, IndexedProducer<BDO>{
   public static final Feature DEFAULT_FORBID_HTML = new Feature("form_factory.default_forbid_html_text",true,"Forbid HTML in auto generated text inputs for database fields");
   public static final Feature DEFER_CONTENT = new Feature("form_factory.defer_content",true,"Defer form label generation till needed.");

protected DataObjectFormFactory(DataObjectFactory<BDO> fac){
	 super(fac);
   }
   /**
	 * builds a default Form for creating new objects.
	 * default values are taken from {@link #getCreationDefaults()}
	 * 
	 * @param f
	 *            Form to build
	 * @throws DataFault
	 */
	public final boolean buildForm(Form f) throws DataFault{
		return buildForm(f,null,getCreationDefaults());
	}
	
	/** builds a default Form for editing the DataObjects belonging to this
	 * {@link DataObjectFactory}
	 * 
	 * @param f {@link Form} to build
	 * @param fixtures Map of fixed form values
	 * @param defaults Map of default values for editable fields
	 *   
	 * @throws DataFault
	 */
	@Override
	public final boolean buildForm(Form f,HashMap fixtures,Map<String,Object> defaults) throws DataFault{
		try(TimeClosable build = new TimeClosable(getContext(), "buildForm")){
			f.setFormTextGenerator(this);
			boolean defer = DEFER_CONTENT.isEnabled(getContext());
			boolean complete = buildForm(getContext(), factory.res,getFields(),f,getOptional(), getSelectors(),getValidators(),getFieldConstraints(),defer ? null :getTranslations(),defer ? null :getFieldHelp(),fixtures,defaults);
			customiseForm(f);
			return complete;
		}
	}
	/**
	 * Construct an edit Form for the associated DataObject based on database
	 * meta-data
	 * @param conn AppContext
	 * @param res Repository to use as template
	 * 
	 * @param f
	 *            Form to build
	 * @param supress_fields
	 *            Vector of fields to supress in the form
	 * @param optional
	 *            Vector marking fields as optional
	 * @param selectors
	 *            Map of selectors to use.
	 * @param labels
	 * 	          Map of field names to form labels
	 * @throws DataFault
	
	 * @return
	 * @throws DataFault
	 */
	public static final boolean buildForm(AppContext conn, Repository res, Form f, Set<String> supress_fields,
			Set<String> optional, Map<String,Selector> selectors,Map<String,String> labels) throws DataFault {
		Set<String> keys = new LinkedHashSet<String>(res.getFields());
		if( supress_fields != null ) {
			keys.removeAll(supress_fields);
		}
		return buildForm(conn, res, keys,f, optional, selectors,new LinkedHashMap<String, FieldValidationSet>(),(Map<String, FieldConstraint>) null,labels, (Map<String, String>) null, null,null);
	}
	/**
	 * Construct an edit Form for the associated DataObject based on database
	 * meta-data
	 * @param conn AppContext
	 * @param res Repository to use as template
	 * 
	 * @param f
	 *            Form to build
	 * @param optional
	 *            Vector marking fields as optional
	 * @param selectors
	 *            Map of selectors to use.
	 * @param labels
	 * 	          Map of field names to form labels
	 * @param tooltips 
	 * 			  Map of tooltip/help text for form labels
	 * 
	 * @return boolean true if the form is complete.
	 * @throws DataFault
	 */
	public static boolean buildForm(AppContext conn,Repository res, Set<String> keys, Form f, 
				Set<String> optional, Map<String,Selector> selectors,Map<String,FieldValidationSet> validators,Map<String,FieldConstraint> constraints,Map<String,String> labels,Map<String,String> tooltips,Map<String,Object> fixtures,Map<String,Object> defaults) throws DataFault {
		//
		String table = res.getTag();
		boolean support_multi_stage = f.supportsMultiStage();
		if( fixtures == null && constraints != null ) {
			fixtures = new HashMap<>();
		}
		// copy defaults to fixtures for any
		// value not in the key-set or not already set as fixtures.
		// This is to allow an update form with supressed fields access
		// to the supressed data in FieldConstraints
		if( defaults != null && fixtures != null) {
			for(Map.Entry<String,Object> e : defaults.entrySet()) {
				if( ! keys.contains(e.getKey())  && ! fixtures.containsKey(e.getKey())) {
					fixtures.put(e.getKey(), e.getValue());
				}
			}
		}
		
		// Try multiple form stages until we have no fields left
		while( ! keys.isEmpty() ) {
			int start = keys.size();
			boolean multi_stage=false;
			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				String name =  it.next();
				Repository.FieldInfo info = res.getInfo(name);


				//
				boolean is_optional;
				if (optional != null) {
					// Don't set optional unless the DB allows this
					// for legacy reasons a non nullable string field will map to
					// the empty string if marked optional
					is_optional = optional.contains(name) && (info == null || info.isString() || info.getNullable());
				} else {
					// default to follow nullability of field.
					is_optional = info != null && info.getNullable();
				}
				// override can set a non-nullable field to optional
				// this allows default values set post-create.
				// The other alternative is to change the field to optional in form customisation
				is_optional = conn.getBooleanParameter("form.optional."+table+"."+name, is_optional);
				Selector sel = selectors == null ? null : selectors.get(name);
				if( sel == null ) {
					// fallback routes
					sel = new Selector() {

						@Override
						public Input getInput() {
							Input i = getInputFromName(conn, table, name);
							if( i == null ) {
								i = getInputFromType(conn, res, info);
							}
							return i;
						}

					};
				}
				boolean emit_input = true;
				Object def = null;
				if( defaults != null) {
					def = defaults.get(name);
					
				}
				FieldValidationSet validator_set = validators.get(name);
				if( validator_set == null) {
					validator_set= new FieldValidationSet<>();
				}
				// Consider field constraints
				if( constraints != null && constraints.containsKey(name)) {
					FieldConstraint fc = constraints.get(name);
					if( fc.suppress(fixtures)) {
						emit_input=false;
						it.remove();
					}else if( fc.requestMultiStage(fixtures)) {
						// multi-stage requested
						if( support_multi_stage ) {
							emit_input=false;  // skip this input
							multi_stage=true;  // do the request
						}else {
							FormValidator fv = fc.getFormValidator();
							if( fc != null) {
								f.addValidator(fv);
							}
						}
					}else {
						sel = fc.changeSelector(sel, fixtures);
						is_optional = fc.changeOptional( is_optional,fixtures);
						def = fc.defaultValue(def, fixtures);
						validator_set = fc.validationSet(validator_set, fixtures);
					}
				}

				if( emit_input ) {
					Input<?> input = sel.getInput();
					
					if( input == null) {
						throw new DataFault("Unable to create input for "+name);
					}
					
					input.addValidatorSet(validator_set);
					String lab = null;
					if (labels != null ) {
						if( labels.containsKey(name)) {

							lab = labels.get(name);
						}else{
							// This is a fall-back that should only be invoked if the static methods
							// are called by an external class
							// a null labels param turns this off as it implied label generation is deferred
							lab = getTranslationFromConfig(conn,conn.getService(MessageBundleService.class).getBundle("form_content"),table, name);
						}
					}
					String tooltip=null;
					if( tooltips != null ) {
						if( tooltips.containsKey(name)) {

							tooltip = tooltips.get(name);
						}else {
							// This is a fall-back that should only be invoked if the static methods
							// are called by an external class
							// a null tooltips param turns this off as it implied label generation is deferred
							tooltip = getHelpTextFromConfig(conn,conn.getService(MessageBundleService.class).getBundle("form_content"),table, name);
						}
					}
					f.addInput(name, lab,tooltip, input).setOptional(is_optional);
					
					if( fixtures != null ) {
						if( f.isFixed(name) ) {
							// pre-emptive copy of fixed value to fixtures
							f.getField(name).lock();
							fixtures.put(name,f.get(name));
						}else if ( fixtures.containsKey(name)) {
							// externally supplied fixture 
							f.put(name, fixtures.get(name));
							f.getField(name).lock();
						}
					}
					if( def != null ) {
						f.put(name, def);
					}
					it.remove(); // field has been processed
				}
			}
			if( start == keys.size()) {
				// No additional inputs have been added this pass
				conn.getService(LoggerService.class).getLogger(DataObjectFormFactory.class).error("No additional inputs added");
				support_multi_stage=false; // This should emit the remaining inputs next pass
				multi_stage=false; // should be false anyway
			}
			if( multi_stage ) {
				try {
					if( ! f.poll() ) {
						return false;
					}
					if( fixtures != null ) {
						// update the fixtures
						for(Field existing : f) {
							if( existing.isFixed()) {  // this will update the value to the forced value
								existing.lock(); // stop value being updated when form populates
								fixtures.put(existing.getKey(),existing.getValue());
							}
						}
					}
				} catch (TransitionException e) {
					conn.getService(LoggerService.class).getLogger(DataObjectFormFactory.class).error("Form poll failed",e);
					support_multi_stage=false; // This should emit the remaining inputs next pass
				}
			}
		}
		return true;
	}
	public static Input<?> getInputFromType(AppContext conn, Repository res,  Repository.FieldInfo info) {
		int sql_type = info.getType();
		// build default based on type
		switch (sql_type) {
		case Types.BIGINT:
			return new LongInput();
			
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			return new IntegerInput();
			
		case Types.REAL:
		case Types.FLOAT:
			return new RealInput();
			
		case Types.DOUBLE:
			return new DoubleInput();
			
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			TextInput ti;
			if( DEFAULT_FORBID_HTML.isEnabled(conn)){
				ti = new NoHtmlInput();
			}else{
				ti= new TextInput();
			}
			ti.addValidator(new MaxLengthValidator(info.getMax()));
			int maxwid = conn.getIntegerParameter("forms.max_text_input_width", 64);
			ti.setBoxWidth(maxwid);
			return ti;
			
		case Types.DATE:
			return new DateInput(res.getResolution());
			
		case Types.BLOB:
		case Types.LONGVARBINARY:
			return new FileInput();
			
		case Types.TIMESTAMP:
			return new TimeStampInput(res.getResolution());
			
		case Types.BOOLEAN:
		case Types.BIT:
			return new BooleanInput();
			
		default:
			return null;
		}
	}
	
	public static Input<?> getInputFromName(AppContext conn, String table, String name) {
		// parameter based form override. Note that table cross references are handled in
		// the addSelectors call
		Class<? extends Input> c = conn.getPropertyClass(Input.class,null, "input."+table+"."+name);
		if( c != null ){
			try{
				return conn.makeObject(c);
			}catch(Exception e){
				conn.getService(LoggerService.class).getLogger(DataObjectFormFactory.class).error("Failed to make input",e);
				
			}
		}
		return null;
	}
	
	/** Add a set of selectors based on the tables referenced by field keys
	 * @param conn 
	 * @param sel 
	 * @param res
	 * @return modified map
	 */
	public static Map<String,Selector> addSelectors(AppContext conn,Map<String,Selector> sel,Repository res){
		if( sel == null ){
			sel = new HashMap<>();
		}
		for(String field : res.getFields()){
			Repository.FieldInfo info = res.getInfo(field);
			if( ! sel.containsKey(field)){		
				if( info.getTypeProducer() != null ){
					// Some TypeProducers also implement selector.
					// check these before referenced table as 
					// 
					TypeProducer prod = info.getTypeProducer();
					if( prod instanceof Selector){
						sel.put(field, (Selector) prod);
					}
				}else{
					String ref_table = info.getReferencedTable();
					if( ref_table != null ){
						Selector o = null;
						Class<? extends Selector> c = conn.getPropertyClass(Selector.class, null,ref_table);
						if( c != null ){
							try {
								o = conn.makeParamObject(c,conn,ref_table );
							} catch (Exception e) {
								try {
									o = conn.makeObject(c);
								} catch (Exception e1) {
									conn.getService(LoggerService.class).getLogger(DataObjectFormFactory.class).error("Unable to construct "+c.getCanonicalName()+" for table "+ref_table);
								}
							}
						}
						if( o != null ){
							sel.put(field, o);
						}
					}
				}
				
			}
		}
		return sel;
		
	}
	
	/** Add validators based on the database
	 * @param conn 
	 * @param val 
	 * @param res
	 * @return modified map
	 */
	public static Map<String,FieldValidationSet> addValidators(AppContext conn,Map<String,FieldValidationSet> val,Repository res){
		if( val == null ){
			val = new HashMap<>();
		}
		for(String field : res.getFields()){
			Repository.FieldInfo info = res.getInfo(field);
			if( info.isString() && ! info.isTruncate()) {
				FieldValidationSet.add(val, field, new MaxLengthValidator(info.getMax()));
			}
		}
		return val;
		
	}
	/**
	 * Extension hook to allow additional Form customisation generic to all
	 * types of Form For example adding a FormValidator or adding min, max
	 * values to NumberInputs.
	 * 
	 * 
	 * 
	 * @param f {@link Form} to modify
	 */
	public  void customiseForm(Form f) {
           factory.customiseForm(f);
           for(TableStructureContributer c : factory.getTableStructureContributers()){
        	   c.customiseForm(f);
           }
	}
	/**
	 * Get a Map of selectors to use for forms of this type.
	 * 
	 * @return Map
	 */
	protected  Map<String,Selector> getSelectors() {
		Map<String,Selector>sel = factory.getSelectors();
		if( sel == null ){
			sel = new HashMap<>();
		}
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			sel = c.addSelectors(sel);
		}
		return addSelectors(getContext(), sel, factory.res);
	}
	
	/**
	 * Get a Map of {@link FieldValidationSet} to use for forms of this type.
	 * 
	 * @return Map
	 */
	protected  Map<String,FieldValidationSet> getValidators() {
		Map<String,FieldValidationSet>val = factory.getValidators();
		if( val == null ){
			val = new HashMap<String, FieldValidationSet>();
		}
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			val = c.addFieldValidations(val);
		}
		return addValidators(getContext(), val, factory.res);
	}
	/**
	 * generate the set of suppressed fields to be used in form creation/update
	 * 
	 * @return Vector
	 */
	protected  Set<String> getSupress() {
		Set<String> supress = factory.getSupress();
		if( supress == null ){
			supress=new HashSet<>();
		}
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			supress = c.addSuppress(supress);
		}
		return supress;
	}
	
	
	protected Set<String> getFields(){
		Set<String> result = new LinkedHashSet<>();
		Set<String> supress = getSupress();
		for(String field : factory.res.getFields()){
			if( supress == null || ! supress.contains(field)){
				result.add(field);
			}
		}
		for(TableStructureContributer<?> c : factory.getTableStructureContributers()){
			result = c.addFormFields(result);
		}
		return result;
	}
	/**
	 * Generate the set of optional fields to be used in form creation/update
	 * default behaviour is to take the set defined by the factory
	 * (or if this is null fields that can be null in the database) modified by 
	 * the {@link Composite} classes.
	 * most classes will override with an explicit list of optional fields.
	 * 
	 * If this method is overridden to return an empty set no fields are optional.
	 * If it is overridden to return null then fields tha can take null values are optional.
	 * 
	 * @return {@link Set} of field names
	 */
	protected  Set<String> getOptional() {
		Set<String> optional = factory.getOptional();
		if( optional == null ){
			// This is to provide a default to be modified by
			// the composites.
			// If a sub-class overrides this method to return null
			// the buildForm method will also check for nullable fields directly
			//
			optional=factory.getNullable();
		}
		optional = addOptionalFromComposites(optional);
		return optional;
	}
	
	/**
	 * @param optional
	 * @return
	 */
	protected Set<String> addOptionalFromComposites(Set<String> optional) {
		for(TableStructureContributer c: factory.getTableStructureContributers()){
			optional=c.addOptional(optional);
		}
		return optional;
	}

	/** Get a set of default values for the form fields 
	 * 
	 * @return Map of defaults
	 */
	public  Map<String, Object> getCreationDefaults() {
		Map<String, Object> defaults = factory.getDefaults();
		if( defaults == null){
			defaults=new HashMap<>();
		}
		for(TableStructureContributer c: factory.getTableStructureContributers()){
			defaults=c.addDefaults(defaults);
		}
		
		return defaults;
	}

	/** get {@link FieldConstraint}s to apply when building the form
	 * 
	 * @return
	 */
	protected Map<String,FieldConstraint> getFieldConstraints(){
		Map<String,FieldConstraint> cst = factory.getFieldConstraints();
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			 cst = c.addFieldConstraints(cst);
		}
		return cst;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#find(int)
	 */
	@Override
	public final BDO find(int id) throws DataException {
		return factory.find(id);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#makeReference(uk.ac.ed.epcc.webapp.Indexed)
	 */
	@Override
	public final IndexedReference<BDO> makeReference(BDO obj) {
		return factory.makeReference(obj);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#makeReference(int)
	 */
	@Override
	public final IndexedReference<BDO> makeReference(int id) {
		return factory.makeReference(id);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#isMyReference(uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference)
	 */
	@Override
	public final boolean isMyReference(IndexedReference ref) {
		return factory.isMyReference(ref);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#getID(uk.ac.ed.epcc.webapp.Indexed)
	 */
	@Override
	public final  String getID(BDO obj) {
		return factory.getID(obj);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#find(java.lang.Object)
	 */
	@Override
	public final BDO find(Number o) {
		return factory.find(o);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#getIndex(java.lang.Object)
	 */
	@Override
	public final Number getIndex(BDO value) {
		return factory.getIndex(value);
	}
}