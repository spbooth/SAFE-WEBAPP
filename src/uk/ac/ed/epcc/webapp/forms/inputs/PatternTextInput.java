// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
/** an input that validates text against a pattern
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PatternTextInput.java,v 1.3 2014/09/15 14:30:20 spb Exp $")

public class PatternTextInput extends TextInput implements TagInput, PatternInput{
   private final Pattern validate_pattern;
   private final String pattern;
   private String tag=null;
   public PatternTextInput(String pattern){
	   super(false);
	   setOptional(false);
	   this.pattern=pattern;
	   validate_pattern = Pattern.compile(pattern);
   }


   public void setTag(String tag){
	   this.tag=tag;
   }
   /* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.forms.inputs.PatternInput#getPattern()
 */
public String getPattern(){
	   return pattern;
   }
public String getTag() {
	if( tag != null ){
		return tag;
	}
	return "Regexp: "+pattern;
}

@Override
public void validate() throws FieldException {
	super.validate();
		if( validate_pattern.matcher(getValue()).matches()) {
			return;
		}
		throw new ValidateException("Input does not match required pattern "+pattern);
}
}