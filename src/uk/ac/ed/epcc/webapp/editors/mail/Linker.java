// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/** A {@link MessageEditLinker} 
 * This could also be used as a general {@link MessageLinker}
 */
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
@uk.ac.ed.epcc.webapp.Version("$Id: Linker.java,v 1.5 2014/09/15 14:30:15 spb Exp $")


public class Linker implements MessageEditLinker, Contexed{

	private final MessageHandler composer;
	private final AppContext conn;
	
	public Linker(AppContext conn,  MessageHandler h){
		this.conn=conn;
		this.composer=h;
	}
	public void addButton(ContentBuilder cb, EditAction action, List<String> path,
			String text) {
		try {
			cb.addButton(conn, text, new MailEditResult(conn,composer, path, action));
		} catch (Exception e) {
			conn.error(e,"Error making MailEditResult");
		}
		
	}
	
	

	public void addLink(ContentBuilder builder, List<String> args,
			String file, String text) {
		
		if( file != null ){
			args = new LinkedList<String>(args);
			args.add(file);
		}
		try {
			builder.addLink(conn, text,new MailEditResult(conn,composer, args, EditAction.Serve) );
		} catch (Exception e) {
			conn.error(e,"Error making link");
		}

	}

	public MessageProvider getMessageProvider() throws Exception {
		return composer.getMessageProvider();
	}


	public AppContext getContext() {
		return conn;
	}
	
}