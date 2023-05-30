package uk.ac.ed.epcc.webapp.forms;

import java.util.ResourceBundle;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
/** Abstract class for implementing {@link FormTextGenerator} using a content bundle
 * 
 * @author Stephen Booth
 *
 */
public abstract class AbstractFormTextGenerator extends AbstractContexed implements FormTextGenerator {
	private ResourceBundle form_content=null;
	public static final String FORM_LABEL_SUFFIX = ".label";
	public static final String FORM_HELP_TEXT_SUFFIX = ".help_text";
	
	public AbstractFormTextGenerator(AppContext conn) {
		super(conn);
		
	}
	public static String getTranslationFromConfig(AppContext conn, ResourceBundle form_content, String qualifier, String field) {
		String key = qualifier+"."+field+FORM_LABEL_SUFFIX;
		if( form_content != null && form_content.containsKey(key)) {
			return form_content.getString(key);
		}
		// fall back to global config
		return conn.getInitParameter(key);
	}
	public static String getHelpTextFromConfig(AppContext conn, ResourceBundle form_content, String qualifier, String field) {
		String key = qualifier+"."+field+FORM_HELP_TEXT_SUFFIX;
		if(  form_content != null && form_content.containsKey(key)) {
			return form_content.getString(key);
		}
		// fall back to global config this also allows parameter expansion
		return conn.getExpandedProperty(key);
	}
	protected ResourceBundle getFormContent() {
		if( form_content == null ) {
			 form_content = conn.getService(MessageBundleService.class).getBundle("form_content");
		}
		return form_content;
	}
}
