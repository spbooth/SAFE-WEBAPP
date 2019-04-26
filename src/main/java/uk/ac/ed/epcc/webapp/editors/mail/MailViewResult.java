//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
/**  a {@link ViewTransitionResult} for viewing emails (or parts thereof).
 * 
 * @author spb
 *
 */
public class MailViewResult extends ViewTransitionResult<MailTarget, EditAction> {
	public MailViewResult(AppContext conn,MessageHandler handler,List<String> args) throws Exception {
		super(new EmailTransitionProvider(handler.getFactory(conn)),new MailTarget(handler, handler.getMessageProvider().getMessageHash(), args));	
	}
	
	
}