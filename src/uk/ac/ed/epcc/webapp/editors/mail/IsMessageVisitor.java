// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.editors.mail;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;

/** A {@link Visitor} that tests if the target path matches an email message
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
}
