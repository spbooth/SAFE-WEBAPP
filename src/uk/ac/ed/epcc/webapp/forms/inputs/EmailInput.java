// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.inputs;

/** Input for email addresses
 * @author spb
 *
 */

public class EmailInput extends PatternTextInput implements HTML5Input {

	public EmailInput() {
		super("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b");
		setTag("email@address.com");
		setBoxWidth(15);
		setSingle(true);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "email";
	}

	

}
