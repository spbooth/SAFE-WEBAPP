// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;



/**
 * Superclass for inputs that might be implemented as a text box (ie ones that
 * implement ParseInput and LengthInput)
 * 
 * @author spb
 * @param <V> type of result object
 * 
 */
public abstract class ParseAbstractInput<V> extends AbstractInput<V> implements
		 LengthInput<V> {
	// maximum text length
	int len = 64;

	// input width
	int maxwid = 64;

	boolean force_single = false;

	/** Get the maximum permitted value for this input
	 *  A zero or -ve value implies no limit.
	 * 
	 */
	public int getMaxResultLength() {
		return len;
	}

	
	public int getBoxWidth() {
		return maxwid;
	}

	/**
	 * Get if the input should be forced to be a single line
	 * 
	 * @return boolean
	 */
	public boolean getSingle() {
		return force_single;
	}

	/**
	 * set the input length for this parameter as a text box.
	 * A zeroor -ve value imples no limit.
	 * 
	 * @param l
	 *            int input_length
	 */
	public void setMaxResultLength(int l) {
		len = l;
	}

	/**
	 * set the input width for this parameter as a text box.
	 * 
	 * @param l
	 *            int maximum input width
	 */
	public void setBoxWidth(int l) {
		maxwid = l;
	}

	/**
	 * Set if the input should be forced to be a single line
	 * 
	 * @param b
	 *            boolean
	 */
	public void setSingle(boolean b) {
		force_single = b;
	}


	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitLengthInput(this);
	}
}