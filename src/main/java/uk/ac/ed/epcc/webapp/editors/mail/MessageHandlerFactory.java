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