// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Date;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
@uk.ac.ed.epcc.webapp.Version("$Id: RangeDateInput.java,v 1.2 2014/09/15 14:30:20 spb Exp $")


public class RangeDateInput extends DateInput {
    private final Date start,end;
    public RangeDateInput(Date start,Date end){
    	this.start=start;
    	this.end=end;
    	if( end.before(start)){
    		throw new ConsistencyError("Date range reversed");
    	}
    	setOptional(false);
    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		
		Date d = getValue();
		if( d != null && (d.before(start) || d.after(end))){
			throw new ValidateException("Value out of range");
		}
	}
    
}