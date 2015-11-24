package uk.ac.ed.epcc.webapp.model.data.filter;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Like {@link ReferenceFilter} but also matches objects
 * where the reference field is null
 * 
 * @author spb
 *
 * @param <T>
 * @param <P>
 */
public class WildCardReferenceFilter<T extends DataObject,P extends Indexed> extends OrFilter<T> {

	public WildCardReferenceFilter(Class<? super T> target,Repository res, String field, P peer){
		super(target);
		addFilter(new NullFieldFilter<T>(target,res, field, true));
		if( peer != null){
			addFilter(new SQLValueFilter<T>(target,res, field, peer.getID()));
		}
	}
}
