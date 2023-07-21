package uk.ac.ed.epcc.webapp.validation;

import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Visitor interface for {@link FieldValidator}s
 * Most {@link FieldValidator}s will default to a generic method.
 * Sub-classes that need special handling can be added to the visitor
 * Because there should always be a generic default mechanism these
 * will usually be added as a default method
 * 
 * 
 * 
 * @param <X> return type of visitor
 * @param <D> type of validator
 */
public interface FieldValidationVisitor<X,D> {

	/** default fallback for generic {@link FieldValidator}s
	 * 
	 * @param val
	 * @return
	 */
	public X visitGenericFieldValidator(FieldValidator<D> val);
	
	default public X visitMaxValueValidator(MaxValueValidator val) {
		return (X) visitGenericFieldValidator(val);
	}
	
	default public X visitMinValueValidator(MinValueValidator val) {
		return (X) visitGenericFieldValidator(val);
	}
	
	default public X visitMaxLengthValidator(MaxLengthValidator val) {
		return (X) visitGenericFieldValidator((FieldValidator)val);
	}
	
	default public X visitDataObjectFieldValidator(DataObjectFactory.DataObjectFieldValidator val) {
		return (X) visitGenericFieldValidator(val);
	}
}
