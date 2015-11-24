// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;


/**
 * Input for a password field. Works the same as a TextInput but is displayed
 * differently in a form
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PasswordInput.java,v 1.3 2015/11/16 17:20:31 spb Exp $")

public class PasswordInput extends TextInput {

	/**
	 * 
	 */
	public PasswordInput() {
		super();
		setBoxWidth(32);
	}


	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitPasswordInput(this);
	}

}