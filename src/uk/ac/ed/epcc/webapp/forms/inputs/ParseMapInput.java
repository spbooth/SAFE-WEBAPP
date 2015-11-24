// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** Equivalent of ParseInput for MultiInputs 
 * If an Input implements this interface then it wants to directly parse a
 * map of all form inputs because of interactions between the sub-inputs.
 * @see AlternateInput  
 * 
 * @author spb
 *
 */
public interface ParseMapInput {
	/**
	 * get a map of String representation of the values in teh input that is compatible
	 * with the way the input parses the map
	 * @return Map or null if no value
	 */
	public abstract Map<String,Object> getMap();

	
	/**
	 * Set the value of the input by parsing a textual representation of the
	 * sub input.
	 * 
	 * @param v
	 * @throws ParseException
	 */
	public abstract void parse(Map<String,Object> v) throws ParseException;
}