// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.inputs;

/** Input for phone numbers
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PhoneInput.java,v 1.3 2014/09/15 14:30:20 spb Exp $")
public class PhoneInput extends PatternTextInput implements HTML5Input {

	/**
	 * @param pattern
	 */
	public PhoneInput() {
		super("^\\+[\\d ]+$");
		setTag("+ followed by numbers and spaces");
		setBoxWidth(15);
		setSingle(true);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "tel";
	}

	

}
