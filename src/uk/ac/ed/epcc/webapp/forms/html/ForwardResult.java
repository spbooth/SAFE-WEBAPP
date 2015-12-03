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
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
/** FormResult to indicate forwarding to a page from within a servlet.
 * From a transition the current target/provider will be passed by attribute by default.
 * Optionally additional attributes can be supplied by this class.
 * 
 * @author spb
 *
 */

public class ForwardResult implements FormResult {
	private final String url;
	private final HashMap<String,Object> attr;
	/** Constructor
	 * 
	 * @param url Target url relative to application root
	 */
	public ForwardResult(String url){
		this.url=url;
		this.attr=null;
	}
	/** Constructor
	 * 
	 * @param url Target url relative to application root
	 * @param attr 
	 */
	public ForwardResult(String url,Map<String,Object> attr){
		this.url=url;
		this.attr=new HashMap<String, Object>();
		this.attr.putAll(attr);
	}
	public String getURL(){
		return url;
	}
	public Map<String,Object> getAttr(){
		if( attr == null ){
			return null;
		}
		return (Map<String, Object>) attr.clone();
	}
	public void accept(FormResultVisitor vis) throws Exception {
		if( vis instanceof WebFormResultVisitor){
			((WebFormResultVisitor)vis).visitForwardResult(this);
			return;
		}
		throw new UnsupportedResultException();
	}
}