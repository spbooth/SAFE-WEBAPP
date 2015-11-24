// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An object that represents a message. 
 * @see MessageHandlerFactory
 * @see MessageComposer
 * @see MessageCreator
 * 
 * @author spb
 *
 */
public interface MessageHandler extends Indexed{
	/** get the {@link MessageProvider} corresponding to the Message
	 * 
	 * @return MessageProvider
	 * @throws Exception
	 */
	public MessageProvider getMessageProvider() throws Exception;
	
	/** Is the specified person allowed to view this message
	 * @param path   Path being viewed
	 * @param operator
	 * @return boolean
	 */
	public boolean canView(List<String> path,SessionService<?> operator);
	/** get identifying name for the type of message being edited
	 * 
	 * @return String
	 */
	public String getTypeName();
	/** Get the MessageHandlerFactory for this MessageHandler.
	 * @param conn 
	 * 
	 * @return MessageHandlerFactory
	 */
	public MessageHandlerFactory getFactory(AppContext conn);
}