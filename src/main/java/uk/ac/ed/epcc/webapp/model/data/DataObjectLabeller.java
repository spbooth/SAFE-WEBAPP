package uk.ac.ed.epcc.webapp.model.data;

import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.factory.FormBuilder;
import uk.ac.ed.epcc.webapp.forms.factory.FormFactory;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.model.data.forms.FieldHelpProvider;
import uk.ac.ed.epcc.webapp.model.data.forms.FormLabelProvider;

/** Class that generates the human readable labels for database fields.
 * 
 * 
 * 
 * @author Stephen Booth
 *
 * @param <BDO>
 */
public class DataObjectLabeller<BDO extends DataObject> implements Contexed{

	public static final String FORM_LABEL_SUFFIX = ".label";
	public static final String FORM_HELP_TEXT_SUFFIX = ".help_text";
	protected final DataObjectFactory<BDO> factory;
	protected final ResourceBundle form_content;

	public static String getTranslationFromConfig(AppContext conn, ResourceBundle form_content, String qualifier, String field) {
		String key = qualifier+"."+field+FORM_LABEL_SUFFIX;
		if( form_content != null && form_content.containsKey(key)) {
			return form_content.getString(key);
		}
		// fall back to global config
		return conn.getInitParameter(key);
	}

	@Override
	public final AppContext getContext() {
		   return factory.getContext();
	   }

	public final DataObjectFactory<BDO> getFactory() {
		return factory;
	}

	protected final Logger getLogger() {
		   return factory.getContext().getService(LoggerService.class).getLogger(getClass());
	   }

	/** Add default translations.
	 * 
	 * For reference fields use the table name in preference to the field name.
	 * Add overrides from config.
	 * @param trans
	 * @return Map of translations
	 */
	public Map<String,String> addTranslations(Map<String,String> trans) {
		if( trans == null ){
			trans = new HashMap<>();
		}
		for(String field : factory.res.getFields()){
			if( ! trans.containsKey(field)){  // no explicit translation
				
				// look for a secondary config value
				String cfg = getConfigTags().get(field);
				String label = null;
				if( cfg != null ) {
					label = getTranslationFromConfig(cfg, field);
				}
				if( label != null ) {
					trans.put(field, label);
				}else {
					// no config value map field name to table-name for refernces
					Repository.FieldInfo info = factory.res.getInfo(field);
					String ref_table = info.getReferencedTable();
					if( ref_table != null ){
						if( trans.get(field)==null){
							String table_label = ref_table;
							// Support global reference labels
							if( form_content.containsKey(ref_table+FORM_LABEL_SUFFIX)) {
								table_label = form_content.getString(ref_table+FORM_LABEL_SUFFIX);
							}
							trans.put(field,table_label);
						}
					}
				}
				
			}
			// allow config to override if using factory tag.
			String override=getTranslationFromConfig(factory.getTag(),field);
			if( override != null){
				trans.put(field, override);
			}
		}
		return trans;
		
	}

	protected Map<String,String> addHelpText(Map<String,String> help) {
		if( help == null ){
			help = new HashMap<>();
		}
		for(String field : factory.res.getFields()){
			if( ! help.containsKey(field)){  // no explicit translation
				
				// look for a secondary config value
				String cfg = getConfigTags().get(field);
				String label = null;
				if( cfg != null ) {
					label = getHelpTextFromConfig(cfg, field);
				}
				if( label != null ) {
					help.put(field, label);
				}
			}
			// allow config to override if using factory tag.
			String override=getHelpTextFromConfig(factory.getTag(),field);
			if( override != null){
				help.put(field, override);
			}
		}
		return help;
		
	}

	private String getTranslationFromConfig(String qualifier, String field) {
		return getTranslationFromConfig(getContext(), form_content, qualifier, field);
	}

	public static String getHelpTextFromConfig(AppContext conn, ResourceBundle form_content, String qualifier, String field) {
		String key = qualifier+"."+field+FORM_HELP_TEXT_SUFFIX;
		if(  form_content != null && form_content.containsKey(key)) {
			return form_content.getString(key);
		}
		// fall back to global config this also allows parameter expansion
		return conn.getExpandedProperty(key);
	}

	private String getHelpTextFromConfig(String qualifier, String field) {
		return getHelpTextFromConfig(getContext(), form_content, qualifier, field);
	}

	/** Set of field config tags 
	 * 
	 */
	private Map<String,String> config_tags = null;

	private Map<String, String> translation_set= null;
	
	private Map<String,String> help_map = null;
	/**
	 * return a default set of translation between field names and text labels.
	 * 
	 * @return Map
	 */
	protected Map<String,String> getTranslations() {
		if( translation_set == null ) {
			Map<String, String> translations = factory.getTranslations();
			if( translations == null){
				translations=new HashMap<>();
			}
			if( factory instanceof FormLabelProvider) {
				((FormLabelProvider)factory).addTranslations(translations);
			}
			for(FormLabelProvider c : factory.getComposites(FormLabelProvider.class)){
				translations=c.addTranslations(translations);
			}
			addTranslations(translations);

			translation_set = Collections.unmodifiableMap(translations);
		}
		return translation_set;
	}

	/** create additional tooltip help text for form fields.
	 * 
	 * @return
	 */
	protected final Map<String,String> getFieldHelp() {
		if( help_map == null) {
			Map<String, String> help = new HashMap<>();
			if( factory instanceof FieldHelpProvider) {
				((FieldHelpProvider)factory).addFieldHelp(help);
			}
			for(FieldHelpProvider c : factory.getComposites(FieldHelpProvider.class)){
				Map mod = c.addFieldHelp(help);
				assert(mod != null);
			}
			addHelpText(help);
			help_map = Collections.unmodifiableMap(help);
		}
		return help_map;
	}

	public DataObjectLabeller(DataObjectFactory<BDO> fac) {
		  assert( fac != null );
		   factory=fac;
		   form_content = fac.getContext().getService(MessageBundleService.class).getBundle("form_content");
	}

	/** Get a map of field names to secondary config tags.
	 * 
	 * @return
	 */
	protected Map<String,String> getConfigTags() {
		if( config_tags == null) {
			config_tags = new HashMap<>();
			try {
				if( factory instanceof FieldHandler) {
					((FieldHandler)factory).addConfigTags(config_tags);
				}
				for(FieldHandler h : factory.getComposites(FieldHandler.class)) {
					h.addConfigTags(config_tags);
				}
			}catch(Exception e) {
				getLogger().error("Error getting config tags", e);
			}
			config_tags = Collections.unmodifiableMap(config_tags);
		}
		return config_tags;
	}
	
	public String getLabel(String field) {
		return getTranslations().get(field);
	}
	
	public String getFieldHelp(String field) {
		return getFieldHelp().get(field);
	}

}