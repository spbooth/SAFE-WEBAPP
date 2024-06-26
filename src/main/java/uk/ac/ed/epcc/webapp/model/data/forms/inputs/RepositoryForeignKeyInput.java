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
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** Input to select one of the foreign keys of a Repository
 * 
 * @author spb
 *
 */


public class RepositoryForeignKeyInput extends CodeListInput<Repository.FieldInfo>{
    private final Repository res;
    private Map<String,FieldInfo> data=new LinkedHashMap<>();
    public RepositoryForeignKeyInput(Repository res){
    	for(FieldInfo info : res.getInfo()){
    		if( info.isIndexed()){
    			String name = info.getForeignKeyName();
    			if( name != null ){
    				data.put(name, info);
    			}
    		}
    	}
    	this.res=res;
    }
	@Override
	public FieldInfo getItemByTag(String value) {
		return data.get(value);
	}

	@Override
	public Iterator<FieldInfo> getItems() {
		return data.values().iterator();
	}
	@Override
	public int getCount(){
		return data.size();
	}

	@Override
	public String getTagByItem(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getForeignKeyName();
	}


	@Override
	public String getText(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getForeignKeyName()+" "+item.getName(false);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(FieldInfo item) {
		return data.containsValue(item);
	}
	
}