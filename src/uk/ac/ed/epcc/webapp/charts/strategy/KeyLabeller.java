// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts.strategy;

import uk.ac.ed.epcc.webapp.AppContext;

@uk.ac.ed.epcc.webapp.Version("$Id: KeyLabeller.java,v 1.3 2014/09/15 14:30:13 spb Exp $")
/** A version of {@link HashLabeller} where the labels are generated from the Key value only
 * 
 * @author spb
 *
 * @param <T> type of object being mapped
 * @param <K> type of key generated
 */
public abstract class KeyLabeller<T,K> extends HashLabeller<T,K>{
	public KeyLabeller(AppContext c) {
		super(c);
	}
	public abstract Object getLabel(K key);
	@Override
	public final Object getLabel(K key, T o){
		return getLabel(key);
	}
	public final int getSetByKey(K key){
		return getSet(key,null);
	}
}