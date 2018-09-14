//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.charts.strategy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Identified;


/**
 * HashLabeller implemetation class for classes that implement Labelled and builds the label
 * vector and the mappings as data is seen. Sets are ordered according to
 * the order the first data from that set was seen.
 * non String internal label types are supported to allow sorting by label but
 * these are converted to Strings
 * 
 * This class is often included by composition rather than directly extended
 * 
 * @author spb
 * @param <T> type of object being mapped
 * @param <K> type of key object generated
 */
public abstract class HashLabeller<T,K> extends AbstractContexed implements Labelled {

	Map<K,Number> map;

	Map<Object,Number> label_to_set;

	Vector<String> labels;

	int next_set = 0;

	@SuppressWarnings("unchecked")
	public HashLabeller(AppContext c) {
		super(c);
		map = new HashMap<K,Number>();
		label_to_set = new TreeMap<Object,Number>(new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				if( o1 instanceof Comparable){
					try{
						return ((Comparable)o1).compareTo(o2);
					}catch(Exception t){

					}
				}
				if( o1 instanceof Identified && o2 instanceof Identified){
					return ((Identified)o1).getIdentifier().compareTo(((Identified)o2).getIdentifier());
				}
				// default to comparing string rep.
				return o1.toString().compareTo(o2.toString());
			}
		});
		labels = new Vector<String>();
	}

	/**
	 * Create the Key object from the object being plotted. All objects with
	 * the same key map to the same plot, different keys may map to the same
	 * plot if desired.
	 * 
	 * @param r
	 *            Plot Object
	 * @return key Object
	 */
	public abstract K getKey(T r);

	/**
	 * generates the label object we could get this either from the key or
	 * the object itself. This will be converted to a string to generate
	 * the chart label
	 * 
	 * @param key
	 *            Key Object
	 * @param r
	 *            Plot Object
	 * @return label Object
	 */
	public abstract Object getLabel(K key, T r);

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.report.TimeChart.LabeledTransform#getLabels()
	 */
	public final Vector<String> getLabels() {

		return labels;
	}

	/** Get a permutation array for the sets depending on the
	 * natural ordering of the internal label objects.
	 * 
	 * @return
	 */
	public int[] getPerm(){
		int result[] = new int[label_to_set.size()];
		int i=0;
		for( Object lab : label_to_set.keySet()){
			Number n = label_to_set.get(lab);
			result[n.intValue()]=i++;
		}
		return result;
	}


	public final int getSet(T o) {
		K key = getKey(o);
		return getSet(key,o);
	}
	protected final int getSet(K key, T o) {
		Number set = map.get(key);
		if (set == null) {
			// found new value
			set = newSet(getLabel(key, o));
			map.put(key, set);
		}
		return set.intValue();
	}

	/**
	 * Generate the Set value corresponding to a label default
	 * implementation is to generate a new set for each new label seen. If
	 * the label has already been used the old set number is returned
	 * otherwise a new one is generates
	 * 
	 * @param lab
	 *            the label object to use for this o
	 * @return Number
	 */
	private Number newSet(Object lab) {

		Number set =  label_to_set.get(lab);
		if (set != null) {
			// we have already seen this label
			return set;
		}
		labels.add(lab.toString());
		set = new Integer(next_set++);
		label_to_set.put(lab, set);
		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.report.TimeChart.LabeledTransform#nSets()
	 */
	public final int nSets() {

		return labels.size();
	}

	
}