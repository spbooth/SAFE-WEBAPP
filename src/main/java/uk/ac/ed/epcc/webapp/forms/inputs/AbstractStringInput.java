package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** A {@link ParseInput} for Strings.
 * 
 */
public abstract class AbstractStringInput extends AbstractInput<String> implements ParseInput<String> {

	@Override
	public final String parseValue(String v) throws ParseException {
		return v;
	}

}
