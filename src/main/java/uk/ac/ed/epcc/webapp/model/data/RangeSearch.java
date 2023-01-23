//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLGroupMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterFinder;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** Search for an integer id from a range which is not allocated
 * to any record from a table.
 * 
 * This is used to allocate un-used id values.
 * It will default to allocating bottom to top but will attempt to fill
 * in gaps once the top value is reached.
 * 
 * 
 * 
 * @author Stephen Booth
 *
 */
public class RangeSearch<D extends DataObject> {

	public static class Info {
		/**  result of a query operation.
		 * 
		 * The count value is an approximation to the number of distinct values.
		 * However this may make the sql query expensive so an upper bound from
		 * the number of matching records is accepable.
		 * 
		 * @param min  mimimum value found
		 * @param max  maximum value fount
		 * @param count (number of distinct values or larger)
		 */
		public Info(long min, long max, long count) {
			super();
			this.min = min;
			this.max = max;
			this.count = count;
		}
		final long min;   // minimum value
		final long max;   // maximum value
		final long count; // matching records 
	}
	public class InfoMapper extends SQLGroupMapper<Info>{

		/**
		 * @param c
		 */
		public InfoMapper(AppContext c) {
			super(c);
			addMinNumber(fac.res.getNumberExpression(Long.class, field_name), null);
			addMaxNumber(fac.res.getNumberExpression(Long.class, field_name), null);
			addSQLCount(fac.res.getNumberExpression(Long.class, field_name), null);
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#makeObject(java.sql.ResultSet)
		 */
		@Override
		public Info makeObject(ResultSet rs) throws DataException, SQLException {
			
				return new Info(getValue(0,rs),getValue(1,rs),getValue(2,rs));
			
			
		}
		private long getValue(int pos,ResultSet rs) throws DataException, SQLException {
			return ((Number)getTargetObject(pos, rs)).longValue();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#makeDefault()
		 */
		@Override
		public Info makeDefault() {
			return new Info(-1L,-1L,0L);
		}
		
	}
	public class InfoFinder extends FilterFinder<D, Info>{
		public InfoFinder() {
			super(fac.getContext(),fac.getTag());
			setMapper(new InfoMapper(fac.getContext()));
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#getDBTag()
		 */
		@Override
		protected String getDBTag() {
			return fac.res.getDBTag();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#addSource(java.lang.StringBuilder)
		 */
		@Override
		protected void addSource(StringBuilder sb) {
			fac.res.addSource(sb,true );
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#getSourceTables()
		 */
		@Override
		protected Set<Repository> getSourceTables() {
			HashSet<Repository> set = new HashSet<>();
			set.add(fac.res);
			return set;
		}
	}
	private final DataObjectFactory<D> fac;
	private final String field_name;
	private final InfoFinder finder;
	/**
	 * 
	 */
	public RangeSearch(DataObjectFactory<D> fac,String field) {
		this.fac=fac;
		this.field_name=field;
		this.finder=new InfoFinder();
	}

	/** search for a positive id value not used by any record in the
	 * repository
	 * 
	 * @param min  minimum legal value (inclusive)
	 * @param max  maximum legal value (inclusive)
	 * @return un-allocated id or -1;
	 * @throws DataException 
	 */
	public long search(long min, long max) throws DataException {
		Logger log = fac.getContext().getService(LoggerService.class).getLogger(getClass());
		log.debug("Searching: "+min+" - "+max);
		if( min < 0 || max < min) {
			log.debug("Bad range");
			return -1;
		}
		Info q = query(min,max);
		log.debug("Min="+q.min+" max="+q.max+" count="+q.count);
		if( q.count == 0) {
			log.debug("No entries returning "+min);
			// no entries at all
			return min;
		}else if( q.max < max && q.max >= min) {
			log.debug("room at the top "+(q.max+1));
			// room at the top
			return q.max+1;
		}else if( q.min > min ) {
			log.debug("room at the bottom "+(q.min-1));
			// room at the bottom
			return q.min-1;
		}else if( q.count >= (max-min+1)) {
			// May not be any space at all
			// there could be space if some records
			// have duplicates but assume not and quit
			log.debug("No space");
			return -1;
		}
		min++; // know these are taken
		max--;
		long mid = (min+max)/2L;
		log.debug("mid is "+mid);
		if( mid > min && mid < max) {
			// search lower half
			long res = search(min,mid);
			if( res >= min) {
				log.debug("found in first half "+res);
				return res;
			}
			// now upper half
			return search(mid+1,max);
		}else {
			return search(min,max);
		}
	}
	
	private Info query(long min, long max) throws DataException {
		return finder.find(fac.getSQLAndFilter( 
				new SQLValueFilter<D>(fac.res, field_name, MatchCondition.GE, min),
				new SQLValueFilter<D>(fac.res, field_name, MatchCondition.LE, max)
				));
	}
}
