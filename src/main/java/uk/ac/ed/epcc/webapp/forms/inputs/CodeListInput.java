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

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** Abstract class to implement ListInput where we want
 * the input to generate a String code for each item.
 * 
 * The values and the list tags are always the same and can be derived from the item so
 * internally this holds state as an item rather than a value.
 * 
 * @author spb
 *
 * @param <O> type of item
 * @see SimpleListInput
 */
public abstract class CodeListInput<O> extends BaseInput<String> implements ListInput<String,O>, NameInput<O> {

	private O item=null;
	
	@Override
	public final String getTagByValue(String value) {
		return value;
	}
	@Override
	public final String getValueByTag(String value) {
		return value;
	}
	@Override
	public final String getValueByItem(O item) throws TypeException {
		return getTagByItem(item);
	}
	@Override
	public final O getItembyValue(String value) {
		return getItemByTag(value);
	}
	@Override
	public String convert(Object v) throws TypeException  {
		if( v == null ){
			return null;
		}
		if( v instanceof String){
			return (String)v;
		}
		throw new TypeException(v.getClass());
	}

	
	@Override
	public String getPrettyString(String value) {
		O tmp = getItembyValue(value);
		if( tmp != null ){
			return getText(tmp);
		}
		return value;
	}

	@Override
	public final String getString(String value) {
		return value;
	}

	@Override
	public final String getValue() {
		if( item == null) {
			return null;
		}
		return getTagByItem(item);
	}

	@Override
	public final String setValue(String v) {

		String previous = getTagByItem(item);
		item = getItembyValue(v);
		return previous;

	}



	@Override
	public final O getItem() {
		return item;
	}

	@Override
	public final void setItem(O item) {
		this.item=item;		
	}


	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	@Override
	public final void setNull() {
		item=null;
	}


	
}