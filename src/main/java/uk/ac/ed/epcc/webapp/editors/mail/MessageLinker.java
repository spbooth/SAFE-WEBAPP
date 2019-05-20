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

import uk.ac.ed.epcc.webapp.content.ContentBuilder;

/** A MessageLinker is an object that wraps a MessageProvider
 * In addition to providing a reference to the MessageProvider it
 * can also provide URL links to a servlet that serves the contents of the message
 * @see MessageEditLinker
 * 
 * @author spb
 *
 */
public interface MessageLinker {
	/** Get the MessageProvider associated with this object
	 * 
	 * @return MEssageProvider
	 * @throws Exception
	 */
	public MessageProvider getMessageProvider() throws Exception;
	/** Create a HTML link to the associated message or part of the message. 
	 * 
	 * @param builder HtmlBuilder to add link to
	 * @param args  args identifying part of message to access
	 * @param file   filename to add to URL
	 * @param text    text of link
	 */
    public void addLink(ContentBuilder builder,  List<String> args, String file, String text);
    
}