// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;

/** An input that selects a day using pull down menus.
 * The class also implements ParseInput to support setting 
 * default values.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DayMultiInput.java,v 1.3 2014/09/15 14:30:19 spb Exp $")

public class DayMultiInput extends TimeStampMultiInput {
    
	public DayMultiInput(){
		super(1000L,Calendar.DAY_OF_MONTH);
	}
	public DayMultiInput(long resolution){
		super(resolution,Calendar.DAY_OF_MONTH);
	}
}