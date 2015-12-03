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

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
/** A FormResult indicating a navigation to a new page within the current application
 * 
 * @author spb
 *
 */


public class RedirectResult implements FormResult {
	private final String url;
	/** Constructor
	 * 
	 * @param url Target url relative to application root
	 */
	public RedirectResult(String url){
		this.url=url;
	}
   public String getURL(){
	   return url;
   }
public void accept(FormResultVisitor vis) throws Exception {
	if( vis instanceof WebFormResultVisitor){
		((WebFormResultVisitor)vis).visitRedirectResult(this);
		return;
	}
	throw new UnsupportedResultException();
}
}