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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.filter.LinkClause;
/** common base for classes that generate results via SQL.
 * It uses a filter class to specify what records are to be retrieved and a 
 * ResultMapper to convert this data into a domain object.
 * The methods to set the filter and ResultMapper are protected so subclasses can choose to expose these in their
 * public interface or to set fixed values in their constructor.
 * @author spb
 * @param <T> Type of filter
 * @param <O> Type of object being produced
 *
 */
public abstract class FilterReader<T,O> extends FilterSelect<T> implements Contexed{
	private final AppContext ctx;
	private final String tag;
	private BaseFilter<? super T> my_filter;

	private ResultMapper<O> mapper;
	
	
	private boolean qualify=false;
	private boolean db_lock=false;
	public FilterReader(AppContext c,String tag){
		ctx=c;
		this.tag=tag;
	}

	public final AppContext getContext() {
		return ctx;
	}
	/** Get the tag used to create the connection for this query from 
	 * a {@link DatabaseService}. 
	 * 
	 * Normally this just queries the repository for the main table in the query
	 * (as this table HAS to take part) and
	 * relies on the query failing if there is an illegal join.
	 * Any more advanced checking should really be done when constructing the filter
	 * as this is where there is a chance to recover from the error.
	 * 
	 * 
	 * @return
	 */
	protected abstract String getDBTag();
	
	protected abstract void addSource(StringBuilder sb);
	protected abstract Set<Repository> getSourceTables();
	
	/**
	 * accessor for cached copy of the filter for use by sub-classes
	 * 
	 * @return BaseFilter
	 */
	protected final BaseFilter<? super T> getFilter() {
		@SuppressWarnings("unchecked")
		SQLFilter<T> mapper_filter = mapper.getRequiredFilter();
		if( mapper_filter == null ){
			return my_filter;
		}else{
			if( my_filter == null ){
				return mapper_filter;
			}else{
				// both non null
				if( my_filter instanceof SQLFilter ){
					SQLAndFilter<T> res = new SQLAndFilter<>(tag);
					res.addFilter(mapper_filter);
					res.addFilter((SQLFilter<? super T>) my_filter);
					return res;
				}else{
					AndFilter<T> res = new AndFilter<>(tag);
					res.addFilter(mapper_filter);
					res.addFilter(my_filter);
					return res;
				}
			}
		}
	}
	protected final void setFilter(BaseFilter<? super T> f){
		if( f != null ){
			String fil_target = f.getTag();
			//TODO enforce this always but run with assert for a bit.
			assert(fil_target == null || tag == null ||  fil_target.equals(tag));
//			if( fil_target != null && ! fil_target.isAssignableFrom(target)){
//				throw new ConsistencyError("Incompatible filter passed to FilterReader "+target.getCanonicalName()+" "+fil_target.getCanonicalName());
//			}
		}
		my_filter=f;
	}
	/** Set the qualify flag explicitly.
	 * 
	 * Needed for when joins added by the Iterator. 
	 * 
	 * @param q
	 */
	public final void setQualify(boolean q){
		qualify=q;
	}
	/** Should field names be qualified.
	 * 
	 * Sub-classes can override to force this to true;
	 * 
	 * @return boolean true if fields should be qualified
	 */
	public boolean getQualify() {
		return qualify;
	}
	protected final void makeSelect(StringBuilder query) {
		StringBuilder join = new StringBuilder();
		boolean use_join=false;
		BaseFilter<? super T> filter = getFilter();
		Set<LinkClause> additions = new LinkedHashSet<>();
		
		Set<Repository> tables = getSourceTables();
		// Note this is a check on the JoinFilter interface not visitor behaviour
		// the combine filters also implement the interface and can provide a join clause
		if( filter instanceof JoinFilter ){
			((JoinFilter) filter).addJoin(tables, join, additions);
		}
		use_join = tables.size() > 1;
		if( use_join || getQualify() || ( filter instanceof MultiTableFilter &&((MultiTableFilter)filter).qualifyTables())){
			mapper.setQualify(true);
			qualify=true;
		}
		query.append("SELECT ");
		query.append(mapper.getTarget());
		query.append(" FROM ");
		addSource(query);
		if( join.length() > 0 ){ 
			  query.append(" ");
			  query.append(join);
		}
		query.append(" WHERE ");
		if( ! additions.isEmpty()) {
			for(LinkClause l : additions) {
				l.addLinkClause(query);
				query.append(" AND ");
			}
		}
		makeWhere(tables,my_filter,query,getQualify());
	}

	
	protected final String getModify(){
		return  mapper.getModify();
	}
	protected final String getLockClause() {
		if( useDbLock()) {
			DatabaseService db = getContext().getService(DatabaseService.class);
			if(db.inTransaction() ) {
				return " FOR UPDATE";
			}
		}
		return "";
	}
	public List<PatternArgument> getModifyParameters(List<PatternArgument> list){
		return mapper.getModifyParameters(list);
	}
	public List<PatternArgument> getTargetParameters(List<PatternArgument> list){
		return mapper.getTargetParameters(list);
	}
	protected final void setMapper(ResultMapper<O>m){
		this.mapper=m;
	}
	
	
	/**
	 * generate a target Object from the ResultSet 
	 * 
	 * It is legal for this method to return null. This usually indicates an
	 * illegal database entry that should be ignored by the code.
	 * Iterators will skip the value, makers will return the default result. 
	 * 
	 * @param rs
	 * @return DataObject or null
	 * @throws DataException
	 * @throws SQLException 
	 */
	public final O makeEntry(ResultSet rs) throws DataException, SQLException  {
		return mapper.makeObject(rs);
	}
	public final O makeDefault(){
		return mapper.makeDefault();
	}
	
	public final String getTag(){
		return tag;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+" [my_filter=" + my_filter + ", mapper=" + mapper + "]";
	}

	/**
	 * @return the db_lock
	 */
	private boolean useDbLock() {
		return db_lock;
	}

	/**
	 * @param db_lock the db_lock to set
	 */
	private void setDbLock(boolean db_lock) {
		this.db_lock = db_lock;
	}
}