// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.inputs;

/** An Interface for {@link Input}s that can represented as html5 inputs.
 * This does not change the behaviour of the input but does assert compatibility (or not) with 
 * a corresponding html-5 input type. 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: HTML5Input.java,v 1.2 2014/09/15 14:30:19 spb Exp $")
public interface HTML5Input {
	/** get the <em>type</em> tag to emit for
	 * the corresponding html5 type. If this method returns null
	 * no type should be used. This is for the case where.
	 * a sub-class breaks compatibility and needs to supress
	 * a type set in a superclass.
	 * 
	 * @return name or null;
	 */
	public String getType();
}
