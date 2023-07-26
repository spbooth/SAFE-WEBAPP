package uk.ac.ed.epcc.webapp.validation;

import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MaximumValueException;
/** A {@link FieldValidator} that checks the value must be below or equal to a specified value.
 * 
 * @param <D>
 */
public final class MaxValueValidator<D extends Comparable> implements FieldValidator<D> {
	@Override
	public String toString() {
		return "MaxValueValidator [val=" + val + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		MaxValueValidator other = (MaxValueValidator) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	private final D val;
	public MaxValueValidator(D min) {
		this.val=min;
	}
	public D getMax() {
		return val;
	}
	public boolean dominates(MaxValueValidator<D> v) {
		if( val == null || v.val == null ) {
			return false;
		}
		return val.compareTo(v.val) < 0;
	}
	@Override
	public void validate(D data) throws FieldException {
		if( data == null || val == null ) {
			return;
		}
		if( data.compareTo(val) > 0 ) {
			throw new MaximumValueException("Value to large, needs to be below "+val, val);
		}
		
	}
	
	public static <D extends Comparable> D getMax(Set<FieldValidator<D>> set){
		D result = null;
		for(FieldValidator<D> v : set) {
			if( v instanceof MaxValueValidator) {
				MaxValueValidator<D> m = (MaxValueValidator<D>) v;
				if( result == null || m.getMax().compareTo(result) < 0 ) {
					result = m.getMax();
				}
			}
		}
		return result;
	}
	public static <D extends Comparable> void clearMax(Set<FieldValidator<D>> set){
		for(Iterator<FieldValidator<D>> it = set.iterator(); it.hasNext() ;) {
			FieldValidator<D> v = it.next();
			if( v instanceof MaxValueValidator) {
				it.remove();
			}
		}
	}
	@Override
	public <X> X accept(FieldValidationVisitor<X, D> vis) {
		return vis.visitMaxValueValidator(this);
	}
	
}
