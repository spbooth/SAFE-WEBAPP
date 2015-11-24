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
@uk.ac.ed.epcc.webapp.Version("$Id: MonthMultiInput.java,v 1.2 2014/09/15 14:30:20 spb Exp $")

public class MonthMultiInput extends TimeStampMultiInput {
    
	public MonthMultiInput(){
		super(1000L,Calendar.MONTH);
	}
	public MonthMultiInput(long resolution){
		super(resolution,Calendar.MONTH);
	}
}