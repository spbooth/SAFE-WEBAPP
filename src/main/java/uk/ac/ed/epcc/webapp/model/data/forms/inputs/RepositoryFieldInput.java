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

import uk.ac.ed.epcc.webapp.forms.inputs.CodeListInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** Input to select one of the fields of a Repository
 * 
 * @author spb
 *
 */


public class RepositoryFieldInput extends CodeListInput<Repository.FieldInfo>{
    private final Repository res;
    public RepositoryFieldInput(Repository res){
    	this.res=res;
    }
	
    @Override
    public FieldInfo getItembyValue(String value) {
		return res.getInfo(value);
	}

    @Override
	public Iterator<FieldInfo> getItems() {
		return res.getInfo().iterator();
	}
    @Override
	public int getCount(){
		return res.getInfo().size();
	}
    @Override
	public String getTagByItem(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getName(false);
	}

    @Override
	public String getText(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getName(false);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(FieldInfo item) {
		return res.hasField(item);
	}
	
	
}