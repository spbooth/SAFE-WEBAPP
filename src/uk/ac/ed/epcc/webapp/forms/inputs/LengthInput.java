// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;


/** An input that corresponds to a text box.
 * 
 * @author spb
 * @param <T> 
 *
 */
public interface LengthInput<T> extends ParseInput<T>{
	/**
	 * Get the input length for this Parameter as a text box.
	 * 
	 * @return int input length
	 */
	public int getMaxResultLength();

	/**
	 * Get the input width for this Parameter as a text box.
	 * 
	 * @return int maximum input width
	 */
	public int getBoxWidth();

	/**
	 * set the input length for this parameter as a text box.
	 * This is the maximum allowed length of the result string.
	 * 
	 * @param l
	 *            int input_length
	 */
	public void setMaxResultLength(int l);

	/**
	 * set the input width for this parameter as a text box.
	 * This is the display width of the form field. longer inputs might be allowed
	 * 
	 * @param l
	 *            int maximum input width
	 */
	public void setBoxWidth(int l);
	
	/** should the input be a single line
	 * 
	 * @return true if input should be single line
	 */
	public boolean getSingle();
	

}