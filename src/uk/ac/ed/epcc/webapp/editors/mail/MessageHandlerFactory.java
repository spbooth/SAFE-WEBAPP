// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Tagged;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Interface for Factory classes that can locate MessageHandler objects
 * via an integer index.
 * 
 * This interface is used to allow {@link EmailTransitionProvider} to handle Messages that come from different locations.
 * so it is classes that implement this interface that need to be configured in the java properties.
 *  
 * @author spb
 *
 */
public interface MessageHandlerFactory extends Contexed,Tagged{
	/** Locate a MessageHandler by id number
	 * This method also does access control. 
	 * If the user only has read permission
	 * a simple MessageHandler should be returned.
	 * If the user has edit permission a MessageComposer should be returned. 
	 * Otherwise this method returns null.
	 * @param id integer identifier for MessageComposers from this Factory
	 * @param user AppUSer making request
	 * @return MessageHandler or MessageComposer or null
	 */
	public MessageHandler getHandler(int id, SessionService<?> user);

}