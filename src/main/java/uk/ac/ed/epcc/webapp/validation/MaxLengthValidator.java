package uk.ac.ed.epcc.webapp.validation;

import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** A {@link FieldValidator} that constrains the maximum length of a String.
 * 
 */
public final class MaxLengthValidator implements FieldValidator<String> {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + max;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaxLengthValidator other = (MaxLengthValidator) obj;
		if (max != other.max)
			return false;
		return true;
	}
	private final int max;
	public MaxLengthValidator(int max) {
		this.max=max;
	}
	@Override
	public void validate(String data) throws FieldException {
		if( data != null && max > 0 && data.length() > max ) {
			throw new ValidateException("Input too long "+data.length()+">"+max);
		}
		
	}
	
	public int getMaxLength() {
		return max;
	}
	/** Get the effective max length from a set of {@link FieldValidator}s
	 * 
	 * @param validators
	 * @return maximum value or -1 if unconstrained
	 */
	public static int getMaxLength(Set<FieldValidator<String>> validators) {
		int max=-1;
		if( validators != null ) {
			for(FieldValidator<String> val : validators) {
				if( val instanceof MaxLengthValidator) {
					MaxLengthValidator m = (MaxLengthValidator) val;
					int i = m.getMaxLength();
					if( i > 0 ) {
						if( max == -1 || i < max ) {
							max = i;
						}
					}
				}
			}
		}
		return max;
	}
	/** Merge any {@link MaxLengthValidator}s in a set of {@link FieldValidator}s
	 * and return the effective length
	 * 
	 * @param validators
	 * @return maximum value or -1 if unconstrained
	 */
	public static int merge(Set<FieldValidator<String>> validators) {
		int max=-1;
		
		for(Iterator<FieldValidator<String>> it = validators.iterator(); it.hasNext() ;) {
			FieldValidator<String> val = it.next();
			if( val instanceof MaxLengthValidator) {
				it.remove();
				MaxLengthValidator m = (MaxLengthValidator) val;
				int i = m.getMaxLength();
				if( i > 0 ) {
					if( max == -1 || i < max ) {
						max = i;
					}
				}
			}
		}
		if( max > -1 ) {
			validators.add(new MaxLengthValidator(max));
		}
		return max;
	}
	public boolean dominates(MaxLengthValidator v) {
		return max < v.max;
	}
	@Override
	public <X> X accept(FieldValidationVisitor<X, String> vis) {
		return vis.visitMaxLengthValidator(this);
	}
	
	

}
