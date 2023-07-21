package uk.ac.ed.epcc.webapp.model.data.convert;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
/** A {@link FieldValidator} that checks the value can be converted by a {@link TypeProducer}
 * 
 * @param <T>
 * @param <D>
 */
public final class TypeProducerFieldValidator<T,D>  implements FieldValidator<D> {

	private final TypeProducer<T, D> producer;

	public TypeProducerFieldValidator(TypeProducer<T, D> producer) {
		super();
		this.producer = producer;
	}

	@Override
	public void validate(D data) throws FieldException {
		if( producer.find(data) == null) {
			throw new ValidateException("Unrecognised value "+data);
		}
		
	}
	

}
