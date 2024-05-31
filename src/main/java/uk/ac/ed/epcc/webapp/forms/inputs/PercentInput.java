//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.text.NumberFormat;

import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/** A DoubleInput which defaults to input a value between 0 and 1 using a
 * percent number format.
 * 
 * @author spb
 *
 */


public class PercentInput extends DoubleInput {
   public PercentInput() {
	   this(false);
   }
   private final boolean integer_only;
   public PercentInput(boolean integer_only){
	   super();
	   this.integer_only=integer_only;
	   NumberFormat perc = NumberFormat.getPercentInstance();
	   if( ! integer_only) {
		   // Allow up to 3 fractional digits in format/reparse
		   perc.setMinimumFractionDigits(0);
		   perc.setMaximumFractionDigits(3);
	   }
	   setNumberFormat(perc);
	   setStep(0.01);
	   setMin(0.0);
	   setMax(1.0);
	   setBoxWidth(4);
	   setUnit("(Percentage)");
   }
   @Override
public String formatRange(Double n) {
	return Integer.toString((int)(n.doubleValue()*100.0));
}
@Override
public String getType() {
	// percentages don't validate in most browsers.
	return null;
}
@Override
protected Double normalise(Double val) {
	if( val == null || ! integer_only) {
		return val;
	}
	double d = val.doubleValue()*100.0;
	return Double.valueOf(Math.rint(d)/100.0);
	
}
}