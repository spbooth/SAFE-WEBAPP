// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.text.NumberFormat;

/** A DoubleInput which defaults to input a value between 0 and 1 using a
 * percent number format.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PercentInput.java,v 1.6 2014/09/15 14:30:20 spb Exp $")

public class PercentInput extends DoubleInput {
   public PercentInput(){
	   super();
	   setNumberFormat(NumberFormat.getPercentInstance());
	   setStep(0.01);
	   setMin(0.0);
	   setMax(1.0);
	   setBoxWidth(4);
	   setMaxResultLength(8);
	   setUnit("(Percentage)");
   }
   @Override
public String formatRange(Number n) {
	return Integer.toString((int)(n.doubleValue()*100.0));
}
@Override
public String getType() {
	// percentages don't validate in most browsers.
	return null;
}
}