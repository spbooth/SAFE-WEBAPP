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

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue;
import uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.filter.FieldOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.Joiner;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;

/** A SQLAccessor for a field referencing a remote table.
 * This accessor returns an IndexedReference for the remote object.
 * 
 * @author spb
 * @param <T> type of owning object
 * @param <I> type of remote object
 */


public class IndexedFieldValue<T extends DataObject,I extends DataObject> implements FieldValue<IndexedReference,T>,IndexedSQLValue<T,I> ,Selector,GroupingSQLValue<IndexedReference>{
	private final Class<T> target;
	private final Repository repository;
	private final IndexedTypeProducer<I,? extends DataObjectFactory<I>> producer;
	public IndexedFieldValue(Class<T> target,Repository repository, IndexedTypeProducer<I,? extends DataObjectFactory<I>> producer) {
		this.target=target;
		this.repository=repository;
		this.producer=producer;
		
	}
	
	@Override
	public IndexedReference<I> getValue(T r) {
		// again if not set we use a zero id value
		return producer.makeReference(r.record.getIntProperty(producer.getField(), 0));
	}
	public I getIndexed(T r){
		return producer.find(r.record.getNumberProperty(producer.getField()));
	}
	public IndexedTypeProducer<I,? extends DataObjectFactory<I>> getProducer(){
		return producer;
	}
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		repository.getInfo(producer.getField()).addName(sb, qualify, true);
		return 1;
	}
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	
	@Override
	public IndexedReference<I> makeObject(ResultSet rs, int pos)
			throws DataException, SQLException {
			int id=rs.getInt(pos);
			IndexedReference<I> res =  makeReference(id);
			// For the moment return a reference to zero to denote null
//			if( rs.wasNull()){
//				return null;
//			}
			return res;
	}
	public IndexedReference<I> makeReference(int id) {
		return producer.makeReference(id);
	}
	
	@Override
	public void setValue(T r, IndexedReference value) {
		if( value == null ){
			r.record.setProperty(producer.getField(), 0);
		}else{
			r.record.setProperty(producer.getField(), value.getID());
		}
		
	}
	@Override
	public void setObject(PreparedStatement stmt, int pos,
			IndexedReference value) throws SQLException {
		stmt.setInt(pos,value.getID());
		
	}
	@Override
	public Class<IndexedReference> getTarget() {
		return IndexedReference.class;
	}
	@Override
	public String toString(){
		String table;
		try {
			table = getFactory().getTag();
		} catch (Exception e) {
			table="unknown";
		}
		String field=producer.getField();
		
		FieldInfo info = this.repository.getInfo(field);
		return info.getName(true)+(info.isIndexed()?"(Index)":"")+"->"+table;
	}
	@Override
	public SQLFilter<T> getFilter(MatchCondition match,
			IndexedReference val) {
		if(match == null ){
			return new SQLValueFilter<>(target,repository,producer.getField(),val.getID());
		}
		// This is slightly meaningless but there might be some call for it.
		return new SQLValueFilter<>(target,repository,producer.getField(),match,val.getID());
	}
	@Override
	public SQLFilter<T> getNullFilter(boolean is_null){
		return new NullFieldFilter<>(target,repository, producer.getField(), is_null);
	}
	@Override
	public SQLFilter<T> getOrderFilter(boolean descending){
		return new FieldOrderFilter<>(target,repository, producer.getField(), descending);
	}
	/** Create a filter for the home table out of a a filter on the target object
	 * 
	 * @param fil
	 * @return SQLFilter<T>
	 * @throws CannotFilterException
	 */
	@Override
	public SQLFilter<T> getSQLFilter(SQLFilter<I> fil) throws CannotFilterException{
		try{
			IndexedProducer<I> ip = producer.getProducer();
			if( ip instanceof DataObjectFactory){
				DataObjectFactory fac = (DataObjectFactory) ip;
				
				return Joiner.getRemoteFilter(getFilterType(),fil,producer.getField(),repository,fac.res);
			}
			throw new CannotFilterException("Not referencing a DataObjectFactory");
		}catch(Exception e){
			throw new CannotFilterException(e);
		}
	}
	@Override
	public String getFieldName() {
		return producer.getField();
	}
	@Override
	public SQLFilter getRequiredFilter() {
		return null;
	}
	@Override
	public Input getInput() {
		try {
			IndexedProducer<I> ip = producer.getProducer();
			if( ip instanceof Selector){
				return ((Selector)ip).getInput();
			}
		} catch (Exception e) {
			repository.getContext().error(e,"Error getting factory");
		}
		return null;
	}

	@Override
	public DataObjectFactory<I> getFactory() throws Exception {
		return (DataObjectFactory<I>) producer.getProducer();
	}

	@Override
	public boolean canSet() {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	@Override
	public Class<T> getFilterType() {
		return target;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getIDExpression()
	 */
	@Override
	public SQLExpression<Integer> getIDExpression() {
		return repository.getNumberExpression(getFilterType(), Integer.class, getFieldName());
		
	}

}