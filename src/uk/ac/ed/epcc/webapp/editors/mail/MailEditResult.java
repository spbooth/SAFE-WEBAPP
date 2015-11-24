package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;

public class MailEditResult extends ChainedTransitionResult<MailTarget, EditAction> {
// Note this is almost a redirect result but
	// we can't encode the action in a url as 
	// the resulting form post url would repeat the action again.
	public MailEditResult(AppContext conn,MessageHandler composer,List<String> args, EditAction action) throws Exception {
		super(new EmailTransitionProvider(composer.getFactory(conn)),new MailTarget(composer, composer.getMessageProvider().getMessageHash(), args),action);	
	}
	
	
}
