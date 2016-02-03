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
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;

/** Input to select a database table
 * 
 * @author spb
 * @param <T> superclass for table types
 *
 */


public class TableInput<T> implements ListInput<String, String> {
	private final AppContext conn;
	private final Class<T> target;
	private String key, table=null;
	public TableInput(AppContext c, Class<T> target){
		conn=c;
		this.target=target;
	}
	public String getItembyValue(String value) {
		return value;
	}

	public Iterator<String> getItems() {
		return getMap().keySet().iterator();
	}

	public int getCount(){
		return getMap().size();
	}
	public String getTagByItem(String item) {
		return item;
	}

	public String getTagByValue(String value) {
		return value;
	}

	public String getText(String item) {
		if( item == null ){
			return null;
		}
		Class<? extends T> clazz = conn.getPropertyClass(target, null, item);
		if( clazz == null ){
			return item;
		}
		return item+": "+clazz.getSimpleName();
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
		return value;
	}

	public String getString(String value) {
		return value;
	}

	public String getValue() {
		return table;
	}

	public void setKey(String key) {
		this.key=key;
		
	}

	public String setValue(String v) throws TypeError {
		String old=table;
		table=v;
		return old;
	}

	public void validate() throws FieldException {
		if( table == null){
			throw new MissingFieldException();
		}
	}

	public String getItem() {
		return table;
	}

	public void setItem(String item) {
		table=item;
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(String item) {
		return getMap().containsKey(item);
	}
	private Map<String,Class> map=null;
	/**
	 * @return
	 */
	private Map<String, Class> getMap() {
		if( map == null){
			map =conn.getClassMap(target);
		}
		return map;
	}

}