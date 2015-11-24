// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;


@uk.ac.ed.epcc.webapp.Version("$Id: TimeStampInput.java,v 1.4 2014/09/15 14:30:21 spb Exp $")



public class TimeStampInput extends AbstractDateInput implements HTML5Input{

	@Override
	public String[] getFormats() {
		return new String[] {"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy-MM-dd HH", "yyyy-MM-dd" };
	}

	public TimeStampInput(long resolution) {
		super(resolution);
	}

	public TimeStampInput(){
		super();
	}
	public String getType(){
		return "datetime-local";
	}
}