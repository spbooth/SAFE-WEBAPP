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
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;


/** An input that checks for existing records of the same name.
 * 
 * This extends {@link NoHtmlInput} as these are user selected names that
 * are likely to be displayed in pages.
 * @author spb
 * @param <F> type of target object
 *
 */
public class UnusedNameInput<F extends DataObject> extends NoHtmlInput{
	private final ParseFactory<F> fac;
	private final F existing;
	public UnusedNameInput(ParseFactory<F> fac,F existing){
		this.fac = fac;
		this.existing=existing;
		setSingle(true);
		// existing null means that name must not be in use. if non-null
		// the name of the existing element is allowed
		addValidator(new UnusedNameValidator<F>(fac, existing));
		if( existing != null ){
			setText(fac.getCanonicalName(existing));	
		}
	}
    public UnusedNameInput(ParseFactory<F> fac){
    	this(fac,null);
    }
	
	
}