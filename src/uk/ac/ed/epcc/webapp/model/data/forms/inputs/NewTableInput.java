// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
/** Input to generate a name for a new database table.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NewTableInput.java,v 1.3 2014/09/15 14:30:31 spb Exp $")

public class NewTableInput extends TextInput {
	private AppContext c;
	public NewTableInput(AppContext c){
		this.c=c;
		setOptional(false);
		setSingle(true);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.TextInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		DataBaseHandlerService serv = c.getService(DataBaseHandlerService.class);
		if( serv != null ){
			if( serv.tableExists(getValue())){
				throw new ValidateException("Table "+getValue()+" already exists");
			}
		}
	}
}