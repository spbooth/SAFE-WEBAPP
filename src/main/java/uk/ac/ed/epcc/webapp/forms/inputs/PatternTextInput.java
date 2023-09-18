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

/** an input that validates text against a pattern
 * 
 * @author spb
 *
 */


public class PatternTextInput extends TextInput implements TagInput, PatternInput{
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
@Override
public String getPattern(){
	   return pattern;
   }
@Override
public String getTag() {
	if( tag != null ){
		return tag;
	}
	return "Regexp: "+pattern;
}
}