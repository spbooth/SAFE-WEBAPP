//| Copyright - The University of Edinburgh 2016                            |
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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** A {@link FieldValidator} that checks for forbidden email patterns
 * @author spb
 *
 */
public class RestrictedEmailFieldValidator implements FieldValidator<String> {

    private String bad_pattern_text[]=null;
	private Pattern bad_patterns[]=null;
	
	/**
	 * @param bad_pattern_list 
	 * 
	 */
	public RestrictedEmailFieldValidator(String bad_pattern_list){
		if(bad_pattern_list != null){
			bad_pattern_text=bad_pattern_list.split(",");
			bad_patterns = new Pattern[bad_pattern_text.length];
			for(int i=0;i<bad_pattern_text.length;i++){
				bad_patterns[i] = Pattern.compile(bad_pattern_text[i]);
			}
		}
	}

	@Override
	public void validate(String email) throws ValidateException {
		
		if( bad_patterns != null ){
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bad_pattern_text);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestrictedEmailFieldValidator other = (RestrictedEmailFieldValidator) obj;
		if (!Arrays.equals(bad_pattern_text, other.bad_pattern_text))
			return false;
		return true;
	}
}
