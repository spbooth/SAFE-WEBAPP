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
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.StringListInput;

/** Input to select a database table
 * 
 * @author spb
 * @param <T> superclass for table types
 *
 */


public class TableInput<T> extends StringListInput implements ListInput<String, String> {
	private final AppContext conn;
	private final Class<T> target;
	public TableInput(AppContext c, Class<T> target){
		conn=c;
		this.target=target;
	}
	

	public Iterator<String> getItems() {
		return getMap().keySet().iterator();
	}

	public int getCount(){
		return getMap().size();
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