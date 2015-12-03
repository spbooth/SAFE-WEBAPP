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

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;



public class SQLSelectAccessor<T,R> implements SQLAccessor<T,R> {
    private final Class<? super T> target;
    private final SQLAccessor<T,R> accessors[];
    private final int offsets[];
    
    public SQLSelectAccessor(Class<? super T> target, SQLAccessor<T,R> accessors[]){
    	this.target=target;
    	this.accessors=accessors;
    	offsets=new int[accessors.length];
    }
	
	public Class<? super T> getTarget() {
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

	public int add(StringBuilder sb, boolean qualify) {
		int count=0;
		for(int i=0;i<accessors.length;i++){
			if( i > 0){
				sb.append(" , ");
			}
			offsets[i]=count;
			count += accessors[i].add(sb, qualify);
		}
		
		return count;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(int i=0;i<accessors.length;i++){
			list = accessors[i].getParameters(list);
		}
		return list;
	}
	
	public T makeObject(ResultSet rs, int pos) throws DataException {
		for(int i=0;i<accessors.length;i++){
			T tmp = accessors[i].makeObject(rs, pos+offsets[i]);
			if( tmp != null ){
				return tmp;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public SQLFilter getRequiredFilter() {
		SQLAndFilter required = null;
		for(int i=0;i<accessors.length;i++){
			SQLFilter tmp = accessors[i].getRequiredFilter();
			if( tmp != null){
				if(required==null){
					required=new SQLAndFilter(tmp.getTarget());
				}
				required.addFilter(tmp);
			}
		}
		return required;
	}
	public boolean canSet() {
		
		return false;
	}
	public void setValue(R r, T value) {
		throw new UnsupportedOperationException("Set not supported");
		
	}
}