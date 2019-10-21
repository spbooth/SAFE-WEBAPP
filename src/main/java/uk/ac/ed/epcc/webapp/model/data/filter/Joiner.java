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

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Filter to join two tables, selecting entries based on a filter on the remote table 
 *
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */


public class Joiner<T extends DataObject, BDO extends DataObject> extends SQLAndFilter<BDO>  {
	/** Bypasses the normal type checks to insert the remote filter into the selection
	 * This constructor is private to ensure the static factory methods are called to ensure
	 * that one of the 
	 * 
	 * @param target
	 */
	private Joiner(Class<BDO> target) {
		super(target);
		
	}

	@SuppressWarnings("unchecked")
	private void addRemoteFilter(SQLFilter<? super T> fil) {
		if( fil != null) {
			// note we are  using a non-generic type here to force a foreign type of filter to be
			// included in the clause.
			SQLFilter f = fil;
			add(f,false); // remote end filter
		}
	}
		
	public static <T extends DataObject, BDO extends DataObject> Joiner<T,BDO> getRemoteFilter(Class<BDO> target,SQLFilter<T> fil, String join_field, Repository res, Repository remote_res){
		Joiner<T,BDO> result = new Joiner<T, BDO>(target);
		result.addFilter(new JoinerFilter(target,join_field, res, remote_res));
		result.addRemoteFilter(fil);
		return result;
	}
		/**
		 * 
		 * @param target type of target
		 * @param fil        Filter on remote table
		 * @param join_field String reference field
		 * @param res        Repository of target
		 * @param remote_res Repository of remote
		
		 * 
		 */
	public static <T extends DataObject, BDO extends DataObject> SQLFilter<BDO> getDestFilter(Class<BDO> target,SQLFilter<T> fil, String join_field, Repository res, Repository remote_res){
		Joiner<T, BDO> result = new Joiner(target);
		if( ! remote_res.getInfo(join_field).isUnique()){
			// use an EXISTS clause to select so as not to expand 
			// result set.
			result.addFilter( new BackJoinFilter<>(target,join_field, res, remote_res, fil));
		}else{

			// unique references/filters can also use a simple join. A unique reference
			// could be implemented in either direction equivalently
			// Ie there is ONLY ONE record in the remote table that references each parent 
			// record. This substitutes for the case where we could have also created a 
			// forward reference.
			
			
			result.addFilter(new JoinerFilter(target,join_field, remote_res, res));
			result.addRemoteFilter(fil);

		}
		return result;
	}
		/** Join to a fixed row of the remote table.
		* @param <T> remote table type
		* @param <BDO> target table type
		 * 
		 * @param target
		 * @param fil
		 * @param remote_res
		 * @param ref
		 * @return 
		 */
		public static <T extends DataObject, BDO extends DataObject> Joiner<T,BDO> joinFixedRef(Class<BDO> target, SQLFilter<? super T> fil, Repository remote_res,int ref){
			Joiner<T,BDO> res = new Joiner<T, BDO>(target);
			res.addFilter(new ConstJoinerFilter<T,BDO>(target, ref, remote_res));
			res.addRemoteFilter(fil);
			return res;
		}



}