//| Copyright - The University of Edinburgh 2018                            |
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

import java.text.DateFormat;
import java.util.Set;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ContentMessageVisitor} that only shows each message once.
 * If a message has already been displayed but occurs a second time (e.g. 
 * as part of a reply) then it is shown as a link rather than in-line.
 * 
 * Messages with the same MEssage-ID are assumed to be the same message.
 * @author Stephen Booth
 *
 */
public class ShortContentMessageVisitor extends ContentMessageVisitor {

	private Set<String> message_ids;
	/**
	 * @param conn	   {@link AppContext}
	 * @param message_ids Set of message-ids to show as links
	 * @param buff  {@link ContentBuilder}
	 * @param linker {@link MessageLinker}
	 */
	public ShortContentMessageVisitor(AppContext conn, Set<String> message_ids, ContentBuilder buff, MessageLinker linker) {
		super(conn, buff, linker);
		this.message_ids=message_ids;
	}
	@Override
	public boolean startMessage(MimePart parent, MimeMessage m, MessageWalker messageWalker) throws WalkerException {
		try {
			String id = m.getMessageID();
			if( id != null ) {
				if( message_ids.contains(id)) {
					sb=sb.getPanel("link","seen_message");
					if( sb instanceof HtmlBuilder) {
						((HtmlBuilder)sb).setNewTab(true);
					}
					DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,getContext().getService(SessionService.class).getLocale());
					addLink(messageWalker.getPath(), id, "["+df.format(m.getSentDate())+"] "+m.getSubject());
					sb=sb.addParent();
					return false; // truncate recursion
				}
				message_ids.add(id);
			}
		} catch (MessagingException e) {
			doMessageError(messageWalker, e);
		}
		return super.startMessage(parent, m, messageWalker);
	}

}
