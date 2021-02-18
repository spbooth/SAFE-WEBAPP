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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

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
 * @param <T> type of target
 *
 */
public class ActionList<T extends DataObject> extends AbstractConstructedTargetList<T,ActionListener<T>> implements ActionListener<T>  {

	
	public ActionList(DataObjectFactory<T> factory,String list_name){
		super(factory,list_name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.AbstractList#getTemplate()
	 */
	@Override
	protected Class<? super ActionListener> getTemplate() {
		return ActionListener.class;
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
				result.add(o);
			}
		}
		if( result != null && result.size() == 1) {
			return result.iterator().next();
		}
		return result;
	}
	
}