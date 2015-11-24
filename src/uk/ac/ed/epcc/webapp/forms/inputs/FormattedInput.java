// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.text.Format;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
/** Input based on a java.text.Format object
 * 
 * @author spb
 *
 * @param <N>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FormattedInput.java,v 1.2 2014/09/15 14:30:19 spb Exp $")

public class FormattedInput<N> extends ParseAbstractInput<N> {
    private Format format;
    public FormattedInput(Format f){
    	format=f;
    }
    
	@SuppressWarnings("unchecked")
	public void parse(String v) throws ParseException {
		try{
		setValue((N) format.parseObject(v));
		}catch(java.text.ParseException e){
			throw new ParseException(e);
		}
	}

	@Override
	public String getString(N val) {
		return format.format(val);
	}

}