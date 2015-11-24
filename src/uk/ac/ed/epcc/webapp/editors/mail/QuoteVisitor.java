// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.AppContext;

/** Class to convert a Message into a quoted text string 
 * Nested messaged are quoted to the appropriate message depth.
 * non text parts are ommitted.
 * 
 * If the top level part passed to the visit class is not a message the top level will
 * be unquoted.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: QuoteVisitor.java,v 1.3 2015/10/15 11:34:34 spb Exp $")

public class QuoteVisitor extends PrefixVisitor<TextMailBuilder> {
	private TextMailBuilder sb;
	public QuoteVisitor(AppContext conn) {
		super(conn);
		sb = new TextMailBuilder();
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	protected TextMailBuilder getMailBuilder() {
		return sb;
	}




}