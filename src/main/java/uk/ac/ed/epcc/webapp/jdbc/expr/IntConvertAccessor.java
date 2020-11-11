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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;





public class IntConvertAccessor<T,R> implements Accessor<Integer,R> {

	protected Accessor<T,R> a;

	public IntConvertAccessor(Accessor<T,R> acc) {
		a=acc;
	}

	public Class<Integer> getTarget() {
		return Integer.class;
	}

	public Integer getValue(R r) {
	    T temp = a.getValue(r);
	    if( temp != null ){
	    	if( temp instanceof Number ){
	    		return Integer.valueOf(((Number)temp).intValue());
	    	}
	    	if( temp instanceof String){
	    		return Integer.parseInt((String)temp);
	    	}
	    }
		return null;
	}
    @Override
	public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("String(");
		sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
}