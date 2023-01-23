package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
/** A {@link AndFilter} linked to a {@link DataObjectFactory}
 * this is intended as a base class for extended filters
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class DataObjectAndFilter<F extends DataObjectFactory<T>, T extends DataObject> extends AndFilter<T> {

	private final F fac;
	public DataObjectAndFilter(F fac) {
		super(fac.getTag());
		this.fac=fac;
	}
	
	public F getFactory(){
		return fac;
	}
}
