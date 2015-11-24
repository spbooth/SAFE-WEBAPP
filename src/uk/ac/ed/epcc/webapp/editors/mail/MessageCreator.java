// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Interface for objects that can create MessageComposer objects
 * for new messages
 * 
 * @author spb
 *
 */
public interface MessageCreator extends MessageHandlerFactory{
	
	/** Can the current operator create emails.
	 * 
	 * @param op
	 * @return boolean
	 */
	public boolean canCreateMessage(SessionService op);
	/** should direct or form creation be used. 
	 * 
	 * @return boolean
	 */
	public boolean createDirectly();
	/** Create the email directly. 
	 * 
	 * @return FormResult or null
	 * @throws Exception 
	 */
	public FormResult directCreate() throws  Exception;
	/** build the email creation form for this email type.
	 * 
	 * @param f   Form
	 * @param operator current user.
	 */
	public void buildMessageCreatorForm(Form f,SessionService operator);
}