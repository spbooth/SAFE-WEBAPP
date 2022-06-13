package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashMap;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
/** A {@link FieldConstraint} that simply orders the fields in different stages
 * 
 * @author Stephen Booth
 *
 */
public class OrderFieldConstraint implements FieldConstraint {

	private final String after;
	public OrderFieldConstraint(String after) {
		this.after=after;
	}

	@Override
	public <I extends Input> Selector<I> apply(boolean support_multi_stage, String field, Selector<I> original,
			Form form, HashMap fixtures) {
		if( ! fixtures.containsKey(after)) {
			return null;
		}
		return original;
	}

}
