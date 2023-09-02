package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;
/** A {@link FieldValidator} that checks if the value
 * is not a name used by a {@link ParseFactory} (or equals an
 * the name of a single specified object).
 * 
 * @param <F>
 */
public class UnusedNameValidator<F extends DataObject> implements SingleLineFieldValidator {
	private final ParseFactory<F> fac;
	private F existing=null;
	public UnusedNameValidator(ParseFactory<F> fac, F existing) {
		this.fac = fac;
		this.existing = existing;
	}

	@Override
	public void validate(String name) throws FieldException {
		F dup = fac.findFromString(name);
		if( dup != null ){
			if( existing == null || ! existing.equals(dup)){
				throw new ValidateException("Name "+name+" already in use");
			}
		}
		
	}
}