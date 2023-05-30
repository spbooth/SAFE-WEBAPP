package uk.ac.ed.epcc.webapp.forms;

import uk.ac.ed.epcc.webapp.AppContext;
/** A simple {@link FormTextGenerator} that looks up qualified values from a resource bundle
 * 
 * @author Stephen Booth
 *
 */
public class SimpleFormTextGenerator extends AbstractFormTextGenerator {
    private final String prefix;
	public SimpleFormTextGenerator(AppContext conn,String tag) {
		super(conn);
		this.prefix=tag+".";
	}

	@Override
	public String getLabel(String field) {
		String lab = getTranslationFromConfig(getContext(), getFormContent(), prefix, field);
		if( lab != null) {
			return lab;
		}
		return field;
	}

	@Override
	public String getFieldHelp(String field) {
		String help = getHelpTextFromConfig(getContext(), getFormContent(), prefix, field);
		if( help != null) {
			return help;
		}
		return null;
	}

}
