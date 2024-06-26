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
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.StringListInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Input to select one of the fields of a Repository
 * 
 * @author spb
 *
 */


public class RepositoryIndexInput extends StringListInput{
    private final Repository res;

    public RepositoryIndexInput(Repository res){
    	this.res=res;
    }
	

	public Iterator<String> getItems() {
		return res.getIndexNames().iterator();
	}
	public int getCount(){
		return res.getInfo().size();
	}

	
	
	public String getText(String item) {
		if( item == null ){
			return "None";
		}
		return item;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(String item) {
		return res.getIndexNames().contains(item);
	}

}