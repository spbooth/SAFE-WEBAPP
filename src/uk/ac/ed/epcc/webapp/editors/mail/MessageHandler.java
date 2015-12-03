//| Copyright - The University of Edinburgh 2011                            |
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