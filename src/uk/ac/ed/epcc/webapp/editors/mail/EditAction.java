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
/**
 * 
 */
package uk.ac.ed.epcc.webapp.editors.mail;
/** Email actions and transitions
 * 
 * @author spb
 *
 */

import uk.ac.ed.epcc.webapp.servlet.ViewTransitionKey;

public enum EditAction implements ViewTransitionKey<MailTarget>{
	Edit,
	EditSubject,
	Update,
	Delete,
	Merge,
	Quote,
	Flatten,
	Send("Send Message"),
	Abort("Abort Message"),
	StartOver("Undo all edits"),
	AddRecipient,
	AddCC,
	AddTo,
	AddBcc,
	AddReplyTo,
	Serve{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.EditAction#isNonModifying(uk.ac.ed.epcc.webapp.editors.mail.MailTarget)
		 */
		@Override
		public boolean isNonModifying(MailTarget target) {
			// Serve is accessed by link but does not modify
			return true;
		}
		
	},
	AddAttachment,
	Upload,
	New;
	private final String help; 
	private EditAction(String h){
		help=h;
	}
	private EditAction(){
		help=null;
	}
	public String getHelp(){
		return help;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ViewTransitionKey#isNonModifying(java.lang.Object)
	 */
	@Override
	public boolean isNonModifying(MailTarget target) {
		return false;
	}
}