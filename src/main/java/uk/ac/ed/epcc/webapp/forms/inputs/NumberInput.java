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

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * superclass for all Inputs that return Numbers
 * 
 * @author spb
 * @param <N> type of result object
 * 
 */
public abstract class NumberInput<N extends Number> extends ParseAbstractInput<N> implements UnitInput, RangedInput<N> {
	

	private N min = null;

	private N max = null;
	// step value for use in html5 input
	// this only controls the html5 control not the validation
	private N step = null;
	
	private String unit = null;
	

	protected NumberFormat nf = null;
	public NumberInput(){
		super();
		setSingle(true);
		setMaxResultLength(32);
		setBoxWidth(32);
		addValidator(new FieldValidator<N>() {

			@Override
			public void validate(N data) throws FieldException {
				if (!(data instanceof Number)) {
					throw new ValidateException("Invalid input");
				}
				Number n = (Number) data;
				if (min != null && n.doubleValue() < min.doubleValue()) {
					throw new ValidateException("Too small minimum value="+getString(min));
				}
				if (max != null && n.doubleValue() > max.doubleValue()) {
					throw new ValidateException("Too large maximum value="+getString(max));
				}
				
			}
		});
	}

	
	
	/**
	 * return unit this field is in.
	 * 
	 * @return String representing unit to use or null if not defined
	 */
	public String getUnit() {
		return unit;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.RangedInput#getMin()
	 */
	public N getMin(){
		return min;
	}

	@Override
	public N getStep(){
		if( step != null ){
			return step;
		}
		if( nf != null ){
			if( nf.isParseIntegerOnly()){
				return convert(1);
			}
		}
		return null;
	}
	public void setStep(N s){
		this.step=s;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.RangedInput#getMax()
	 */
	public N getMax(){
		return max;
	}
	public N setMax(N m) {
		N old = max;
		max = m;
		return old;
	}

	public N setMin(N m) {
		N old = min;
		min = m;
		return old;
	}

	/**
	 * Set a number format for use with the input
	 * 
	 * @param n
	 * @return NumberFormat previously in use.
	 */
	public NumberFormat setNumberFormat(NumberFormat n) {
		NumberFormat old = nf;
		// html5 input handling seems not to like grouping characters so supress in inputs
		nf = (NumberFormat) n.clone();
		nf.setGroupingUsed(false);
		return old;
	}

	
	
	/**
	 * set the units for the field
	 * 
	 * @param u
	 *            String reperesenting unit to use
	 * @return previous unit value
	 */
	public String setUnit(String u) {
		String old = unit;
		unit = u;
		return old;
	}


	@Override
	public String getString(N val) {
		if(val == null){
			return null;
		}
		if( nf != null && val != null){
			return nf.format(val);
		}
		return super.getString(val);
	}

	public String formatRange(N n) {
		// Normally getString is the correct conversion
		//This handles scale factors
		// only override explicitly if parse format contains extra markup.
		return getString(n);
	}

	public String getType() {
		return "number";
	}


}