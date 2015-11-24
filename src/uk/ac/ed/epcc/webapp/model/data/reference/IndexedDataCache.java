// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataCache;
/** DataCache for Indexed types.
 * Entries can also be located by integer id. 
 * 
 * @author spb
 *
 * @param <K> key used to lookup target
 * @param <D> target type
 */
public abstract class IndexedDataCache<K,D extends Indexed> extends DataCache<K,D> {
    private Map<IndexedReference<? extends D>,D> index_map = new HashMap<IndexedReference<? extends D>,D>();
    private AppContext c;
    public IndexedDataCache(AppContext c){
    	this.c=c;
    }
	@Override
	protected final D find(K key) throws DataException {
		D res = findIndexed(key);
		if( res != null ){
			index_map.put(getReference(res),res);
		}
		return res;
	}
    
	public D find(IndexedReference<D> id){
		if( id == null || id.isNull()){
			return null;
		}
		D res = index_map.get(id);
		if( res == null){
			res = id.getIndexed(c);
			if( res != null ){
			  index_map.put(id, res);
			}
		}
		return res;
	}
	protected abstract D findIndexed(K key) throws DataException;
	protected abstract IndexedReference<? extends D> getReference(D dat);
}