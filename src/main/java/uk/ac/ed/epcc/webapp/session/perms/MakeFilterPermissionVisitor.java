package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.NegatingFilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

public abstract class MakeFilterPermissionVisitor<R extends DataObject,T extends DataObject> implements PermissionVisitor<BaseFilter<R>, T>{
   public abstract DataObjectFactory<R> getFactory();
   
   @Override
	public BaseFilter<R> visitAndPermissionClause(AndPermissionClause<T> andc) throws UnknownRelationshipException {
		AndFilter<R> and = new AndFilter<R>(getFactory().getTarget());
		for(PermissionClause<T> s : andc) {
			if( ! and.isForced()) {
				and.addFilter(s.accept(this));
			}
		}
		return and;
	}
	@Override
	public BaseFilter<R> visitOrPermissionClause(OrPermissionClause<T> orc) throws UnknownRelationshipException {
		OrFilter<R> or = new OrFilter<>(getFactory().getTarget(), getFactory());
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
			getFactory().getContext().getService(LoggerService.class).getLogger(getClass()).error("Error negating filter",e);
			throw new UnknownRelationshipException("!");
		}
	}
	@Override
	public BaseFilter<R> visitBinaryPermissionClause(BinaryPermissionClause<T> b) {
		return new GenericBinaryFilter<R>(getFactory().getTarget(), b.getValue());
	}
	
}
