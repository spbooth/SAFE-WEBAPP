//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.lifecycle;

import java.util.*;

import uk.ac.ed.epcc.webapp.model.AbstractConstructedTargetList;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** A {@link LinkedList} of {@link AbstractAction}s that is populated
 * from configuration parameters.
 * 
 * The aim is to remove unnecessary code dependencies 
 * 
 * 
 * This looks in the parameter <b><em>tag</em>.<em>list-name</em></b> where
 * <b>tag</b> is the configuration tag for the parent factory.
 * This value is interpreted as a comma separated list of class tags and used to create the
 * listeners. The target classes for the listeners and the factory are checked for type conflicts.
 * 
 * @author spb
 * @param <T> 
 *
 */
public class ListenerList<T extends DataObject> extends AbstractConstructedTargetList<T,LifeCycleListener<T>> implements LifeCycleListener<T>{

	
	public ListenerList(Class<T> target,DataObjectFactory<T> factory,String list_name){
		super(target,factory,list_name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.AbstractList#getTemplate()
	 */
	@Override
	protected Class<? super LifeCycleListener> getTemplate() {
		return LifeCycleListener.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.ActionListener#allow(java.lang.Object, boolean)
	 */
	@Override
	public boolean allow(T target, boolean throw_reason) throws LifeCycleException {
		for(ActionListener<T> l  : this){
			if( ! l.allow(target, throw_reason)){
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.ActionListener#action(java.lang.Object)
	 */
	@Override
	public void action(T target) throws Exception {
		for(ActionListener<T> l  : this){
			l.action(target);
		}
	}
	@Override
	public Object getWarning(T target) {
		Set result = null;
		for( ActionListener l : this) {
			Object o = l.getWarning(target);
			if( o != null) {
				if( result == null ) {
					result = new LinkedHashSet<>();
				}
				if( o instanceof Collection) {
					result.addAll((Collection)o);
				}else {
					result.add(o);
				}
			}
		}
		if( result != null && result.size() == 1) {
			return result.iterator().next();
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.LifeCycleListener#prepare(java.lang.Object)
	 */
	@Override
	public void prepare(T target) throws Exception {
		for(ActionListener<T> l  : this){
			prepare(target);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.LifeCycleListener#abort(java.lang.Object)
	 */
	@Override
	public void abort(T target) {
		for(ActionListener<T> l  : this){
			abort(target);
		}
	}
	
}