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
package uk.ac.ed.epcc.webapp.email.inputs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Email address input that dis-allows certain types of address
 * 
 * @author spb
 *
 */


public class RestrictedEmailInput extends EmailInput {

	private Pattern bad_patterns[]=null;
	
	public RestrictedEmailInput(String bad_pattern_list){
		super();
		if(bad_pattern_list != null){
			String list[]=bad_pattern_list.split(",");
			bad_patterns = new Pattern[list.length];
			for(int i=0;i<list.length;i++){
				bad_patterns[i] = Pattern.compile(list[i]);
			}
		}
	}

	@Override
	public void validate() throws FieldException {
		super.validate();
		
		if( bad_patterns != null ){
			String email = getValue();
			if(email != null ){
				for(Pattern p : bad_patterns){
					Matcher m = p.matcher(email);
					if( m.find()){
						throw new ValidateException("Forbidden email address "+p.pattern());
					}
				}
			}
		}
	}
}