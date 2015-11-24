// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
/** {@link SQLGroupMapper} to populate a Map from a SQL query where the key and data are single
 * target values.
 * 
 * @author spb
 * @param <K> type of key
 * @param <R> type of result
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MapMapper.java,v 1.6 2014/09/15 14:30:23 spb Exp $")

public class MapMapper<K,R> extends GeneralMapMapper<K,R>{
	private Class<? super K> key_type;
	public MapMapper(AppContext c,SQLValue<K> key,
			String key_name) {
		super(c);
		addKey(key,key_name);
		key_type = key.getTarget();
	}

	/** Simple constructor for a basic map between two values with no combine operation. 
	 * 
	 * @param c
	 * @param key
	 * @param key_name
	 * @param dat
	 * @param dat_name
	 */
	public MapMapper(AppContext c, SQLValue<K> key, String key_name, SQLValue<R> dat, String dat_name){
		this(c,key,key_name);
		addClause(dat, dat_name);
	}
	@Override
	@SuppressWarnings("unchecked")
	protected K makeKey(ResultSet rs) throws DataException{
	
		K key = (K) getTargetObject(0, rs);
		if( key == null ){
			// this can happen if the SQLValue can't map the DB value to the target space
		    // for strings we can define a default
			key=getNullKey();
		}
		return key;
	}
	@Override
	@SuppressWarnings("unchecked")
	protected R makeResult(ResultSet rs) throws DataException{
		R val  = (R) getTargetObject(1, rs);
		return val;
	}


	@SuppressWarnings("unchecked")
	protected K getNullKey(){
		if( key_type==String.class){
			return (K) "Unknown";
		}
		return null;
	}
	

}