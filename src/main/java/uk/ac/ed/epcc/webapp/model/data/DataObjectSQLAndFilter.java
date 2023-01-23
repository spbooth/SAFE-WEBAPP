package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
/** A {@link AndFilter} linked to a {@link DataObjectFactory}
 * this is intended as a base class for extended filters
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class DataObjectSQLAndFilter<F extends DataObjectFactory<T>, T extends DataObject> extends SQLAndFilter<T> {

	private final F fac;
	public DataObjectSQLAndFilter(F fac) {
		super(fac.getTag());
		this.fac=fac;
	}
	
	public F getFactory(){
		return fac;
	}
}
