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
package uk.ac.ed.epcc.webapp.model.data;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Decorator around a IndexProducer that caches values.
 * The entire cache is hidden behind a SoftReference so if memory becomes tight the
 * cache is cleared.
 *  This has less overhead than hiding individual elements behind a reference but it
 *  means we clear the entire cache if memory becomes tight.
 * @author spb
 *
 * @param <T>
 */


public class CachedIndexedProducer<T extends Indexed>  implements IndexedProducer<T> {
    private IndexedProducer<T> maker;
    private SoftReference<Map<Integer,T>> ref=null;
    public CachedIndexedProducer(IndexedProducer<T> m){
    	maker = m;
    }
	@Override
	public T find(int id) throws DataException {
		Map<Integer,T> cache=getCache();
		T result;
		if( cache == null ){
			// default in case cache not found
			return maker.find(id);
		}
		result = cache.get(id);
		if( result == null ){
			result = maker.find(id);
			cache.put(id, result);
		}
		cache=null;
		return result;
	}
	/** Get a  the current cache. Note there is a race condition between
	 * making the reference and getting the result so this may still return null
	 * 
	 * @return
	 */
	private final Map<Integer,T> getCache(){
		if( ref == null || ref.get() == null ){
			ref = new SoftReference<>(new HashMap<Integer,T>());
		}
		return ref.get();
	}
    public void clear(){
    	if( ref == null ){
    		return;
    	}
        Map<Integer,T> cache = ref.get();
        if( cache==null){
        	ref=null;
        	return;
        }
        cache.clear();
        cache=null;
        ref.clear();
        ref=null;
        return;
    }
	
	@Override
	public IndexedReference<T> makeReference(T obj) {
		return maker.makeReference(obj);
	}
	@Override
	public IndexedReference<T> makeReference(int id) {
		return maker.makeReference(id);
	}
	@Override
	public boolean isMyReference(IndexedReference ref) {
		return maker.isMyReference(ref);
	}
	@Override
	public T find(Number o) {
		return maker.find(o);
	}
	@Override
	public Number getIndex(T value) {
		return maker.getIndex(value);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer#getID(uk.ac.ed.epcc.webapp.Indexed)
	 */
	@Override
	public String getID(T obj) {
		return maker.getID(obj);
	}
}