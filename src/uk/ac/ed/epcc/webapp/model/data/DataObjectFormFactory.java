// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.FormFactory;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ConstantInput;
import uk.ac.ed.epcc.webapp.forms.inputs.DateInput;
import uk.ac.ed.epcc.webapp.forms.inputs.DoubleInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.LongInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.inputs.RealInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.IndexedFormEntry;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Base class for Forms based on DataObject fields such as create/update forms.
 * This class is intended to hold code common to both create and update forms which can 
 * customise their behaviour by overriding the various methods.
 * This class implements default implementations for a target class by calling the equivalent methods in the Factory.
 * However sub-classes can customise further.
 * <p>
 * The class implements {@link IndexedProducer} by forwarding onto the nested factory. This allows custom from factories to be installed in
 * {@link IndexedFormEntry}s to produce specialised creation forms that add entries to an existing factory.
 * @author spb
 *
 * @param <BDO>
 */
public  abstract class DataObjectFormFactory<BDO extends DataObject> implements FormFactory, IndexedProducer<BDO>{
   


protected final DataObjectFactory<BDO> factory;
   protected DataObjectFormFactory(DataObjectFactory<BDO> fac){
	   assert( fac != null );
	   factory=fac;
   }
   public final AppContext getContext(){
	   return factory.getContext();
   }
   public final DataObjectFactory<BDO> getFactory() {
		return factory;
	}
   protected final Logger getLogger(){
	   return factory.getContext().getService(LoggerService.class).getLogger(getClass());
   }
   /**
	 * builds a default Form for editing the DataObjects belonging to this
	 * DataObjectFactory The state of the Form is initialised using getDefaults
	 * 
	 * @param f
	 *            Form to build
	 * @throws DataFault
	 */
	public final void buildForm(Form f) throws DataFault {
		buildForm(getContext(), factory.res,f,getSupress(),getOptional(),getSelectors(),getTranslations());
		customiseForm(f);
		f.setContents(getDefaults());
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
	 */
	public static final void buildForm(AppContext conn, Repository res, Form f, Set<String> supress_fields,
				Set<String> optional, Map<String,Object> selectors,Map<String,String> labels) throws DataFault {
		int maxwid = conn.getIntegerParameter("forms.max_text_input_width", 64);
		Set<String> keys = res.getFields();
        String table = res.getTag();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String name =  it.next();
			Repository.FieldInfo info = res.getInfo(name);

			if (!(supress_fields != null && supress_fields.contains(name)) ) {
				int sql_type = info.getType();
				boolean is_optional;
				if (optional != null) {
					is_optional = optional.contains(name);
				} else {
					// default to follow nullability of field.
					is_optional = info.getNullable();
				}
				is_optional = conn.getBooleanParameter("form.optional."+table+"."+name, is_optional);
				Input<?> input = getInput(conn,selectors,table,name);
				if( input == null ){
				    // build default based on type
					switch (sql_type) {
					case Types.BIGINT:
						input = new LongInput();
						break;
					case Types.INTEGER:
					case Types.SMALLINT:
					case Types.TINYINT:
						input = new IntegerInput();
						break;
					case Types.REAL:
					case Types.FLOAT:
						input = new RealInput();
						break;
					case Types.DOUBLE:
						input = new DoubleInput();
						break;
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
						TextInput ti = new TextInput(info.getNullable());
						ti.setMaxResultLength(info.getMax());
						ti.setBoxWidth(maxwid);
						input = ti;
						break;
					case Types.DATE:
						input = new DateInput(res.getResolution());
						break;
					case Types.BLOB:
					case Types.LONGVARBINARY:
						input = new FileInput();
						break;
					case Types.TIMESTAMP:
						input = new TimeStampInput(res.getResolution());
						break;
					case Types.BOOLEAN:
					case Types.BIT:
						input = new BooleanInput();
						break;
					default:
						throw new DataFault("Unsupported sql.Type: " + sql_type);
					}
				}
				if (input instanceof OptionalInput) {
					OptionalInput ab = (OptionalInput) input;
					ab.setOptional(is_optional);
				}
				if( input instanceof TextInput){
					// If MaxResultLength out of range then
					// set from the DB.
					TextInput ti = (TextInput) input;
					int length=ti.getMaxResultLength();
					int max = info.getMax();
					if( max > 0 && (length <= 0 || length > max)){
						ti.setMaxResultLength(max);
					}
				}
				String lab = name;
				if (labels != null && labels.containsKey(name)) {
					lab = labels.get(name);
				}else{
					lab = conn.getInitParameter("form.label."+table+"."+name,name);
				}
				f.addInput(name, lab, input);
			}
		}

	}
	public static final  Input<?> getInput(AppContext conn,Map<String, Object> selectors, String table,String name) {
		Input<?> input=null;
		Object o = null;
		if (selectors != null && selectors.containsKey(name)) {
			o = selectors.get(name);
			
		}
		if( o == null ){
			// parameter based form override. Note that table cross references are handled in
			// the addSelectors call
			Class<? extends Input> c = conn.getPropertyClass(Input.class,null, "input."+table+"."+name);
			if( c != null ){
				try{
					input = conn.makeObject(c);
				}catch(Exception e){
					conn.error(e,"Failed to make input");
				}
			}
		}
		
		if( o == null ){
			return null;
		}
		if (o instanceof Input) {
			input = (Input) o;
		} else if (o instanceof String) {
			input = new ConstantInput((String) o);
		} else {
			Selector s = (Selector) o;
			input = s.getInput();
		}
		return input;
	}
	/** Calculate a set of optional fields based on field nullability.
	 * 
	 * @param res
	 * @return Set of field names
	 */
	public static Set<String> getOptional(Repository res){
		Set<String> keys = res.getFields();
        Set<String> result = new HashSet<String>();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String name =  it.next();
			Repository.FieldInfo info = res.getInfo(name);
			if( info.getNullable() ){
				result.add(name);
			}
		}
		return result;
	}
	/** Add a set of selectors based on the tables referenced by field keys
	 * @param conn 
	 * @param sel 
	 * @param res
	 * @return modified map
	 */
	public static Map<String,Object> addSelectors(AppContext conn,Map<String,Object> sel,Repository res){
		if( sel == null ){
			sel = new HashMap<String,Object>();
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
									conn.error("Unable to construct "+c.getCanonicalName()+" for table "+ref_table);
								}
							}
						}
						if( o != null ){
							sel.put(field, o);
						}
					}
				}
				
			}else{
				// check for consistency against repo don't allow value sthat will overflow field.
				Object input =  sel.get(field);
				if( input instanceof TextInput && info.isString()){
					int len = info.getMax();
					TextInput text_input = (TextInput)input;
					if( text_input.getMaxResultLength() > len){
						text_input.setMaxResultLength(len);
					}
					if( text_input.getBoxWidth() > len){
						text_input.setBoxWidth(len);
					}
				}
			}
		}
		return sel;
		
	}
	/** Add default translations for reference fields using the table name in
	 * preference to the field name.
	 * 
	 * @param conn
	 * @param trans
	 * @param res
	 * @return Map of translations
	 */
	public static Map<String,String> addTranslations(AppContext conn,Map<String,String> trans,Repository res){
		if( trans == null ){
			trans = new HashMap<String,String>();
		}
		for(String field : res.getFields()){
			if( ! trans.containsKey(field)){
				Repository.FieldInfo info = res.getInfo(field);
				String ref_table = info.getReferencedTable();
				if( ref_table != null ){
					if( trans.get(field)==null){
						trans.put(field,ref_table);
					}
				}
				
			}
			// allow config to override.
			String override=conn.getInitParameter("form."+res.getTag()+"."+field);
			if( override != null){
				trans.put(field, override);
			}
		}
		return trans;
		
	}
	/**
	 * Extension hook to allow additional Form customisation generic to all
	 * types of Form For example adding a FormValidator or adding min, max
	 * values to NumberInputs.
	 * 
	 * @param f
	 *            Form to modify
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
	protected  Map<String,Object> getSelectors() {
		Map<String,Object>sel = factory.getSelectors();
		if( sel == null ){
			sel = new HashMap<String, Object>();
		}
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			sel = c.addSelectors(sel);
		}
		return addSelectors(getContext(), sel, factory.res);
	}
	/**
	 * generate the set of suppressed fields to be used in form creation/update
	 * 
	 * @return Vector
	 */
	protected  Set<String> getSupress() {
		Set<String> supress = factory.getSupress();
		if( supress == null ){
			supress=new HashSet<String>();
		}
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			supress = c.addSuppress(supress);
		}
		return supress;
	}
	protected Set<String> getFields(){
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		Set<String> supress = getSupress();
		for(String field : factory.res.getFields()){
			if( supress == null || ! supress.contains(field)){
				result.add(field);
			}
		}
		return result;
	}
	/**
	 * return a default set of translation between field names and text labels.
	 * 
	 * @return Map
	 */
	protected  Map<String,String> getTranslations() {
		Map<String, String> translations = factory.getTranslations();
		if( translations == null){
			translations=new HashMap<String, String>();
		}
		for(TableStructureContributer c : factory.getTableStructureContributers()){
			translations=c.addTranslations(translations);
		}
		return addTranslations(getContext(),translations,factory.res);
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
			optional=getOptional(factory.res);
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
	public  Map<String, Object> getDefaults() {
		Map<String, Object> defaults = factory.getDefaults();
		if( defaults == null){
			defaults=new HashMap<String, Object>();
		}
		for(TableStructureContributer c: factory.getTableStructureContributers()){
			defaults=c.addDefaults(defaults);
		}
		
		return defaults;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#find(int)
	 */
	@Override
	public final BDO find(int id) throws DataException {
		return factory.find(id);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#getTarget()
	 */
	@Override
	public final Class<? super BDO> getTarget() {
		return factory.getTarget();
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