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

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager;


/** AcceptFilter selecting based on the Link peers.
 * 
 * This caches the result based on the peerID to save on multiple lookups.
 * 
 * @author spb
 * @param <L> 
 * @param <R> 
 * @param <T> 
 *
 */


public class LinkHistoryAcceptFilter<L extends Indexed, R extends Indexed, T extends IndexedLinkManager.Link<L,R>,H extends History<T>> extends AbstractAcceptFilter<H> implements AcceptFilter<H>{
	/**
	 * 
	 */
	private final LinkHistoryHandler<L, R, T> linkHistoryHandler;
	L left;
	R right;
	String left_field;
	String right_field;
	boolean has_left_field;
	boolean has_right_field;
	Map<Integer,Boolean> cache;
	public LinkHistoryAcceptFilter(Class<H> target,LinkHistoryHandler<L, R, T> linkHistoryHandler, L left, R right){
		super(target);
		this.linkHistoryHandler = linkHistoryHandler;
		this.left=left;
		this.right=right;
		cache = new HashMap<Integer,Boolean>();
		left_field=linkHistoryHandler.getLinkManager().getLeftField();
		right_field=linkHistoryHandler.getLinkManager().getRightField();
		has_left_field = this.linkHistoryHandler.canLeftJoin();
		has_right_field = this.linkHistoryHandler.canRightJoin();
	}
	public boolean accept(H h) {
		if( left != null ){
			if( has_left_field ){
				if( ! h.matchIntegerProperty(left_field, left.getID())){
					return false;
				}
			}else{
				Integer key = new Integer(h.getPeerID());

				Boolean b = cache.get(key);
				if( b != null ){

					return b.booleanValue();
				}
				boolean ok;
				try {
					T l = h.getPeer();
					ok = l.isLeftPeer(left);
				} catch (DataException e) {
					this.linkHistoryHandler.getContext().getService(LoggerService.class).getLogger(getClass()).error("error getting peer in LinkHistoryFilter",e);
					ok=false;;
				}
				cache.put(key, new Boolean(ok));
				return ok;
			}
		}
		if( right != null ){
			if( has_right_field ){
				if( ! h.matchIntegerProperty(right_field, right.getID())){
					return false;
				}
			}else{
				Integer key = new Integer(h.getPeerID());
				Boolean b = cache.get(key);
				if( b != null ){
					return b.booleanValue();
				}
				boolean ok;
				try {
					T l = h.getPeer();
					ok = l.isRightPeer(right);
				} catch (DataException e) {
					this.linkHistoryHandler.getContext().error(e,"error getting peer in LinkHistoryFilter");
					ok=false;;
				}
				cache.put(key, new Boolean(ok));
				return ok;

			}
		}
		return true;
	}
	
	
}