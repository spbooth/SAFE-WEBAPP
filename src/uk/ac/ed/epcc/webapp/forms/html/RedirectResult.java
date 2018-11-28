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

import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
/** A FormResult indicating a navigation to a new page within the current application
 * 
 * @author spb
 *
 */


public class RedirectResult implements SerializableFormResult {
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
@Override
public int hashCode() {
	return url.hashCode();
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	RedirectResult other = (RedirectResult) obj;
	if (url == null) {
		if (other.url != null)
			return false;
	} else if (!url.equals(other.url))
		return false;
	return true;
}
@Override
public String toString() {
	return "RedirectResult [url=" + url + "]";
}

}