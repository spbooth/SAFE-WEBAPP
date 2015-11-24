// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input for URLs
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: URLInput.java,v 1.4 2014/09/15 14:30:21 spb Exp $")
public class URLInput extends TextInput implements HTML5Input{

	@Override
	public void validate() throws FieldException {
		super.validate();
		try {
			URL url = new URL(value);
		} catch (MalformedURLException e) {
			throw new ValidateException("Bad URL", e);
		}
	}

	/**
	 * 
	 */
	public URLInput() {
		super();
	}

	/**
	 * @param allow_null
	 */
	public URLInput(boolean allow_null) {
		super(allow_null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "url";
	}

	
}
