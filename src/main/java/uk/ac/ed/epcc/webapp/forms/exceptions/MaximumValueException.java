package uk.ac.ed.epcc.webapp.forms.exceptions;
/** A {@link ValidateException} thrown when the provided value is above a maximum value.
 * 
 * It forwards the threshold value so higher level logic can substitute a better error message
 * 
 */

public class MaximumValueException extends ValidateException {

	private final Comparable max;
	

	public MaximumValueException(String message,Comparable max) {
		super(message);
		this.max=max;
	}

	public Comparable getMax() {
		return max;
	}

	

}
