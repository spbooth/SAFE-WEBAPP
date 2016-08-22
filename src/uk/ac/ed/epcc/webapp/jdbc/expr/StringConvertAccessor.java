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
package uk.ac.ed.epcc.webapp.jdbc.expr;




/** A {@link Accessor} that converts the result of a nested {@link Accessor} to a string.
 * 
 * @author spb
 *
 * @param <T>
 * @param <R>
 */
public class StringConvertAccessor<T,R> implements Accessor<String,R> {

	protected Accessor<T,R> a;

	public StringConvertAccessor(Accessor<T,R> acc) {
		a=acc;
	}

	public Class<? super String> getTarget() {
		return String.class;
	}

	public String getValue(R r) {
	    T temp = a.getValue(r);
	    if( temp != null ){
	    	return temp.toString();
	    }
		return null;
	}
	public boolean canSet() {
		
		return false;
	}
	public void setValue(R r, String value) {
		throw new UnsupportedOperationException("Set not supported");
		
	}
    @Override
	public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("String(");
    	if (a instanceof SQLExpression) {
			((SQLValue) a).add(sb, true);
		} else {
			sb.append(a.toString());
		}
    	sb.append(")");
    	return sb.toString();
    }
}