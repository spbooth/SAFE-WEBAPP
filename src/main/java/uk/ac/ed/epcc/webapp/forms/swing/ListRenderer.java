//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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