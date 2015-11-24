// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.inputs;

/** An Input where the values must come from a numerical range.
 * 
 * This can be used to add HTML5 validation 
 * @author spb
 *
 * @param <N>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RangedInput.java,v 1.6 2014/09/15 14:30:20 spb Exp $")
public interface RangedInput<N extends Number> extends HTML5Input{

	/** Minimum valid number value.
	 * null value implies no minimum.
	 * 
	 * @return Number
	 */
	public abstract Number getMin();

	/** Maximum  valid number value
	 * null value implies no maximum
	 * 
	 * @return
	 */
	public abstract Number getMax();
	
	/** Step value
	 * defines step value. Valid values should be a multiple of the setp value.
	 * null value implies unconstrained.
	 * This is used to drive the html number input.
	 * 
	 * @return
	 */
	public abstract Number getStep();
	
	/** format step/range values compatible to the way they are  
	 * presented. for example a percent imput may use 0.0 and 1.0 but present as 0, 100
	 * used  to generate HTML5 ranges.
	 * 
	 * @param n
	 * @return
	 */
	public abstract String formatRange(Number n);

}