package uk.ac.ed.epcc.webapp.content;

import java.util.Set;
/** An extended {@link Labeller} that generates a {@link Set} of generated values.
 * 
 * This is intended for labels that form a sequence and all elements of the sequence
 * should be presented even though no data has been generated. Upper and lower bounds of
 * the sequence could still depend on the data generated.
 * 
 * It could also be used for labels from a fixed set where all members should be
 * presented.
 * 
 * Normally when the labels form a sequence the result type R should implement
 * {@link Comparable} to reflect the ordering of the sequence.
 * 
 * @author Stephen Booth
 *
 * @param <T>
 * @param <R>
 */
public interface EnumeratingLabeller<T,R> extends Labeller<T, R> {

	/** Generate a  {@link Set} of result values that includes
	 * all results that have been returned from {@link #getLabel(uk.ac.ed.epcc.webapp.AppContext, Object)}
	 * by this instance. It may also contain additional values that should be included in the presented data
	 * to complete a dataset.
	 * 
	 * @return
	 */
	public Set<R> getRange();
}
