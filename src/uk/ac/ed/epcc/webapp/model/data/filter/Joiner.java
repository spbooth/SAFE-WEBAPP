// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.filter;

import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
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
@uk.ac.ed.epcc.webapp.Version("$Id: Joiner.java,v 1.3 2014/09/15 14:30:30 spb Exp $")

public class Joiner<T extends DataObject, BDO extends DataObject> extends SQLAndFilter<BDO> implements JoinFilter<BDO> {
		public Joiner(Class<? super BDO> target,SQLFilter<T> fil, String join_field, Repository res, Repository remote_res){
			this(target,fil,join_field,res,remote_res,true);
		}
		/**
		 * 
		 * @param fil        Filter on remote table
		 * @param join_field String reference field
		 * @param res        Repository of target
		 * @param remote_res Repository of remote
		 * @param target_references boolean true if target points to remote. false if remote points to target
		 */
		@SuppressWarnings("unchecked")
		public Joiner(Class<? super BDO> target,SQLFilter<T> fil, String join_field, Repository res, Repository remote_res,
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
}