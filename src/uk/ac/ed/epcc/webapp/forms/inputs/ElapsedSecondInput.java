// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.StringTokenizer;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input for a time duration in  HH:mm::ss format
 * returned in seconds
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ElapsedSecondInput.java,v 1.4 2014/12/01 14:58:50 spb Exp $")

public class ElapsedSecondInput extends ParseAbstractInput<Number> implements TagInput{

	public ElapsedSecondInput() {
		super();
		setBoxWidth(10);
		setMaxResultLength(16);
		setSingle(true);
	}

	public void parse(String v) throws ParseException {
		if( v == null || v.trim().length()== 0){
			setValue(null);
			return;
		}
		long result=0L;
		try{
		StringTokenizer st = new StringTokenizer(v,":");
		if( st.countTokens() > 3){
			throw new ParseException("Too many fields");
		}
		while( st.hasMoreElements()){
			result *= 60L;
			long val = Long.parseLong(st.nextToken());
			if( val < 0L ){
				throw new ParseException("-ve time value");
			}
			result += val;
		}
		}catch(Exception e){
			throw new ParseException("Invalid format");
		}
		setValue(new Long(result));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString(java.lang.Object)
	 */
	@Override
	public String getString(Number val) {
		if( val == null ){
			return "";
		}
		int total=val.intValue();
		int seconds = total%60;
		total = total/60;
		int min = total%60;
		total = total/60;
		return total+":"+min+":"+seconds;
	}

	public String getTag() {
		return "(HH:MM:SS)";
	}

	@Override
	public void validate() throws FieldException {
		super.validate();
		Number val = getValue();
		if( val != null && val.intValue() < 0){
			throw new ValidateException("-ve duration");
		}
	}

}