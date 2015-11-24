// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;

/** Formats an mail message substituting a designated 
 * text part with a single form Input and associated
 * action buttons.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FormContentEditMessageVisitor.java,v 1.6 2015/11/09 16:32:07 spb Exp $")

public class FormContentEditMessageVisitor extends ContentMessageVisitor {
	
	private final Form form;
	private final String field;
	public FormContentEditMessageVisitor(AppContext conn, ContentBuilder buff,String field,  Form form,MessageLinker link) {
		super(conn, buff, link);
		this.field=field;
		this.form=form;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void doSubject(String subject, MessageWalker w) {
		if( w.matchPath()){
			form.put(field, subject);
			sb.addFormInput(getContext(), form.getField(field),null);
			sb.addActionButtons(form);
		}else{
			super.doSubject(subject, w);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(MimePart parent, String string,MessageWalker w) {
	
		if( w.matchPath()){
			form.put(field, string);
			sb.addFormInput(getContext(), form.getField(field),null);
			sb.addActionButtons(form);
		}else{
		   super.visit(parent, string,w);
		}
	}
}