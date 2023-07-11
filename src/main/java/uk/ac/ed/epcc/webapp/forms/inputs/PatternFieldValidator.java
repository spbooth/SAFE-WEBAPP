package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** A {@link FieldValidator} that matches a regular expression
 * @author Stephen Booth
 *
 */
public final class PatternFieldValidator implements FieldValidator<String> {
	private final Pattern validate_pattern;
	/**
	 * 
	 */
	private final String pattern;

	/**
	 * @param pattern
	 */
	public PatternFieldValidator(String pattern) {
		this.pattern = pattern;
		validate_pattern = Pattern.compile(pattern);
	}

	@Override
	public void validate(String v) throws FieldException {
		if( v == null || v.isEmpty()) {
			return;
		}
		if( validate_pattern.matcher(v).matches()) {
			return;
		}
		throw new ValidateException("Input does not match required pattern "+pattern);
		
	}
}