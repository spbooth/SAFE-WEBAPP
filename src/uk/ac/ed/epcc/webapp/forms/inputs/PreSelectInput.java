// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;


/** An optional ListInput where we can supress the pre-selection
 * of the first element when the input is non-optional.
 * 
 * @author spb
  * @param <V> type of value object
 * @param <T> type of Item object
 *
 */
public interface PreSelectInput<V,T> extends ListInput<V, T>, OptionalInput {

	public boolean allowPreSelect();
	
	public void setPreSelect(boolean value);
}