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





public class SelectAccessor<T,R> implements Accessor<T,R> {
    private final Class<T> target;
    private final Accessor<T,R> accessors[];
    
    public SelectAccessor(Class<T> target, Accessor<T,R> accessors[]){
    	this.target=target;
    	this.accessors=accessors;
    }
	
	public Class<T> getTarget() {
		return target;
	}

	
	public T getValue(R r) {
		for(Accessor<T,R> a: accessors){
			T val = a.getValue(r);
			if( val != null ){
				return val;
			}
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Select(");
		boolean seen=false;
		for(Accessor<T,R> a: accessors){
			if( seen ){
				sb.append(",");
			}
			seen=true;
			sb.append(a.toString());
		}
		sb.append(")");
		return sb.toString();
	}

}