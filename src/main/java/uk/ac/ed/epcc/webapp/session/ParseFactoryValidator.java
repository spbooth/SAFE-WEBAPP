//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.session;


import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** An {@link FieldValidator} that checks the value does not already resolve in a {@link ParseFactory}.
 * 
 * 
 * Optionally an allowed result can be specified to allow update operations.
 * @author spb
 * @param <BDO> type of object returned.
 *
 */

public class ParseFactoryValidator<BDO> implements FieldValidator<String> {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((existing == null) ? 0 : existing.hashCode());
		result = prime * result + ((parser == null) ? 0 : parser.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParseFactoryValidator other = (ParseFactoryValidator) obj;
		if (existing == null) {
			if (other.existing != null)
				return false;
		} else if (!existing.equals(other.existing))
			return false;
		if (parser == null) {
			if (other.parser != null)
				return false;
		} else if (!parser.equals(other.parser))
			return false;
		return true;
	}

	/**
	 * @param parser
	 * @param existing
	 */
	public ParseFactoryValidator(ParseFactory<BDO> parser, BDO existing) {
		super();
		this.parser = parser;
		this.existing = existing;
	}

	private final ParseFactory<BDO> parser;
	private final BDO existing;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.InputValidator#validate(uk.ac.ed.epcc.webapp.forms.inputs.Input)
	 */
	@Override
	public void validate(String value) throws ValidateException {
		
		BDO result = parser.findFromString(value);
		if( result != null ){
			if( existing == null ||  ! result.equals(existing)){
				throw new ValidateException("Already in use");
			}
		}
		
	}

}