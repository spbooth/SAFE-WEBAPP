// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

public interface CreateTemplate<BDO extends DataObject> extends CreateCustomizer<BDO>{


	
	public abstract DataObjectFactory<BDO> getFactory();

	public abstract FormResult getResult(String type_name,BDO dat, Form f);
	/** should a confirm dialog be presented. 
	 * A null value means no confirm dialog.
	 * @param f
	 * @return confirm type or null 
	 */
	public abstract String getConfirm(Form f);
	
	
}