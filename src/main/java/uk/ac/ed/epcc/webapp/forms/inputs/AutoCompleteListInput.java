package uk.ac.ed.epcc.webapp.forms.inputs;
/** This is an input that can either be presented as a {@link ListInput} or an {@link AutoComplete}.
 * 
 * Like a {@link ListInput} it only allows values from the suggested list (or the current value of the input).
 * so the choice of which presentation to use can be deferred to very late in the form generation
 * (e.g. based on a preference or the number of suggestions).
 * 
 * An implementing class can also force a particular presentation by calling the corresponding {@link InputVisitor} method in the 
 * {@link #accept(InputVisitor)} method.
 * 
 * @param <V>
 * @param <T>
 */
public interface AutoCompleteListInput<V,T> extends ListInput<V, T>, AutoComplete<V, T> {

	
	public default  <R> R accept(InputVisitor<R> vis) throws Exception{
		return vis.visitAutoCompleteListInput(this);
	}
}
