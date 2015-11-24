// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FieldOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.PrimaryOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SelfReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Field value for the current record
 * 
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SelfSQLValue.java,v 1.12 2014/11/10 19:11:56 spb Exp $")

public class SelfSQLValue<T extends DataObject> implements SQLAccessor<IndexedReference<T>,T>,FilterProvider<T, IndexedReference<T>> {
	private final Repository repository;
	private final Class<? extends DataObjectFactory<T>> clazz;
	private final Class<? super T> target_class;
	public SelfSQLValue(Class<? super T> target_class,Repository repository, Class<? extends DataObjectFactory<T>> clazz) {
		this.target_class=target_class;
		this.repository=repository;
		this.clazz=clazz;
		
	}
	
	public IndexedReference<T> getValue(Record r) {
		// again if not set we use a zero id value
		return makeReference(r.getID());
	}
	
	public int add(StringBuilder sb, boolean qualify) {
		repository.addUniqueName(sb, qualify, true);
		return 1;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	
	public IndexedReference<T> makeObject(ResultSet rs, int pos)
			throws DataException {
		try {
			int id=rs.getInt(pos);
			IndexedReference<T> res =  makeReference(id);
			return res;
		} catch (SQLException e) {
			  throw new DataFault("Error making IndexedReferencefield result",e);
		}
	}
	public IndexedReference<T> makeReference(int id) {
		return new IndexedReference<T>(id, clazz,repository.getTag());
	}
	
	
	public Class<IndexedReference> getTarget() {
		return IndexedReference.class;
	}
	@Override
	public String toString(){
		return "SelfId";
	}
	
	
	public SQLFilter getRequiredFilter() {
		return null;
	}
	
	public boolean canSet() {
		return false;
	}

	public final void setValue(T r, IndexedReference<T> value) {
		throw new ConsistencyError("Cannot set self reference");
	}

	public SQLFilter<T> getFilter(MatchCondition match, IndexedReference<T> val)
			throws CannotFilterException {
		if( match == null ){
			return new SelfReferenceFilter<T>(target_class,repository,val);
		}else if( match == MatchCondition.NE){
			return new SelfReferenceFilter<T>(target_class,repository,true,val);
		}
		throw new CannotFilterException("Relative MatchCondition requested for IndexedReference");
	}

	public SQLFilter<T> getNullFilter(boolean is_null)
			throws CannotFilterException {
		if( is_null){
			return new SQLAndFilter<T>(target_class);
		}
		return new FalseFilter<T>(target_class);
	}
	public SQLFilter<T> getOrderFilter(boolean descending)
			throws CannotFilterException {
		return new PrimaryOrderFilter<T>(target_class,repository, descending);
	}
	public IndexedReference<T> getValue(T r) {
		return getValue(r.record);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	public Class<? super T> getFilterType() {
		return target_class;
	}

	
	

}