package uk.ac.ed.epcc.webapp.model.data.expr;

/**
 * Mapper translates an object into a Table key.
 * 
 * Used when populating a table from a ResultSet when you want to map
 * multiple ResultSet entries to the same row or otherwise customise the
 * mapping of ResultSet entries to keys.
 * 
 * @author spb
 * @param <R>
 *            type of row key
 * 
 */
public interface Mapper<R> {
	public abstract R map(Object key);
}