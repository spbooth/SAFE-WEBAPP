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
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.Version;


/** A Table.Formatter. If the SimpleXMLBuilder is really a
 * HTMLBuilder then convert whitespaces into non breaking spaces.
 * 
 * 
 * @author spb
 * @param <C> 
 * @param <R> 
 *
 */


public class HTMLTransform <C,R> extends Object implements Table.Formatter<C,R> {

	public Object convert(Object old) {
		return old;
	}

	

	public Object convert(Table<C, R> t, C col, R row, Object raw) {
		if( raw instanceof String){
			return new HtmlSpaceGenerator((String)raw);
		}
		return raw;
	}
}