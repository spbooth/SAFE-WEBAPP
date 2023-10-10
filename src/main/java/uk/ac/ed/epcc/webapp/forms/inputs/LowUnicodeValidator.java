package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** A {@link FieldValidator} to restrict strings to 3-byte printable
 * 
 */
public class LowUnicodeValidator implements FieldValidator<String> {

	public LowUnicodeValidator() {
	}

	@Override
	public void validate(String data) throws FieldException {
		for(int i = 0 ; i < data.length() ; i++) {
			int code = data.codePointAt(i);
			validateCodePoint(code);
		}

	}

	public void validateCodePoint(int code) throws FieldException{
		if( code > 0xfff ) {
			throw new ValidateException("Invalid character "+Character.toString(code));
		}
		if( Character.isWhitespace(code)) {
			return;
		}
		if( code < 32 ) {
			throw new ValidateException("Invalid character "+Integer.toHexString(code));
		}
	}
}
