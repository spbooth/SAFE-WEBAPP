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

import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/** Iterator over filter results.
 * 
 *  * <p>
	 * For large queries the results are generated in batches when the
	 * <b>chunking</b> feature is enabled, to reduce memory
	 * use. It is up to the calling method not to retain too many references to
	 * the generated objects. Note this uses SQL limit clauses to edits that change
	 * the returned row ordering should be avoided within the iteration loop.
	 * <p>
	 * 
	 * 
	 * Note that where the filter implements more than one of the supported
	 * filter interfaces AcceptFilter, ConditionFilter, PatternFilter The
	 * iterator will return the intersection of the sets selected by each
	 * interface.
	 * <p>
	 * The ResultMapper, target, source and order clauses are set as methods
	 * 
 * @param <O> Type of object produced.
 * 
 */
public abstract class ResultIterator<O> extends SQLResultIterator<O,O> implements java.util.Iterator<O> {
		

		private AcceptFilter<? super O> f = null;
        private ResultVisitor<O> vis = null;
	
		
		
        protected ResultIterator(AppContext c,Class<? super O> target){
            super(c,target);
        }
		
		
	    protected ResultVisitor<O> setVisitor(ResultVisitor<O> vis){
	    	ResultVisitor<O> tmp=this.vis;
	    	this.vis=vis;
	    	return tmp;
	    }

		

		

	
		/**
		 * Get the next object from the stream and store it in next. Once the
		 * end of the stream is reached set next to null
		 * 
		 * @throws SQLException
		 * @throws DataException 
		 */
		@Override
		protected final O iterate() throws SQLException, DataException {
			//Logger log = getLogger();
			//

			//log.debug("In iterate");
			if( f == null ){
				return super.iterate();
			}
			O next=super.iterate();
			while( next != null && ! f.accept(next)){
				next=super.iterate();
			}
			if( next != null && vis != null ){
				vis.visit(next);
			}
			return next;
		}

		
		
	

		

		

		/** close the underlying statement etc. to free resources
		
		
		
		/** method to do the initialisation
		 * 
		 * @param f
		 * @param start
		 * @param max
		 * @throws DataFault
		 */
		@Override
		protected void setup(BaseFilter<? super O> f, int start, int max) throws DataException {
			// must come before super.setup as setup calls iterate
			if (f instanceof AcceptFilter) {
				this.f = (AcceptFilter<? super O>) f;
			}
			if( f instanceof ResultVisitor){
				setVisitor((ResultVisitor<O>) f);
			}
			super.setup(f,start,max);
		}

	}