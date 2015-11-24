// Copyright - The University of Edinburgh 2011
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