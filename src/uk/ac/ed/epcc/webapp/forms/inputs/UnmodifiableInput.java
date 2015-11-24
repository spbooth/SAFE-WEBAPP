// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;
/** An input that should be shown as a non editable text label.
 * 
 * The input can still be set queried and validated as normal but the presentation layer will 
 * prevent the user from editing the value
 * @author spb
 *
 */
public interface UnmodifiableInput {
	/** generate the text to be presented to the user
	 * 
	 * @return String
	 */
  public String getLabel();
}