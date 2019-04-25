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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * UpdateForm is handler class for updating Objects where the Factory can provide a class implementing
 * FormUpdate it is independent of the type of Form used Almost all the
 * functionality is in the FormUpdate object but this class also holds the state
 * of the currently selected Object.
 * 
 * @author spb
 * @param <T> type of object being updated
 * 
 */


public class UpdateForm<T> {
	private FormUpdate<T> updater;

	private String label;

	private T dat = null;

	public String getTypeName(){
		return label;
	}
	public UpdateForm(String label, FormUpdate<T> fac) {
		updater = fac;
		this.label = label;
	}

	/** Get the underlying FormUpdate object.
	 * 
	 * @return FormUpdate
	 */
	public FormUpdate<T> getFormUpdate(){
		return updater;
	}
	/**
	 * build a form to select a DataObject managed by the updater
	 * 
	 * @param f
	 *            The select form
	 */
	public void buildSelectForm(Form f) {
		updater.buildSelectForm(f, label, dat);
	}


	public AppContext getContext() {
		return updater.getContext();
	}
	protected final Logger getLogger(){
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}

	/**
	 * return the target DataObject
	 * 
	 * @return target DataObject
	 */
	protected T getObject() {
		return dat;
	}

	/**
	 * Do we know which object we are editing yet.
	 * 
	 * @return boolean true if target object is known
	 */
	public boolean haveTarget() {
		return dat != null;
	}
	

	/**
	 * set the edit object based on the selection form.
	 * 
	 * @param f
	 *            the validated select Form
	 */
	public void setObject(Form f) {
		
		if (!f.validate()) {
			return;
		}
		dat = updater.getSelected(f);
	}

	/**
	 * Set the Object to edit. Use this if we already know which object we
	 * want to edit The alternative is to use the built-in select form methods.
	 * 
	 * @param dat Object to edit
	 */
	public void setObject(T dat) {
		this.dat = dat;
	}

}