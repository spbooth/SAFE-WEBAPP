package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;
/** A {@link TextInput} that takes a comma separated list of values that must come
 * from a defined set
 * 
 * @author Stephen Booth
 *
 */
public class SetTextInput extends TextInput implements PatternInput, ItemInput<String, Set<String>> {
	private static final String SPLIT = "\\s*,\\s*";
	private Set<String> allowed = new LinkedHashSet<String>();
	/** Construct input
	 * 
	 * @param list comma seperated list of allowed values
	 */
	public SetTextInput(String list) {
		for(String a : list.split(SPLIT)) {
			allowed.add(a);
		}
		addValidator(new SingleLineFieldValidator() {
			
			@Override
			public void validate(String data) throws FieldException {
				if( data != null) {
					for(String s : data.split(SPLIT)) {
						if( ! allowed.contains(s)) {
							throw new ValidateException("Value "+s+" not allowed");
						}
					}
				}
				
			}
		});
	}
	
	@Override
	public String getPattern() {
		if( allowed == null || allowed.isEmpty()) {
			return null;
		}
		Set<String> set = allowed;
		String opts[] = set.toArray(new String[set.size()]);
		String regs[] = new String[set.size()];
		for(int i=0; i< set.size(); i++) {
			regs[i] = Pattern.quote(opts[i]);
		}
		String alt = "(?:"+String.join("|",regs)+")";
		return "(?:"+alt+",)*"+alt;
	}

	

	@Override
	public Set<String> getItembyValue(String value) {
		LinkedHashSet<String> set = new LinkedHashSet<>();
		for(String v : value.split(SPLIT)) {
			set.add(v);
		}
		return set;
	}

	@Override
	public void setItem(Set<String> item) {
		try {
			setValue(String.join(",", item.toArray(new String[item.size()])));
		} catch (TypeException e) {
			throw new TypeError(e);
		}
	}

}
