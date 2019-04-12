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

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;

/** A {@link Visitor} that tests if the target path matches an email message
 * @author spb
 *
 */

public class IsMessageVisitor extends AbstractVisitor {

	

	private boolean is_message=false;
	/**
	 * @param conn
	 */
	public IsMessageVisitor(AppContext conn) {
		super(conn);
	}
	/**
	 * @return the is_message
	 */
	public boolean testMessage() {
		return is_message;
	}
	/**
	 * @param is_message the is_message to set
	 */
	public void reset() {
		this.is_message = false;
	}

	@Override
	public boolean startMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		if( messageWalker.matchPath()){
			is_message=true;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visitHeaders() {
		// Not interested in the headers
		return false;
	}
}