package uk.ac.ed.epcc.webapp.forms.exceptions;
/** A {@link ValidateException} thrown when the provided value is below a minimum value.
 * 
 * It forwards the threshold value so higher level logic can substitute a better error message
 * 
 */

public class MinimumValueException extends ValidateException {

	private final Comparable min;
	

	public MinimumValueException(String message,Comparable min) {
		super(message);
		this.min=min;
	}

	public Comparable getMin() {
		return min;
	}

	

}
