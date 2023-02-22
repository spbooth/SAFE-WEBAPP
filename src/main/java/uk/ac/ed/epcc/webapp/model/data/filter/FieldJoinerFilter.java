package uk.ac.ed.epcc.webapp.model.data.filter;
/** An extension to the {@link JoinerFilter} that links to a different field
 * from the default primary key.
 * 
 * Our normal convention is to join tables using an integer reference to the
 * primary key but this can be used when there is already a common field with a
 * unique index that could be used without introducing a redundant reference. 
 * 
 * If there is both a reference and a common field the reference should be used.
 * @author Stephen Booth
 *
 */

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** A {@link JoinerFilter} that references a remote field other than the primary key
 * 
 * @author Stephen Booth
 *
 * @param <TARGET>
 * @param <REMOTE>
 */
public class FieldJoinerFilter<TARGET extends DataObject, REMOTE extends DataObject> extends JoinerFilter<TARGET,REMOTE> {

	private final String remote_field;
	public FieldJoinerFilter( String join_field, Repository res,String remote_field, Repository remote_res) {
		super( join_field, res, remote_res);
		this.remote_field = remote_field;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((remote_field == null) ? 0 : remote_field.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldJoinerFilter other = (FieldJoinerFilter) obj;
		if (remote_field == null) {
			if (other.remote_field != null)
				return false;
		} else if (!remote_field.equals(other.remote_field))
			return false;
		return true;
	}

	@Override
	public void addLinkClause(StringBuilder join) {
		join.append("(");
		
 		FieldInfo info = res.getInfo(join_field);
		info.addName(join, true, true);
     	join.append(" = ");
     	FieldInfo info2 = remote_res.getInfo(remote_field);
     	info2.addName(join, true, true);
     	join.append(")");
	}

	
}
