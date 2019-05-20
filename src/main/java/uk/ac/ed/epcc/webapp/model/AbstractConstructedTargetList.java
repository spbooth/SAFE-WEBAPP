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
package uk.ac.ed.epcc.webapp.model;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** A {@link LinkedList} of {@link Targetted} objects that is populated
 * from configuration parameters.
 * 
 * The aim is to remove unnecessary code dependencies.
 * 
 * 
 * This looks in the parameter <b><em>tag</em>.<em>list-name</em></b> where
 * <b>tag</b> is the configuration tag for the parent factory.
 * This value is interpreted as a comma separated list of class tags and used to create the
 * listeners. The target classes for the listeners and the factory are checked for type conflicts.
 * 
 * 
 * @author spb
 * @param <T> type of data object
 * @param <L> type of constructed object
 *
 */
public abstract class AbstractConstructedTargetList<T extends DataObject,L extends Targetted> extends LinkedList<L> implements Contexed,Targetted<T>{

	private final AppContext conn;
	private final Class<T> target;
	public AbstractConstructedTargetList(DataObjectFactory<T> factory,String list_name){
		super();
		this.conn=factory.getContext();
		this.target=factory.getTarget();
		String list = conn.getExpandedProperty(factory.getConfigTag()+"."+list_name,"");
		for(String action : list.split("\\s*,\\s*")){
			if( ! action.isEmpty()){
				try{
				@SuppressWarnings("unchecked")
				L a = (L) conn.makeObject(getTemplate(), action);
				if( a == null){
					getLogger().error(action+" failed to resolve to "+getTemplate().getCanonicalName());
				}else if( ! a.getTarget().isAssignableFrom(factory.getTarget())){
					getLogger().error("Incompatible targets for list member "+a.getTarget().getCanonicalName()+" "+factory.getTarget().getCanonicalName());;
				}else{
					add(a);
				}
				}catch(Exception t){
					getLogger().error("Error making list for action="+action,t);
				}
			}
		}
	}
	
	protected abstract Class<? super L> getTemplate();
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}
	
	private Logger logger=null;
	public Logger getLogger(){
		if( logger == null){
			logger=conn.getService(LoggerService.class).getLogger(getClass());
		}
		return logger;
	}
	public final Class<T> getTarget(){
		return target;
	}
}