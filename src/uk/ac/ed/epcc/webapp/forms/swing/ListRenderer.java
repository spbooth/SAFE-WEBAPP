// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;

/**
 * custom renderer for JCombobox generated from a ListInput
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ListRenderer.java,v 1.2 2014/09/15 14:30:21 spb Exp $")

public class ListRenderer extends JLabel implements ListCellRenderer {
	ListInput input;

	public ListRenderer(ListInput input) {
		this.input = input;
	}

	@SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.BLACK);
		}
		if( value == SwingField.LIST_INPUT_UNSELECTED|| value == null){
			setText("NONE");
		}else{
			setText(input.getText(value));
		}
		return this;
	}

}