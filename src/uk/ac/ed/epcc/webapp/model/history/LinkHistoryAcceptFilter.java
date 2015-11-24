// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.history;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
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
@uk.ac.ed.epcc.webapp.Version("$Id: LinkHistoryAcceptFilter.java,v 1.9 2014/09/15 14:30:33 spb Exp $")

public class LinkHistoryAcceptFilter<L extends Indexed, R extends Indexed, T extends IndexedLinkManager.Link<L,R>> implements AcceptFilter<History<T>>{
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
	public LinkHistoryAcceptFilter(LinkHistoryHandler<L, R, T> linkHistoryHandler, L left, R right){
		this.linkHistoryHandler = linkHistoryHandler;
		this.left=left;
		this.right=right;
		cache = new HashMap<Integer,Boolean>();
		left_field=linkHistoryHandler.getLinkManager().getLeftField();
		right_field=linkHistoryHandler.getLinkManager().getRightField();
		has_left_field = this.linkHistoryHandler.canLeftJoin();
		has_right_field = this.linkHistoryHandler.canRightJoin();
	}
	public boolean accept(History<T> h) {
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
					this.linkHistoryHandler.getContext().error(e,"error getting peer in LinkHistoryFilter");
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends History<T>> vis)
			throws Exception {
		return vis.visitAcceptFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super History<T>> getTarget() {
		return History.class;
	}
	
}