package uk.ac.ed.epcc.webapp.email.inputs;

import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput;
import uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;

public class EmailFieldValidator implements SingleLineFieldValidator , HTML5Input, FormatHintInput{
	/**
	 * 
	 */
	private final EmailInput emailInput;

	/**
	 * @param emailInput
	 */
	EmailFieldValidator(EmailInput emailInput) {
		this.emailInput = emailInput;
	}

	@Override
	public void validate(String email) throws FieldException {
		if( email == null || email.trim().length()==0){
			// must be optional
			return;
		}
		if (!Emailer.checkAddress(this.emailInput.getString())) {
			throw new ValidateException("Invalid email address");
		}
		
	}

	@Override
	public String getType() {
		return "email";
	}

	@Override
	public final String getFormatHint() {
		return "name@example.com";
	}
}