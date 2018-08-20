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
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;

/** Abstract class to implement ListInput where we want
 * the input to generate a String code for each item.
 * 
 * @author spb
 *
 * @param <O> type of item
 */
public abstract class CodeListInput<O> implements ListInput<String,O>, NameInput<O> {

	private O item=null;
	private String key=null;
	

	
	public String getTagByValue(String value) {
		return value;
	}

	
	public String convert(Object v) throws TypeError {
		if( v == null ){
			return null;
		}
		return v.toString();
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(String value) {
		O tmp = getItembyValue(value);
		if( tmp != null ){
			return getText(tmp);
		}
		return value;
	}

	public String getString(String value) {
		return value;
	}

	public String getValue() {
		return getTagByItem(item);
	}

	public void setKey(String key) {
		this.key=key;
	}

	public String setValue(String v) throws TypeError {
		String previous = getTagByItem(item);
		item = getItembyValue(v);
		return previous;
	}

	public void validate() throws FieldException {
	    if( item == null ){
	    	throw new MissingFieldException(getKey()+" missing");
	    }
	}

	public O getItem() {
		return item;
	}

	public void setItem(O item) {
		this.item=item;		
	}


	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

}