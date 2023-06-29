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
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.action.ConfirmMessage;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
//import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataException;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/**
 * Utility class to do standard Object creation via a HTML Form The form is
 * always built using the standard buildCreationForm method of the
 * DataObjectFactory
 * 
 * @author spb
 * 
 */


public class HTMLCreationForm {
	private FormCreator creator;

	private HTMLForm form = null;

	private boolean use_multipart = false;


	public HTMLCreationForm(FormCreator c) {
		creator = c;
	}

	public HTMLForm getForm() {
		if (form != null)
			return form;
		form = new HTMLForm( creator.getContext());
		try {
			creator.buildCreationForm(form);
		} catch (Exception e) {
			Logger.getLogger(HTMLCreationForm.class).error("Error building Form", e);
		}
		use_multipart = form.containsInput(FileInput.class);
		return form;
	}

	/**
	 * method to emit the HTML edit form. This may be the initial edit form or
	 * it may be a a redirect with error information.
	 * 
	 * @param req
	 * 
	 * @return String containg HTML fragment.
	 
	 */
	public final String getHtmlForm(HttpServletRequest req)  {
        try{
		HTMLForm f = getForm();
		if (f == null)
			return "";
		return f.getHtmlFieldTable(req) + "\n"
				+ f.getActionButtons();
        }catch(Exception e){
        	AppContext c = creator.getContext();
        
        	getLogger(c).error("Error creating form",e);
        	
        	return "An error occured creating a form";
        }
	}

	Logger getLogger(AppContext c) {
		return c.getService(LoggerService.class).getLogger(getClass());
	}
	/**
	 * method to parse and validate the post params
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @return created Object or null on error
	 * @throws ActionException
	 * @throws FieldException
	 */
	public FormResult parseCreationForm(HttpServletRequest req)
			throws FieldException, ActionException{
		HTMLForm f = getForm();
		AppContext conn = creator.getContext();
		if (f == null)
			return null;
		Map<String,Object> params = conn.getService(ServletService.class).getParams();
		FormAction shortcut = f.getShortcutAction(params);
    	if( shortcut != null ){
    		FormResult result=null;
    		ConfirmMessage confirm_action = shortcut.getConfirmMessage(f);
    		if( confirm_action != null ){
				result = confirmTransition(req, conn, confirm_action.getMessage(),confirm_action.getArgs());
				if( result != null ){
					return result;
				}
				
			}
    		result = shortcut.action(f);
			if( result != null ){
				return result;
			}
    	}
		boolean ok = f.parsePost(req);
		if (!ok) {
			conn.getService(LoggerService.class).getLogger(getClass()).debug("form failed to parse");
			return null;
		}
		ConfirmMessage confirm_action = f.mustConfirm(params);

		if( confirm_action != null ){
			FormResult result = confirmTransition(req, conn, confirm_action.getMessage(),confirm_action.getArgs());
			if( result != null ){
				return result;
			}
		}
		FormResult o =  f.doAction(params);
		if (o == null) {
			Logger.getLogger(getClass()).error("error in create doAction",null);
		}

		return o;
	}
	
	/**
	 * @param req
	 * @param conn
	 * @param confirm_action
	 * @param object
	 * @return
	 */
	private FormResult confirmTransition(HttpServletRequest req, AppContext conn, String confirm_action,
			Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parsePost(HttpServletRequest req){
		return getForm().parsePost(req);
	}

	/**
	 * should we use multi-part encoding for this form
	 * 
	 * @return boolean true if multiopart encoding required
	 */
	public boolean useMultiPart() {
		getForm();
		return use_multipart;
	}
	
}