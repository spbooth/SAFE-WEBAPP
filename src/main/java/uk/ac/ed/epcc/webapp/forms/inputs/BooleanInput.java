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

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;





public class BooleanInput extends AbstractInput<Boolean> implements ParseInput<Boolean> ,BinaryInput<Boolean>{
    

	public Boolean parseValue(String v) throws ParseException {
		if( v==null || v.trim().length() == 0){
			// unchecked boxes are false
			return Boolean.FALSE;
		}else{
			return Boolean.valueOf(v);
		}
	}

	public Boolean convert(Object v) throws TypeError {
		if( v instanceof String ){
		    return Boolean.valueOf((String) v);
		}
		if( v instanceof Boolean){
			return (Boolean) v;
		}
		return Boolean.FALSE;
	}

	

	public String getPrettyString(Boolean value) {
		return value.toString();
	}

	public String getString(Boolean value) {
		return value.toString();
	}


	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitBinaryInput(this);
	}

	public boolean isChecked() {
		Boolean v = getValue();
		if( v == null) {
			return false;
		}
		return v;
	}

	public void setChecked(boolean value) {
		setValue(value);
	}

	public String getChecked() {
		return "true";
	}

}