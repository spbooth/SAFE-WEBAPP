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

import uk.ac.ed.epcc.webapp.AppContext;


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