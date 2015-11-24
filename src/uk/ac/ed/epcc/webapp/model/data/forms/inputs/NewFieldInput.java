// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Input to select a name that is not one of the current fields of a Repository
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NewFieldInput.java,v 1.2 2014/09/15 14:30:31 spb Exp $")

public class NewFieldInput extends TextInput {
	private Repository res;
	public NewFieldInput(Repository res) {
		super(false);
		this.res=res;
		setOptional(false);
		setBoxWidth(32);
		setMaxResultLength(32);
		setSingle(true);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.TextInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		if( res.getInfo(getValue()) != null ){
			throw new ValidateException("Name already in use");
		}
	}

}