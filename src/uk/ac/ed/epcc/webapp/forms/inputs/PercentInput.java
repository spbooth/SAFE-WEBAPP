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

/** A DoubleInput which defaults to input a value between 0 and 1 using a
 * percent number format.
 * 
 * @author spb
 *
 */


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