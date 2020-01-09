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
package uk.ac.ed.epcc.webapp.model.history;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;


/** SQLFilter for LinkHistory objects. 
 * 
 * Note that if
 * History table does not actually contain the Link fields then
 * the selection will use a join.
 * 
 * @author spb
 * @param <L> 
 * @param <R> 
 * @param <T> 
 * @param <H> 
 * @param <M> 
 *
 */


public class LinkHistorySQLFilter<L extends Indexed, R extends Indexed,
	T extends IndexedLinkManager.Link<L,R>,
    H extends DataObject & History<T>,
    M extends DataObjectFactory<H> & LinkHistoryHandler<L, R, T>>

	extends SQLAndFilter<H>{
	/**
	 * 
	 */
	private final M linkHistoryManager;

	public LinkHistorySQLFilter( M linkHistoryManager, L left, R right) {
		super(linkHistoryManager.getTarget());
		this.linkHistoryManager = linkHistoryManager;
		if( left != null && right != null){
			// should use normal HistoryFilter with peer id.
			throw new ConsistencyError("Both links specified for LinkHistoryFilter");
		}
		// need to select something
		String left_field=linkHistoryManager.getLinkManager().getLeftField();
		String right_field=linkHistoryManager.getLinkManager().getRightField();
		boolean has_left_field = linkHistoryManager.canLeftJoin();
		boolean has_right_field = linkHistoryManager.canRightJoin();
		if( left != null ){
			if(has_left_field){
				addFilter(new ReferenceFilter<>(this.linkHistoryManager,left_field,left));
			}else{
				// join with parent
				addFilter(((DataObjectFactory<H>)linkHistoryManager).getRemoteSQLFilter(linkHistoryManager.getLinkManager(),
						linkHistoryManager.getPeerName(),
						new ReferenceFilter<>(linkHistoryManager.getLinkManager(), left_field,left)));
			}
		}else if( right != null){
			if(has_right_field && right != null){
				addFilter(new ReferenceFilter<>(this.linkHistoryManager,right_field,right));
			}else{
				// join with parent 
				addFilter(((DataObjectFactory<H>)linkHistoryManager).getRemoteSQLFilter(linkHistoryManager.getLinkManager(),
						linkHistoryManager.getPeerName(),
						new ReferenceFilter<>(linkHistoryManager.getLinkManager(), right_field,right)));
			}
		}

	}

	
}