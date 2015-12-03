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
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
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
	private String type_name;

	public HTMLCreationForm(String type_name,FormCreator c) {
		creator = c;
		this.type_name=type_name;
	}

	private HTMLForm getForm() {
		if (form != null)
			return form;
		form = new HTMLForm( creator.getContext());
		try {
			creator.buildCreationForm(type_name,form);
		} catch (Exception e) {
			creator.getContext().error(e, "Error building Form");
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
        
        	c.error(e,"Error creating form");
        	
        	return "An error occured creating a form";
        }
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
		boolean ok = f.parsePost(req);
		if (!ok) {
			conn.getService(LoggerService.class).getLogger(getClass()).debug("form failed to parse");
			return null;
		}
		Map<String,Object> params = conn.getService(ServletService.class).getParams();
		FormResult o =  f.doAction(params);
		if (o == null) {
			conn.error(null,"error in create doAction");
		}

		return o;
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