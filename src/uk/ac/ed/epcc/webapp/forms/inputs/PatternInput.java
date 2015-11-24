// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.inputs;

/** An input where the values have to match a regular expression.
 * This can be used to set a validation pattern in HTML5
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PatternInput.java,v 1.2 2014/09/15 14:30:20 spb Exp $")
public interface PatternInput {

	public abstract String getPattern();

}