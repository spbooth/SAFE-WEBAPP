// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;


/** An input that presents as a check-box.
 * 
 * @author spb
 * @param <I> type of input
 *
 */
public interface BinaryInput<I> extends Input<I> {

	public abstract boolean isChecked();

	public abstract void setChecked(boolean value);
	
	public abstract String getChecked();

}