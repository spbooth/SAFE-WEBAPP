// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
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
	private boolean must_validate=true;

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
	 * 
	 * @param f Form calling action.
	 * 
	 * @return String confirm name
	 */
	public String getConfirm(Form f) {
		return confirm;
	}

	public void setConfirm(String c) {
		confirm = c;
	}
	/** Return an optional help text for this action that can be presented as a tooltip etc.
	 * 
	 * @return String or null
	 */
	public String getHelp(){
		return null;
	}
	/** Get optional button text to use instead of the action name. 
	 * 
	 * @return
	 */
	public String getText(){
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