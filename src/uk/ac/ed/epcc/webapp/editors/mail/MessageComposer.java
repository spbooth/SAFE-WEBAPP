// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.List;

import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Interface for classes that represent a message undergoing composition
 * 
 * @author spb
 *
 */
public interface MessageComposer extends MessageHandler{

	/** Send the message
	 * @param operator 
	 * @return FormResult to invoke after send
	 * @throws Exception 
	
	 * 
	 *
	 */
	public abstract FormResult send(SessionService<?> operator) throws Exception;
    /** re-initialise the message to its starting state
     * 
     * @param operator
     * @throws Exception
    
     */
	public abstract void repopulate(SessionService<?> operator) throws Exception;
	/** Initialise the message to a starting state if it has not already been set.
	 * If a message already exists do nothing.
	 * 
	 * @param operator
	 * @throws Exception
	 */
	public abstract void populate(SessionService<?> operator) throws Exception;

	/** Abort the composition of the message
	 * @return FormResult to use after abort.
	 * 
	 * @throws DataFault
	 */
	public abstract FormResult abort() throws DataFault;
	
	/** Is the specified person allowed to edit this message
	 * @param path being edited
	 * @param operator
	 * @return boolean
	 */
	public boolean canEdit(List<String> path,SessionService<?> operator);

	/** get an {@link Input} for email addresses.
	 * Only used if the composer allows.
	 * Normally this will just be and {@link EmailInput} but a {@link MessageComposer}
	 * can substitute something else to make it easier to find the desired email. The 
	 * action will accept a comma separated list of multiple values if the composer wishes
	 * to support bulk add
	 * 
	 * @return Input<String>
	 */
	public Input<String> getEmailInput();
   
}