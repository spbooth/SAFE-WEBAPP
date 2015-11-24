// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.List;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;

public interface MessageEditLinker extends MessageLinker {
	/** Add a single button form to a HtmlBuilder page that invokes the named
	 * operation on the encapsulated Message
	 * 
	 * @param sb
	 * @param action
	 * @param path 
	 * @param text
	 */
  public void addButton(ContentBuilder sb, EditAction action, List<String> path, String text);
  
}