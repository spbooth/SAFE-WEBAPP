package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
/** A {@link FieldConstraint} that simply orders the fields in different stages
 * 
 * @author Stephen Booth
 *
 */
public class OrderFieldConstraint<D> implements FieldConstraint<D> {

	private final String after;
	public OrderFieldConstraint(String after) {
		this.after=after;
	}


	@Override
	public boolean requestMultiStage(Map<String, Object> fixtures) {
		if( ! fixtures.containsKey(after)) {
			return true;
		}
		return false;
	}

}
