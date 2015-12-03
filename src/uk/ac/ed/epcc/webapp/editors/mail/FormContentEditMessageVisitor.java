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