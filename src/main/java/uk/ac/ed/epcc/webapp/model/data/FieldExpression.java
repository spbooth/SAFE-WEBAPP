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
/**
 * 
 */
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;

/** Abstract class for SQL fields that can be used in expressions.
 * 
 * Sub-classes effectively have to forward the methods equivalent to Accessor targeted on Record. 
 * @author spb
 *
 * @param <T> data type
 * @param <X> target type
 */
public abstract class FieldExpression<T,X extends DataObject> implements FieldSQLExpression<T,X>{
	public static final Feature LOG_FETCH = new Feature("log_fetch",false,"add type logging to FieldExpression");
	protected final Repository repository;
	protected  final String name;
	protected final Class<T> target;
	protected final Class<X> filter_type;
	protected FieldExpression(Class<X> filter_type,Repository repository, Class<T> target,String name){
		this.repository = repository;
		this.target=target;
		this.name=name;
		this.filter_type=filter_type;
		assert(repository.hasField(name));
	}
	public int add(StringBuilder sb, boolean qualify) {
		// quote when adding to a SQL fragment
		repository.getInfo(name).addName(sb,qualify,true);
		return 1;
	}

	public T makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		T result = Repository.makeTargetObject(target, rs, pos);
		if( result != null && LOG_FETCH.isEnabled(repository.getContext())){
			repository.getContext().getService(LoggerService.class).getLogger(getClass()).debug(
					"Fetch type "+name+" is "+result.toString()+" type "+result.getClass().getCanonicalName()
					);
		}
		return result;
	}
	
	public final Class<T> getTarget() {
		return target;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.FieldSQLExpression#getFieldName()
	 */
	public String getFieldName() {
		return name;
	}
	public void setObject(PreparedStatement stmt, int pos, T value)
			throws SQLException {
		repository.setObject(stmt, pos, name, repository.convert(name,value));
		
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("FieldExpression(");
		repository.getInfo(name).addName(sb,true,false);
		sb.append("->");
		sb.append(target.getName());
		sb.append(")");
		return sb.toString();
	}
	public SQLFilter getRequiredFilter() {
		return null;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	public boolean canSet() {
		return true;
	}
	protected abstract T getValue(Record r);
	public final  T getValue(X r) {
		return getValue(r.record);
	}
	protected abstract void setValue(Record r, T value);
	public final void setValue(X r, T value) {
		setValue(r.record,value);
	}
	public final Class<X> getFilterType(){
		return filter_type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((repository == null) ? 0 : repository.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldExpression other = (FieldExpression) obj;
		if (filter_type == null) {
			if (other.filter_type != null)
				return false;
		} else if (!filter_type.equals(other.filter_type))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (repository == null) {
			if (other.repository != null)
				return false;
		} else if (!repository.equals(other.repository))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
}