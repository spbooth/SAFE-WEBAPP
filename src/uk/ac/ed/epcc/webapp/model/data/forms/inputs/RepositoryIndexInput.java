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
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Input to select one of the fields of a Repository
 * 
 * @author spb
 *
 */


public class RepositoryIndexInput implements ListInput<String,String>{
    private final Repository res;
    private String key=null;
    private String value;
    public RepositoryIndexInput(Repository res){
    	this.res=res;
    }
	public String getItembyValue(String value) {
		return value;
	}

	public Iterator<String> getItems() {
		return res.getIndexNames().iterator();
	}
	public int getCount(){
		return res.getInfo().size();
	}

	public String getTagByItem(String item) {
		return item;
	}

	public String getTagByValue(String value) {
		return value;
	}

	public String getText(String item) {
		if( item == null ){
			return "None";
		}
		return item;
	}

	public String convert(Object v) throws TypeError {
		if( v instanceof String){
			return (String)v;
		}
		throw new TypeError();
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(String value) {
		return value;
	}

	public String getString(String value) {
		return value;
	}

	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key=key;
	}

	public String setValue(String v) throws TypeError {
		String old = getValue();
		value=v;
		return old;
	}

	public void validate() throws FieldException {
		if( value == null ){
			throw new MissingFieldException();
		}
	}

	public String getItem() {
		return value;
	}

	public void setItem(String item) {
		this.value=item;
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

}