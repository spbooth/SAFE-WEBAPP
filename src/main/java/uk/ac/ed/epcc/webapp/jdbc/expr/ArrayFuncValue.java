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
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;



/** A {@link SQLValue} that selects one of a set of values under control of
 * an {@link ArrayFunc} (e.g. greatest/least).
 * 
 * @author spb
 *
 * @param <T>
 */
public class ArrayFuncValue<F,T> implements SQLValue<T> {
	
	
    private final ArrayFunc func;
    private final SQLValue<? extends T> e[];
    private final String filter_tag;
    private final Class<T> target_class;
    private int sizes[];
    public ArrayFuncValue(String tag,ArrayFunc f, Class<T> target, SQLValue<? extends T> ... e){
    	this.filter_tag=tag;
    	assert(f!=null);
    	// expression may be null 
    	func=f;
    	this.target_class=target;
    	this.e=e;
    }
	public int add(StringBuilder sb, boolean qualify) {
		int res=0;

		sizes = new int[e.length];
		boolean seen=false;
		int i=0;
		for(SQLValue<? extends T> x : e){
			if( seen ){
				sb.append(",");
			}
			res += (sizes[i++]=x.add(sb,qualify));
			seen=true;
		}
		
		return res;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(SQLValue<? extends T> x : e){
			x.getParameters(list);
		}
		return list;
	}
	
	@Override
	public String toString(){
		StringBuilder sb= new StringBuilder();
		sb.append(func.name());
		sb.append("(");
		boolean seen=false;
		for(SQLValue<? extends T> x : e){
			if( seen ){
				sb.append(",");
			}
			sb.append(x.toString());
			seen=true;
		}
		sb.append(")");
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	public T makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		T result = null;
		int i=0;
		for(SQLValue<? extends T> x : e){
			result = (T) func.combine((Comparable)result,(Comparable) x.makeObject(rs, pos));
			pos+=sizes[i++];
		}
		return result;
	}
	public Class<T> getTarget() {
		return target_class;
	}
	public SQLFilter<F> getRequiredFilter() {
		SQLAndFilter<F> result = null;
		for(SQLValue<? extends T> x: e){
			SQLFilter fil = x.getRequiredFilter();
			if(fil != null){
				if( result == null){
					result = new SQLAndFilter<>(filter_tag);
				}
				result.addFilter(fil);
			}
		}
		return result;
	}

}