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
