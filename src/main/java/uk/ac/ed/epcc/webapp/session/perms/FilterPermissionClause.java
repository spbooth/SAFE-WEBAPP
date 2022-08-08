package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** {@link PermissionClause} that only depends on a filter on the target
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class FilterPermissionClause<T extends DataObject> implements PermissionClause<T> {
	
	

	public FilterPermissionClause(BaseFilter<T> fil,String name) {
		super();
		this.fil = fil;
		this.name = name;
	}

	private final String name;
	public String getName() {
		return name;
	}

	private final BaseFilter<T> fil;

	public BaseFilter<T> getFilter() {
		return fil;
	}

	@Override
	public Class<T> getTarget() {
		return fil.getTarget();
	}

	@Override
	public <X> X accept(PermissionVisitor<X, T> visitor) throws UnknownRelationshipException {
		return visitor.visitFilterPermissionClause(this);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fil == null) ? 0 : fil.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterPermissionClause other = (FilterPermissionClause) obj;
		if (fil == null) {
			if (other.fil != null)
				return false;
		} else if (!fil.equals(other.fil))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FilterPermissionClause[" + name + "]";
	}
}
