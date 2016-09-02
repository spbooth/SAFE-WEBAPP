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
package uk.ac.ed.epcc.webapp.model.data.filter;

import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/** Filter to join two tables, selecting entries based on a filter on the remote table 
 *
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */


public class Joiner<T extends DataObject, BDO extends DataObject> extends SQLAndFilter<BDO> implements JoinFilter<BDO> {
		public Joiner(Class<? super BDO> target,SQLFilter<? super T> fil, String join_field, Repository res, Repository remote_res){
			this(target,fil,join_field,res,remote_res,true);
		}
		/**
		 * 
		 * @param target type of target
		 * @param fil        Filter on remote table
		 * @param join_field String reference field
		 * @param res        Repository of target
		 * @param remote_res Repository of remote
		 * @param target_references boolean true if target points to remote. false if remote points to target
		 */
		@SuppressWarnings("unchecked")
		public Joiner(Class<? super BDO> target,SQLFilter<? super T> fil, String join_field, Repository res, Repository remote_res,
			boolean target_references	){
			super(target);
			if( ! target_references){
				// use an EXISTS clause to select so as not to expand 
				// result set.
				addFilter(new BackJoinFilter<T, BDO>(target,join_field, res, remote_res, fil));
			}else{
				addFilter(new JoinerFilter(target,join_field, res, remote_res, target_references));
				// note we are  using a non-generic type here to force a foreign type of filter to be
				// included in the clause.
				SQLFilter f = fil;
				add(f,false); // remote end filter
			}
     }
		/** Join to a fixed row of the remote table.
		 * 
		 * @param target
		 * @param fil
		 * @param remote_res
		 * @param ref
		 */
		public Joiner(Class<? super BDO> target, SQLFilter<? super T> fil, Repository remote_res,int ref){
			super(target);
			addFilter(new ConstJoinerFilter<T,BDO>(target, ref, remote_res));
			SQLFilter f = fil;
			add(f,false);
		}
}