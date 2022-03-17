package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
/** A {@link Selector} for {@link BoundedInput}s that 
 * 
 * @author Stephen Booth
 *
 * @param <N> type of bound/value
 * @param <I> type of input
 */
public interface BoundedSelector<N,I extends BoundedInput<N>> extends Selector<I> {

	/** Create a new {@link BoundedSelector} with the select range
	 * of the produced input further restricted by additional bounds
	 * @param min   new minimum (can be null)
	 * @param max   new maximum (can be null)
	 * @return
	 */
	public abstract BoundedSelector<N, I> narrowBounds(N min,N max);
}
