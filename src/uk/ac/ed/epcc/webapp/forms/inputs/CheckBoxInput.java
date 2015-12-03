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
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** A checkbox input. 
 * 
 * One peculiarity of Checkbox input is that it always has a valid state.
 * Inserting an invalid input results in an unchecked state.
 * 
 * @author spb
 *
 */


public class CheckBoxInput implements ParseInput<String>,  BinaryInput<String> {

	private final String checked_value;
	private final String unchecked_value;
	private String key;
	boolean is_checked;
	String value;

	public CheckBoxInput(String checked, String unchecked) {
		this.checked_value = checked;
		this.unchecked_value = unchecked;
		is_checked=false;
		value = unchecked;
	}

	public String getChecked() {
		return checked_value;
	}

	public String getUnChecked() {
		return unchecked_value;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.BinaryInput#isChecked()
	 */
	public boolean isChecked() {
		return is_checked;
	}
    private boolean checkString(String v){
    	if (v != null && v.trim().equalsIgnoreCase(checked_value)) {
			return true;
		} else {
			return false;
		}
    }
	public void parse(String v) throws ParseException {
		is_checked = checkString(v);
		setValue(getValue(is_checked));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.BinaryInput#setChecked(boolean)
	 */
	public void setChecked(boolean value) {
		is_checked=value;
	}

	public String setValue(String o) throws TypeError{
		if( o == null ){
			// null means unchecked
			String old = getValue();
			is_checked=false;
			value = getUnChecked();
			return old;
		}
		if(o instanceof String){
			String v = o;
			// no input implies unchecked
			String old = getValue();
			is_checked=checkString(v);
			value=v;
			return old;
		}
		throw new TypeError("Unsupported object in setValue");
	}

	public String convert(Object o) throws TypeError{
		if( o == null ){
			return getUnChecked();
		}
		if(o instanceof String){
			String v = (String) o;
			return getValue(checkString(v));
		}
		if( o instanceof Boolean){
			return getValue(((Boolean)o).booleanValue());
		}
		throw new TypeError(o.getClass());
	}
	public void validate() throws FieldException {
		return;
	}

	public String getString() {
		return getValue(is_checked);
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(String value) {
		return getString(value);
	}

	public String getString(String value) {
		return getValue(checkString(value));
	}

	private String getValue(boolean val ) {
		if( val ){
			return checked_value;
		}else{
			return unchecked_value;
		}
	}
   
	public void setKey(String key) {
	    this.key=key;	
	}

	public String getValue() {
		// getValue returns the ACTUAL value as set by the setValue call
		return value;
	}

	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitBinaryInput(this);
	}
}