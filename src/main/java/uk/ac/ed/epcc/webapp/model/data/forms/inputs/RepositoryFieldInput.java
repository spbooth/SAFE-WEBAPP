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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.BaseInput;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeException;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** Input to select one of the fields of a Repository
 * 
 * @author spb
 *
 */


public class RepositoryFieldInput extends BaseInput<String> implements ListInput<String,Repository.FieldInfo>{
    private final Repository res;
    private FieldInfo item=null;
    public RepositoryFieldInput(Repository res){
    	this.res=res;
    }
	public FieldInfo getItembyValue(String value) {
		return res.getInfo(value);
	}

	public Iterator<FieldInfo> getItems() {
		return res.getInfo().iterator();
	}
	public int getCount(){
		return res.getInfo().size();
	}

	public String getTagByItem(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getName(false);
	}

	public String getTagByValue(String value) {
		return value;
	}

	public String getText(FieldInfo item) {
		if( item == null ){
			return null;
		}
		return item.getName(false);
	}

	public String convert(Object v) throws TypeException {
		if( v == null ) {
			return null;
		}
		if( v instanceof String){
			return (String)v;
		}
		throw new TypeException(v.getClass());
	}

	

	
	@Override
	public String getValue() {
		return getTagByItem(item);
	}

	@Override
	public final String getValueByItem(FieldInfo item) {
		return getTagByItem(item);
	}
	
	@Override
	public String setValue(String v){
		String old = getValue();
		item=res.getInfo(v);
		return old;
	}

	
	@Override
	public FieldInfo getItem() {
		return item;
	}

	@Override
	public void setItem(FieldInfo item) {
		this.item=item;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(FieldInfo item) {
		return res.hasField(item);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#validate()
	 */
	@Override
	public void setNull() {
		item=null;
		
	}
	@Override
	public String parseValue(String v) throws ParseException {
		if( v == null || v.trim().isEmpty()) {
			return null;
		}
		if( ! res.hasField(v)) {
			throw new ParseException("Invalid field "+v);
		}
		return v;
	}
	

}