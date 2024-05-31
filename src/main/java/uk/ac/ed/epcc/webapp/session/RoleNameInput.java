package uk.ac.ed.epcc.webapp.session;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
/** An input for valid role names.
 * If a {@link SessionService} is passed on construction a the standard role names
 * are generated as auto-complete suggestions.
 * 
 * @author Stephen Booth
 *
 */
public class RoleNameInput extends AutocompleteTextInput<String> {
	public RoleNameInput(SessionService sess) {
		addValidator(new PatternFieldValidator("((@(name:)?)?\\w+)|(\\w+%\\w+(@(name:)?\\w+))"));
		this.sess=sess;
	}

	private final SessionService sess;

	

	
	private Set<String> makeSuggestions() {
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
	
	private Set<String> suggestions = null;
	public Set<String> getSuggestions(){
		if( suggestions == null) {
			suggestions=makeSuggestions();
		}
		return suggestions;
	}

	@Override
	public String getValue(String item) {
		return item;
	}

	@Override
	public String getItembyValue(String value) {
		return value;
	}
}
