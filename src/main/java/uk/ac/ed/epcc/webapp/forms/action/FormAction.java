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
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;


/**
 * A formAction represents an action button attached to a form When the button
 * is pressed.
 * Actions can request a yes/no confirm message.  
 * If validation is turned off then the state of the form is ignored and the
 * action is run anyway. This is used to implement cancel actions.
 * @author spb
 * 
 */
public abstract class FormAction{
	String confirm = null;  // 
	String confirm_args[] = null;
	private boolean must_validate=true;
	private boolean new_window=false;
	
	public boolean wantNewWindow() {
		return new_window;
	}

	public void setNewWindow(boolean new_window) {
		this.new_window = new_window;
	}

	/** Perform  the actual action based on the form parameters.
	 * Throw a {@link TransitionException} to show an error message to the user.
	 * or a {@link ActionException} to produce a generic error message.
	 * 
	 * @param f  Form
	 * @return FormResult
	 * @throws ActionException
	 */
	public abstract FormResult action(Form f) throws ActionException;

	/**
	 * should this action be confirmed before execute
	 * If this returns null no confirm is required.
	 * If not null it is the name of the confirm message in the confirm.properties
	 * <p>
	 * The form is passed to allow a sub-class to trigger confirm based on form parameters.
	 * However in this case the implementation must handle an incomplete form.
	 * 
	 * Note that the message can be set by calling {@link #setConfirm(String)} or
	 * this method can be overidden
	 * 
	 * @param f Form calling action.
	 * 
	 * @return String confirm name
	 */
	
	public String getConfirm(Form f) {
		return confirm;
	}
	public final ConfirmMessage getConfirmMessage(Form f) {
		// call method as sub-classes might override
		String tag = getConfirm(f);
		if( tag != null ) {
			return new ConfirmMessage(tag, confirm_args);
		}
		return null;
	}

	
	
	
	public void setConfirm(String c) {
		confirm = c;
	}
	public void setConfirmArgs(String args[]) {
		confirm_args=args;
	}
	
	/** Return an optional help text for this action that can be presented as a tooltip etc.
	 * 
	 * @return String or null
	 */
	public String getHelp(){
		return null;
	}
	/** Get optional button content to use instead of the action name. 
	 * This can be a {@link UIProvider} or {@link UIGenerator} but should not include active content 
	 * @return Object to add
	 */
	public Object getText(){
		return null;
	}
	/** Return an optional shortcut key for this action.
	 * 
	 * @return String or null
	 */
	public String getShortcut(){
		return null;
	}
	public boolean getMustValidate(){
		return must_validate;
	}
	public void setMustValidate(boolean validate){
		this.must_validate=validate;
	}
}