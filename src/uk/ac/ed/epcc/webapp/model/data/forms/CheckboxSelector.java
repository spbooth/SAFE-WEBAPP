// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/*
 * Created on Nov 3, 2004 by spb
 *
 */
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.inputs.CheckBoxInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;

/**
 * CheckboxSelector Generates a checkbox selector for DataObject edit forms
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CheckboxSelector.java,v 1.3 2014/09/15 14:30:31 spb Exp $")

public class CheckboxSelector implements Selector<Input<String>> {
	String checked_tag;

	String unchecked_tag;

	/**
	 * Default constructor for Y/N tags
	 * 
	 * 
	 */
	public CheckboxSelector() {
		this("Y", "N");
	}

	/**
	 * create a selector with customised tags.
	 * 
	 * Note this can only be used to represent a boolean if the checked tag
	 * auto-converts to boolean true
	 * 
	 * @param checked_tag
	 *            the String value corresponding to a checked box
	 * @param unchecked_tag
	 *            the String value corresponding to a unchecked box
	 * 
	 * 
	 */
	public CheckboxSelector(String checked_tag, String unchecked_tag) {
		super();
		this.checked_tag = checked_tag;
		this.unchecked_tag = unchecked_tag;
	}

	public Input<String> getInput() {
		return new CheckBoxInput(checked_tag, unchecked_tag);
	}

}