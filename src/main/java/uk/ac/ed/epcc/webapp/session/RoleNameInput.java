package uk.ac.ed.epcc.webapp.session;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete;
import uk.ac.ed.epcc.webapp.forms.inputs.PatternTextInput;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
/** An input for valid role names.
 * If a {@link SessionService} is passed on constuction a the standard role names
 * are generated as auto-complete suggestions.
 * 
 * @author Stephen Booth
 *
 */
public class RoleNameInput extends PatternTextInput implements AutoComplete<String, String> {
	public RoleNameInput(SessionService sess) {
		super("((@(name:)?)?\\w+)|(\\w+%\\w+(@(name:)?\\w+))");
		setSingle(true);
		setTag("");
		this.sess=sess;
	}

	private final SessionService sess;

	@Override
	public String getItembyValue(String value) {
		return value;
	}

	@Override
	public void setItem(String item) {
		setValue(item);
		
	}

	@Override
	public Set<String> getSuggestions() {
		if( sess == null) {
			return new HashSet<String>();
		}
		Set set = sess.getStandardRoles();
		AppUserFactory fac = sess.getLoginFactory();
		NamedFilterWrapper w = new NamedFilterWrapper(fac);
		Set<String> named_filter = new HashSet<String>();
		w.addFilterNames(named_filter);
		for(String n : named_filter) {
			set.add("@"+n);
		}
		return set;
	}

	@Override
	public String getValue(String item) {
		return item;
	}

	@Override
	public String getSuggestionText(String item) {
		return item;
	}
}
