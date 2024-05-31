package uk.ac.ed.epcc.webapp.forms.inputs;

import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;

/**
 * @author Stephen Booth
 *
 */
public final class URLValidator implements SingleLineFieldValidator , HTML5Input{
	@Override
	public void validate(String value) throws FieldException {
		try {
			URL url = new URL(value);
		} catch (MalformedURLException e) {
			throw new ValidateException("Bad URL", e);
		}
		
	}

	@Override
	public String getType() {
		return "url";
	}
}