package uk.ac.ed.epcc.webapp.email.inputs;

import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput;
import uk.ac.ed.epcc.webapp.forms.inputs.MultipleInput;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;

public class EmailListValidator implements SingleLineFieldValidator, MultipleInput, FormatHintInput {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
	 */
	@Override
	public void validate(String email) throws FieldException {
		if( email == null || email.trim().length()==0){
			// must be optional
			return;
		}
		if (!Emailer.checkAddressList(email)) {
			throw new ValidateException("Expecting comma seperated email addresses");
		}

	}




	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	@Override
	public String getType() {
		return "email";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultipleInput#isMultiple()
	 */
	@Override
	public boolean isMultiple() {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput#getFormatHint()
	 */
	@Override
	public String getFormatHint() {

		return "name@example.com, name2@example.com";
	}
}