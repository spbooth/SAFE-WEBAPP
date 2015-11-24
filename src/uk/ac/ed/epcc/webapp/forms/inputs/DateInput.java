// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

@uk.ac.ed.epcc.webapp.Version("$Id: DateInput.java,v 1.6 2014/09/15 14:30:19 spb Exp $")


/** A text input for {@link Date}s on day boundaries-
 * 
 * @author spb
 *
 */
public class DateInput extends AbstractDateInput implements TagInput, HTML5Input {
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    public DateInput(){
    	super();
    }
	public DateInput(long resolution) {
		super(resolution);
		
	}
	

	public String[] getFormats(){
		return new String[] {DEFAULT_FORMAT} ;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "date";
	}

	

}