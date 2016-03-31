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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * superclass for all Inputs that return Numbers
 * 
 * @author spb
 * @param <N> type of result object
 * 
 */
public abstract class NumberInput<N extends Number> extends ParseAbstractInput<N> implements LabelInput, UnitInput, RangedInput<N> {
	

	private Number min = null;

	private Number max = null;
	// step value for use in html5 input
	// this only controls the html5 control not the validation
	private Number step = null;
	
	private String unit = null;
	private String label = null;

	protected NumberFormat nf = null;
	public NumberInput(){
		super();
		setSingle(true);
		setMaxResultLength(32);
		setBoxWidth(32);
	}

	/**
	 * return unit this field is in.
	 * 
	 * @return String the label describing this input or null if not defined
	 */
	public String getLabel() {
		return label;
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
	public Number getMin(){
		return min;
	}

	public Number getStep(){
		if( step != null ){
			return step;
		}
		if( nf != null ){
			if( nf.isParseIntegerOnly()){
				return 1;
			}
		}
		return null;
	}
	public void setStep(Number s){
		this.step=s;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.RangedInput#getMax()
	 */
	public Number getMax(){
		return max;
	}
	public Number setMax(Number m) {
		Number old = max;
		max = m;
		return old;
	}

	public Number setMin(Number m) {
		Number old = min;
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
	 * @param l
	 *            String describing input to use
	 * @return previous label value
	 */
	public String setLabel(String l) {
		String old = label;
		label = l;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		Object o = getValue();
		if (o == null) {
			// super class checks for optional input
			return;
		}
		if (!(o instanceof Number)) {
			throw new ValidateException("Invalid input");
		}
		Number n = (Number) o;
		if (min != null && n.doubleValue() < min.doubleValue()) {
			throw new ValidateException("Too small minimum value="+formatRange(min));
		}
		if (max != null && n.doubleValue() > max.doubleValue()) {
			throw new ValidateException("Too large maximum value="+formatRange(max));
		}
	}



	@Override
	public String getString(N val) {
		if( nf != null && val != null){
			return nf.format(val);
		}
		return super.getString(val);
	}

	public String formatRange(Number n) {
		if(n == null){
			return null;
		}
		if( nf != null ){
			return nf.format(n);
		}
		return n.toString();
	}

	public String getType() {
		return "number";
	}


}