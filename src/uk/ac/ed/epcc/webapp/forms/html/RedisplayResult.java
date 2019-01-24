//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;

/** A {@link FormResult} that indicates that the current form should 
 * be re-built and re-displayed.
 * 
 * This is to support use cases where the form depends on some state outwith the form framework
 * @author Stephen Booth
 *
 */
public class RedisplayResult implements FormResult {

	/**
	 * 
	 */
	public RedisplayResult() {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResult#accept(uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor)
	 */
	@Override
	public void accept(FormResultVisitor vis) throws Exception {
		if( vis instanceof WebFormResultVisitor){
			((WebFormResultVisitor)vis).visitRedisplayResult(this);
			return;
		}
		throw new UnsupportedResultException();
	}

}
