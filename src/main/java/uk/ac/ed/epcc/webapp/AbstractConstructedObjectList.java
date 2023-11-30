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
package uk.ac.ed.epcc.webapp;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.AbstractConstructedTargetList;

/** A {@link LinkedList} of objects that is populated
 * from configuration parameters.
 * Sub-classes will normally implement the same interface used as the template (with appropriate forwarding methods)
 * so the list can be used instead of a single implementing class.
 * 
 * The aim is to remove unnecessary code dependencies.
 * 
 * 
 * This looks in the parameter <b><em>tag</em>.list</b> where
 * <b>tag</b> is the configuration tag for this object.
 * This value is interpreted as a comma separated list of class tags and used to create the
 * list members.
 * 
 * 
 * @author spb
 * @param <L> type of constructed object
 * @see AbstractConstructedTargetList
 *
 */
public abstract class AbstractConstructedObjectList<L> extends LinkedList<L> implements Contexed{

	private final AppContext conn;
	public AbstractConstructedObjectList(AppContext conn, String tag){
		super();
		this.conn=conn;
		String list = conn.getExpandedProperty(tag+".list","");
		for(String action : list.split("\\s*,\\s*")){
			if( ! action.isEmpty()){
				if( action.equals(tag)) {
					getLogger().error("Explicit recursion in construted object list tag="+tag);
				}else {
					try{
						@SuppressWarnings("unchecked")
						L a = (L) conn.makeObject(getTemplate(), action);
						if( a == null){
							getLogger().error(action+" failed to resolve to "+getTemplate().getCanonicalName());
						}else{
							add(a);
						}
					}catch(Exception t){
						getLogger().error("Error making list for tag="+action,t);
					}
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
			logger=Logger.getLogger(conn,getClass());
		}
		return logger;
	}
}