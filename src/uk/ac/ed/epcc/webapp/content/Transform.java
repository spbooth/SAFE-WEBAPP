// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;


/**
 * Transform is a generic data converter
 * 
 * This can either be a one off transform e.g. transforming the contents of a data column 
 * or it can be a formatting transform applied when the table is printed.
 * 
 * @author spb
 * 
 */
public interface Transform {

	Object convert(Object old);
}