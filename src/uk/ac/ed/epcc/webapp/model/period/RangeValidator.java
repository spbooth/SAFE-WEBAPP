package uk.ac.ed.epcc.webapp.model.period;

import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

public class RangeValidator implements FormValidator{
	private final Date min_date;
	private final Date max_date;
	private final String field;
	public RangeValidator(String field,Date min, Date max){
		assert(min.before(max));
		this.min_date=min;
		this.max_date=max;
		this.field=field;
	}
	
	public void validate(Form f)
			throws ValidateException {
		Date d = (Date) f.get(field);
		if( !( d.after(min_date) && d.before(max_date))){
			throw new ValidateException("Selected date not within enclosing object");
		}
	}
	
}