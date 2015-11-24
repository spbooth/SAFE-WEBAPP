// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * DataCache implements caches round queries that will be repeated multiple
 * times, e.g. username lookups. It caches the results so later identical
 * queries do no have to go to the DB Concrete subclasses will usually be inner
 * classes of a Factory class
 * 
 * @author spb
 * @param <K> type of key
 * @param <V> type of data
 * 
 */
public abstract class DataCache<K,V> {
	private Map<K,V> cache;

	private Set<K> bad_keys;

	
	public DataCache(){
		this(null);
	}
	public DataCache(Map<K,V> initial_data) {
		if( initial_data == null ){
			cache = new HashMap<K, V>();
		}else{
			cache = new HashMap<K,V>(initial_data);
		}
		bad_keys = new HashSet<K>();
	}

	/**
	 * The underlying query method.
	 * 
	 * @param key
	 * @return Object from cache
	 * @throws DataException
	 */
	protected abstract V find(K key) throws DataException;

	/** explicitly load data into the cache
	 * 
	 * @param key
	 * @param value
	 */
	public void save(K key, V value){
		cache.put(key, value);
	}
	/**
	 * The public query method. It looks up a result object and returns it. If
	 * the lookup fails to find a value it return null.
	 * 
	 * @param key
	 * @return found Object or null for bad key
	 */
	public V get(K key)  {
		V result;

		result = cache.get(key);
		if (result == null) {
			if (!bad_keys.contains(key)) {
				try {
					result = find(key);
					cache.put(key, result);
					if( result == null ){
						bad_keys.add(key);
					}
				} catch (DataException e) {
					bad_keys.add(key);
				}
			}
		}
		return result;
	}

	public int nBadKeys() {
		return bad_keys.size();
	}
	public Set<K> badKeys(){
		return new HashSet<K>(bad_keys);
	}
	public void clear(){
		cache.clear();
		bad_keys.clear();
	}
}