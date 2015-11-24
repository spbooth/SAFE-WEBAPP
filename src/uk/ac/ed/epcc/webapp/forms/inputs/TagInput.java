// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;
/** Interface for inputs that need to add a tag string in the form 
 * after the input. For example to specify the allowable formats of the input
 * 
 * @author spb
 *
 */
public interface TagInput {
  public String getTag();
}