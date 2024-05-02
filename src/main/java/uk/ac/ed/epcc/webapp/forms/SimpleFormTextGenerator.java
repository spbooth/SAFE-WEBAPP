package uk.ac.ed.epcc.webapp.forms;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
/** A simple {@link FormTextGenerator} that looks up qualified values from a resource bundle
 * 
 * @author Stephen Booth
 *
 */
public class SimpleFormTextGenerator extends AbstractFormTextGenerator {
    private final Set<String> prefix;
	public SimpleFormTextGenerator(AppContext conn,String ... tags) {
		super(conn);
		prefix = new LinkedHashSet<>();
		for(String s : tags) {
			prefix.add(s);
		}
	
	}

	@Override
	public String getLabel(String field) {
		for(String p : prefix) {
			String lab = getTranslationFromConfig(getContext(), getFormContent(), p,field);
			if( lab != null) {
				return lab;
			}
		}
		return field;
	}

	@Override
	public String getFieldHelp(String field) {
		for(String p : prefix) {
			String help = getHelpTextFromConfig(getContext(), getFormContent(), p, field);
			if( help != null) {
				return help;
			}
		}
		return null;
	}

	@Override
	public String getFieldHint(String field) {
		for(String p : prefix) {
			String help = getHintFromConfig(getContext(), getFormContent(), p, field);
			if( help != null) {
				return help;
			}
		}
		return null;
	}

}
