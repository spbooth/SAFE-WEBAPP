// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.content;

/**
 * @author spb
 *
 * @param <C>
 * @param <R>
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface TableFormatPolicy<C, R> {

	public abstract void add(Table<C, R> t);

	public abstract void addColumn(Table<C, R> t, C key);

}