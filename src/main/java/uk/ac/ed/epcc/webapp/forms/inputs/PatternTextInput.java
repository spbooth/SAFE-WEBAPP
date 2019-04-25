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

import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
/** an input that validates text against a pattern
 * 
 * @author spb
 *
 */


public class PatternTextInput extends TextInput implements TagInput, PatternInput{
   /**
	 * @author Stephen Booth
	 *
	 */
	public final class PatternFieldValidator implements FieldValidator<String> {
		private final Pattern validate_pattern;
		/**
		 * 
		 */
		private final String pattern;

		/**
		 * @param pattern
		 */
		public PatternFieldValidator(String pattern) {
			this.pattern = pattern;
			validate_pattern = Pattern.compile(pattern);
		}

		@Override
		public void validate(String v) throws FieldException {
			if( v == null || v.isEmpty()) {
				return;
			}
			if( validate_pattern.matcher(v).matches()) {
				return;
			}
			throw new ValidateException("Input does not match required pattern "+pattern);
			
		}
	}

   private final String pattern;
   private String tag=null;
   public PatternTextInput(String pattern){
	   super();
	   this.pattern=pattern;
	   
	   addValidator(new PatternFieldValidator(pattern));
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


}