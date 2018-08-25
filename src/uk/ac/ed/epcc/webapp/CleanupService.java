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

import java.util.Iterator;
import java.util.LinkedHashSet;

/** A {@link CleanupService} 
 * implements deferred actions to be performed later (usually when the {@link AppContext}
 * is closed.
 * 
 * When running from a servlet this usually triggers the close to be performed in a background 
 * thread so as not to delay the browser.
 * 
 * @author spb
 *
 */
public class CleanupService extends AbstractContexed implements AppContextService<CleanupService>{

	private final LinkedHashSet<Runnable> actions;
	
	public CleanupService(AppContext conn){
		super(conn);
		this.actions=new LinkedHashSet<Runnable>();
	}
	/** Add a {@link Runnable} to the set of actions performed on cleanup.
	 * The runnable object should implement {@link #hashCode()} and {@link #equals(Object)} so identical 
	 * actions are not run twice.
	 * The class should also be annotated with {@link PreRequisiteService} if they use an {@link AppContextService}.
	 * 
	 * @param r
	 */
	public void add(Runnable r){
		Class<? extends Runnable> clazz = r.getClass();
		PreRequisiteService prs =  clazz.getAnnotation(PreRequisiteService.class);
		if( prs != null){
			for(Class x : prs.value()){
				conn.getService(x);
			}
		}
		actions.add(r);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public final void cleanup() {
		action();
	}
	
	/** action to be performed.
	 * 
	 */
	public synchronized  void action(){

		for(Iterator<Runnable> it = actions.iterator() ; it.hasNext(); ){
			Runnable r = it.next();
			r.run();
			it.remove();
		}
	}

	public boolean hasActions(){
		return ! actions.isEmpty();
	}
	public void reset(){
		actions.clear();
	}
	public LinkedHashSet<Runnable> getActions(){
		return (LinkedHashSet<Runnable>) actions.clone();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public final Class<? super CleanupService> getType() {
		return CleanupService.class;
	}
	

}
