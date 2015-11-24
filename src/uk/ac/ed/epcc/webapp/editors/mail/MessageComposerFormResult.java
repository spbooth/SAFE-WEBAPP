// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.AppContext;
@uk.ac.ed.epcc.webapp.Version("$Id: MessageComposerFormResult.java,v 1.5 2014/09/15 14:30:16 spb Exp $")


public class MessageComposerFormResult extends MailEditResult {
	
	public MessageComposerFormResult(AppContext conn,MessageComposer comp) throws Exception {
		super(conn,comp,null,null);
	}
	
}