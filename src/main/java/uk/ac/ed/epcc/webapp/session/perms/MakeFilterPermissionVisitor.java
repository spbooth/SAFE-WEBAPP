package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** common superclass for {@link PermissionVisitor}s that generate {@link BaseFilter}s
 * 
 * this handles filter combinations and true/false conditions that are the same
 * for all such visitors
 * 
 * @author Stephen Booth
 *
 * @param <R>
 * @param <T>
 */
public abstract class MakeFilterPermissionVisitor<R extends DataObject,T extends DataObject> implements PermissionVisitor<BaseFilter<R>, T>{
   public abstract DataObjectFactory<R> getFactory();
   
   @Override
	public BaseFilter<R> visitAndPermissionClause(AndPermissionClause<T> andc) throws UnknownRelationshipException {
		AndFilter<R> and = getFactory().getAndFilter();
		for(PermissionClause<T> s : andc) {
			if( ! and.isForced()) {
				and.addFilter(s.accept(this));
			}
		}
		return and;
	}
	@Override
	public BaseFilter<R> visitOrPermissionClause(OrPermissionClause<T> orc) throws UnknownRelationshipException {
		OrFilter<R> or = getFactory().getOrFilter();
		for( PermissionClause<T>  s  : orc){
			if( ! or.isForced()) { // combinations shortcut if forced
				or.addFilter(s.accept(this));
			}
		}
		return or;
	}
	@Override
	public BaseFilter<R> visitNegatingClause(NegatingClause<T> n) throws UnknownRelationshipException {
		NegatingFilterVisitor<R> nv = new NegatingFilterVisitor<>(getFactory());
		try {
			return n.getInner().accept(this).acceptVisitor(nv);
		} catch (UnknownRelationshipException e) {
			throw e;
		} catch (Exception e) {
			Logger.getLogger(getFactory().getContext(),getClass()).error("Error negating filter",e);
			throw new UnknownRelationshipException("!");
		}
	}
	@Override
	public BaseFilter<R> visitBinaryPermissionClause(BinaryPermissionClause<T> b) {
		return new GenericBinaryFilter<R>( b.getValue());
	}
	
}
