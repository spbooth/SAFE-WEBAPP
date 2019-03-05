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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;

/** A SQLAccessor for a field with a {@link TypeProducer}
 * This accessor returns the produced object.
 * 
 * Unlike most {@link FieldValue}s it is neither a {@link FieldSQLExpression} not
 * a {@link FilterProvider} but it can generate filters via {@link FieldValueFilter}.
 * 
 * @author spb
 * @param <T> type of owning object
 * @param <O> type of produced object
 * @param <D> type of underlying data
 * @see FieldValueFilter
 */


public class TypeProducerFieldValue<T extends DataObject,O,D> implements FieldValue<O,T>,GroupingSQLValue<O> {
	private final Repository repository;
	private final TypeProducer<O,D> producer;
	public TypeProducerFieldValue(Repository repository, TypeProducer<O,D> producer) {
		this.repository=repository;
		this.producer=producer;
		
	}
	
	public O getValue(T r) {
		return r.record.getProperty(producer);
	}
	public TypeProducer<O,D> getProducer(){
		return producer;
	}
	public int add(StringBuilder sb, boolean qualify) {
		repository.getInfo(producer.getField()).addName(sb, qualify, true);
		return 1;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	
	public O makeObject(ResultSet rs, int pos)
			throws DataException, SQLException {
			return producer.find((D) rs.getObject(pos));
	}
	
	
	public void setValue(T r, O value) {
		if( value == null ){
			r.record.setProperty(producer.getField(), null);
		}else{
			r.record.setProperty(producer, value);
		}
		
	}
	public void setObject(PreparedStatement stmt, int pos,
			O value) throws SQLException {
		stmt.setObject(pos,producer.getIndex(value));
		
	}
	public Class<O> getTarget() {
		return producer.getTarget();
	}
	
	
	
	public String getFieldName() {
		return producer.getField();
	}
	
	

	public boolean canSet() {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return null;
	}

	

}